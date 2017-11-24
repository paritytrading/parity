package com.paritytrading.parity.fix;

import static com.paritytrading.philadelphia.fix44.FIX44Enumerations.*;
import static com.paritytrading.philadelphia.fix44.FIX44MsgTypes.*;
import static com.paritytrading.philadelphia.fix44.FIX44Tags.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.nassau.soupbintcp.SoupBinTCP;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClient;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClientStatusListener;
import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.net.poe.POEClientListener;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.philadelphia.FIXConfig;
import com.paritytrading.philadelphia.FIXField;
import com.paritytrading.philadelphia.FIXMessage;
import com.paritytrading.philadelphia.FIXMessageListener;
import com.paritytrading.philadelphia.FIXSession;
import com.paritytrading.philadelphia.FIXStatusListener;
import com.paritytrading.philadelphia.FIXValue;
import com.paritytrading.philadelphia.FIXValueFormatException;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class Session implements Closeable {

    private static final String UNKNOWN_ORDER_ID = "NONE";

    private static SoupBinTCP.LoginRequest loginRequest = new SoupBinTCP.LoginRequest();

    private static POE.EnterOrder enterOrder = new POE.EnterOrder();

    private static POE.CancelOrder cancelOrder = new POE.CancelOrder();

    private static FIXMessage txMessage = new FIXMessage(64, 64);

    private static ByteBuffer txBuffer = ByteBuffer.allocateDirect(POE.MAX_INBOUND_MESSAGE_LENGTH);

    private long nextOrderEntryId;

    private Orders orders;

    private FIXSession fix;

    private SoupBinTCPClient orderEntry;

    private Instruments instruments;

    public Session(OrderEntryFactory orderEntry, SocketChannel fix,
            FIXConfig config, Instruments instruments) throws IOException {
        this.nextOrderEntryId = 1;

        this.orders = new Orders();

        OrderEntryListener orderEntryListener = new OrderEntryListener();

        this.orderEntry = orderEntry.create(orderEntryListener, orderEntryListener);

        FIXListener fixListener = new FIXListener();

        this.fix = new FIXSession(fix, config, fixListener, fixListener);

        this.instruments = instruments;
    }

    @Override
    public void close() throws IOException {
        fix.close();
        orderEntry.close();
    }

    public FIXSession getFIX() {
        return fix;
    }

    public SoupBinTCPClient getOrderEntry() {
        return orderEntry;
    }

    private void send(POE.InboundMessage message) throws IOException {
        txBuffer.clear();
        message.put(txBuffer);
        txBuffer.flip();

        orderEntry.send(txBuffer);
    }

    private class FIXListener implements FIXMessageListener, FIXStatusListener {

        @Override
        public void message(FIXMessage message) throws IOException {
            FIXValue msgType = message.getMsgType();

            if (msgType.length() != 1) {
                invalidMsgType(message);
                return;
            }

            switch (msgType.asChar()) {
            case NewOrderSingle:
                newOrderSingle(message);
                break;
            case OrderCancelReplaceRequest:
                orderCancel(message, OrderCancelReplaceRequest);
                break;
            case OrderCancelRequest:
                orderCancel(message, OrderCancelRequest);
                break;
            default:
                invalidMsgType(message);
                break;
            }
        }

        private void newOrderSingle(FIXMessage message) throws IOException {
            FIXValue clOrdIdValue  = null;
            FIXValue accountValue  = null;
            FIXValue sideValue     = null;
            FIXValue symbolValue   = null;
            FIXValue orderQtyValue = null;
            FIXValue priceValue    = null;

            for (int i = 0; i < message.getFieldCount(); i++) {
                FIXField field = message.getField(i);

                switch (field.getTag()) {
                case ClOrdID:
                    clOrdIdValue = field.getValue();
                    break;
                case Account:
                    accountValue = field.getValue();
                    break;
                case Side:
                    sideValue = field.getValue();
                    break;
                case Symbol:
                    symbolValue = field.getValue();
                    break;
                case OrderQty:
                    orderQtyValue = field.getValue();
                    break;
                case Price:
                    priceValue = field.getValue();
                    break;
                }
            }

            if (clOrdIdValue == null) {
                requiredTagMissing(message, "ClOrdID(11) missing");
                return;
            }

            if (sideValue == null) {
                requiredTagMissing(message, "Side(54) missing");
                return;
            }

            if (symbolValue == null) {
                requiredTagMissing(message, "Symbol(55) missing");
                return;
            }

            if (orderQtyValue == null) {
                requiredTagMissing(message, "OrderQty(38) missing");
                return;
            }

            if (priceValue == null) {
                requiredTagMissing(message, "Price(44) missing");
                return;
            }

            long orderEntryId = nextOrderEntryId++;

            ASCII.putLongLeft(enterOrder.orderId, orderEntryId);

            String clOrdId = clOrdIdValue.asString();

            String account = null;

            if (accountValue != null)
                account = accountValue.asString();

            char side = sideValue.asChar();

            switch (side) {
            case SideValues.Buy:
                enterOrder.side = POE.BUY;
                break;
            case SideValues.Sell:
                enterOrder.side = POE.SELL;
                break;
            default:
                valueIsIncorrect(message, "Unknown value in Side(54)");
                return;
            }

            String symbol = symbolValue.asString();

            try {
                enterOrder.instrument = ASCII.packLong(symbol);
            } catch (IllegalArgumentException e) {
                incorrectDataFormatForValue(message, "Expected 'String' in Symbol(55)");
                return;
            }

            double orderQty = 0.0;

            try {
                orderQty = orderQtyValue.asFloat();
            } catch (FIXValueFormatException e) {
                incorrectDataFormatForValue(message, "Expected 'float' in OrderQty(38)");
                return;
            }

            if (orderQty < 0.0) {
                sendOrderRejected(clOrdId, OrdRejReasonValues.IncorrectQuantity, account,
                        symbol, side, orderQty);
                return;
            }

            Instrument config = instruments.get(enterOrder.instrument);
            if (config == null) {
                sendOrderRejected(clOrdId, OrdRejReasonValues.UnknownSymbol, account,
                        symbol, side, orderQty);
                return;
            }

            enterOrder.quantity = (long)(orderQty * config.getSizeFactor());

            double price = 0.0;

            try {
                price = priceValue.asFloat();
            } catch (FIXValueFormatException e) {
                incorrectDataFormatForValue(message, "Expected 'float' in Price(44)");
                return;
            }

            if (price < 0.0) {
                sendOrderRejected(clOrdId, OrdRejReasonValues.BrokerCredit, account,
                        symbol, side, orderQty);
                return;
            }

            Order order = orders.findByClOrdID(clOrdId);
            if (order != null) {
                sendOrderRejected(order, OrdRejReasonValues.DuplicateOrder);
                return;
            }

            enterOrder.price = (long)(price * config.getPriceFactor());

            orders.add(new Order(orderEntryId, clOrdId, account, side, symbol, orderQty));

            send(enterOrder);
        }

        private void orderCancel(FIXMessage message, char msgType) throws IOException {
            FIXValue clOrdIdValue     = null;
            FIXValue origClOrdIdValue = null;
            FIXValue orderQtyValue    = null;

            for (int i = 0; i < message.getFieldCount(); i++) {
                FIXField field = message.getField(i);

                switch (field.getTag()) {
                case ClOrdID:
                    clOrdIdValue = field.getValue();
                    break;
                case OrigClOrdID:
                    origClOrdIdValue = field.getValue();
                    break;
                case OrderQty:
                    orderQtyValue = field.getValue();
                    break;
                }
            }

            if (origClOrdIdValue == null) {
                requiredTagMissing(message, "OrigClOrdID(41) missing");
                return;
            }

            if (clOrdIdValue == null) {
                requiredTagMissing(message, "ClOrdID(11) missing");
                return;
            }

            if (msgType == OrderCancelReplaceRequest && orderQtyValue == null) {
                requiredTagMissing(message, "OrderQty(38) missing");
                return;
            }

            char cxlRejResponseTo = CxlRejResponseToValues.OrderCancelRequest;

            if (msgType == OrderCancelReplaceRequest)
                cxlRejResponseTo = CxlRejResponseToValues.OrderCancel;

            String origClOrdId = origClOrdIdValue.asString();
            String clOrdId     = clOrdIdValue.asString();

            Order order = orders.findByClOrdID(origClOrdId);
            if (order == null) {
                sendOrderCancelReject(clOrdId, origClOrdId, cxlRejResponseTo);

                return;
            } else if (order.isInPendingStatus()) {
                sendOrderCancelReject(order, clOrdId, cxlRejResponseTo,
                        CxlRejReasonValues.OrderAlreadyInPendingStatus);

                return;
            }

            if (orders.findByClOrdID(clOrdId) != null) {
                sendOrderCancelReject(order, clOrdId, cxlRejResponseTo,
                        CxlRejReasonValues.DuplicateClOrdID);
                return;
            }

            double orderQty = 0.0;

            if (msgType == OrderCancelReplaceRequest) {
                try {
                    orderQty = orderQtyValue.asFloat();
                } catch (FIXValueFormatException e) {
                    incorrectDataFormatForValue(message, "Expected 'float' in OrderQty(38)");
                    return;
                }
            }

            double qty = Math.max(orderQty - order.getCumQty(), 0);

            Instrument config = instruments.get(order.getSymbol());

            order.setNextClOrdID(clOrdId);
            order.setCxlRejResponseTo(cxlRejResponseTo);

            ASCII.putLongLeft(cancelOrder.orderId, order.getOrderEntryID());
            cancelOrder.quantity = (long)(qty * config.getSizeFactor());

            send(cancelOrder);

            char execType  = ExecTypeValues.PendingCancel;
            char ordStatus = OrdStatusValues.PendingCancel;

            if (msgType == OrderCancelReplaceRequest) {
                execType  = ExecTypeValues.PendingReplace;
                ordStatus = OrdStatusValues.PendingReplace;
            }

            sendOrderCancelAcknowledgement(order, execType, ordStatus);
        }

        @Override
        public void close(FIXSession session, String message) throws IOException {
            orderEntry.close();
        }

        @Override
        public void heartbeatTimeout(FIXSession session) throws IOException {
        }

        @Override
        public void logon(FIXSession session, FIXMessage message) throws IOException {
            FIXValue username = message.findField(Username);
            if (username == null) {
                requiredTagMissing(message, "Username(553) missing");
                return;
            }

            FIXValue password = message.findField(Password);
            if (password == null) {
                requiredTagMissing(message, "Password(554) missing");
                return;
            }

            fix.updateCompID(message);

            ASCII.putLeft(loginRequest.username, username.asString());
            ASCII.putLeft(loginRequest.password, password.asString());
            ASCII.putRight(loginRequest.requestedSession, "");
            ASCII.putLongRight(loginRequest.requestedSequenceNumber, 0);

            orderEntry.login(loginRequest);
        }

        @Override
        public void logout(FIXSession session, FIXMessage message) throws IOException {
            fix.sendLogout();

            orderEntry.logout();
        }

        @Override
        public void reject(FIXSession session, FIXMessage message) {
        }

        @Override
        public void sequenceReset(FIXSession session) {
        }

        @Override
        public void tooLowMsgSeqNum(FIXSession session, long receivedMsgSeqNum, long expectedMsgSeqNum) {
        }

    }

    private void invalidMsgType(FIXMessage message) throws IOException {
        fix.sendReject(message.getMsgSeqNum(), SessionRejectReasonValues.InvalidMsgType,
                "Invalid MsgType(35)");
    }

    private void requiredTagMissing(FIXMessage message, String text) throws IOException {
        fix.sendReject(message.getMsgSeqNum(), SessionRejectReasonValues.RequiredTagMissing, text);
    }

    private void valueIsIncorrect(FIXMessage message, String text) throws IOException {
        fix.sendReject(message.getMsgSeqNum(), SessionRejectReasonValues.ValueIsIncorrect, text);
    }

    private void incorrectDataFormatForValue(FIXMessage message, String text) throws IOException {
        fix.sendReject(message.getMsgSeqNum(), SessionRejectReasonValues.IncorrectDataFormatForValue, text);
    }

    private void sendOrderCancelReject(String clOrdId, String origClOrdId, char cxlRejResponseTo) throws IOException {
        fix.prepare(txMessage, OrderCancelReject);

        txMessage.addField(OrderID).setString(UNKNOWN_ORDER_ID);
        txMessage.addField(ClOrdID).setString(clOrdId);
        txMessage.addField(OrigClOrdID).setString(origClOrdId);
        txMessage.addField(OrdStatus).setChar(OrdStatusValues.Rejected);
        txMessage.addField(CxlRejResponseTo).setChar(cxlRejResponseTo);
        txMessage.addField(CxlRejReason).setInt(CxlRejReasonValues.UnknownOrder);

        fix.send(txMessage);
    }

    private void sendOrderCancelReject(Order order, String clOrdId, char cxlRejResponseTo,
            int cxlRejReason) throws IOException {
        fix.prepare(txMessage, OrderCancelReject);

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(clOrdId);
        txMessage.addField(OrigClOrdID).setString(order.getClOrdID());
        txMessage.addField(OrdStatus).setChar(OrdStatusValues.Rejected);
        txMessage.addField(CxlRejResponseTo).setChar(cxlRejResponseTo);
        txMessage.addField(CxlRejReason).setInt(cxlRejReason);

        fix.send(txMessage);
    }

    private void sendOrderCancelReject(Order order) throws IOException {
        fix.prepare(txMessage, OrderCancelReject);

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getNextClOrdID());
        txMessage.addField(OrigClOrdID).setString(order.getClOrdID());
        txMessage.addField(OrdStatus).setChar(OrdStatusValues.Filled);
        txMessage.addField(CxlRejResponseTo).setChar(order.getCxlRejResponseTo());
        txMessage.addField(CxlRejReason).setInt(CxlRejReasonValues.TooLateToCancel);

        fix.send(txMessage);
    }

    private void sendOrderAccepted(Order order) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        String symbol = order.getSymbol();

        Instrument config = instruments.get(symbol);

        int priceFractionDigits = config.getPriceFractionDigits();
        int sizeFractionDigits  = config.getSizeFractionDigits();

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(ExecTypeValues.New);
        txMessage.addField(OrdStatus).setChar(order.getOrdStatus());

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(symbol);
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setFloat(order.getOrderQty(), sizeFractionDigits);
        txMessage.addField(LeavesQty).setFloat(order.getLeavesQty(), sizeFractionDigits);
        txMessage.addField(CumQty).setFloat(order.getCumQty(), sizeFractionDigits);
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), priceFractionDigits);

        fix.send(txMessage);
    }

    private void sendOrderRejected(Order order, int ordRejReason) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        String symbol = order.getSymbol();

        Instrument config = instruments.get(symbol);

        int priceFractionDigits = config.getPriceFractionDigits();
        int sizeFractionDigits  = config.getSizeFractionDigits();

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(ExecTypeValues.Rejected);
        txMessage.addField(OrdStatus).setChar(OrdStatusValues.Rejected);
        txMessage.addField(OrdRejReason).setInt(ordRejReason);

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(symbol);
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setFloat(order.getOrderQty(), sizeFractionDigits);
        txMessage.addField(LeavesQty).setFloat(0.0, sizeFractionDigits);
        txMessage.addField(CumQty).setFloat(order.getCumQty(), sizeFractionDigits);
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), priceFractionDigits);

        fix.send(txMessage);
    }

    private void sendOrderRejected(String clOrdId, int ordRejReason,
            String account, String symbol, char side, double orderQty) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        Instrument config = instruments.get(symbol);

        int priceFractionDigits = 0;
        int sizeFractionDigits  = 0;

        if (config != null) {
            priceFractionDigits = config.getPriceFractionDigits();
            sizeFractionDigits  = config.getSizeFractionDigits();
        }

        txMessage.addField(OrderID).setString(UNKNOWN_ORDER_ID);
        txMessage.addField(ClOrdID).setString(clOrdId);
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(ExecTypeValues.Rejected);
        txMessage.addField(OrdStatus).setChar(OrdStatusValues.Rejected);
        txMessage.addField(OrdRejReason).setInt(ordRejReason);

        if (account != null)
            txMessage.addField(Account).setString(account);

        txMessage.addField(Symbol).setString(symbol);
        txMessage.addField(Side).setChar(side);
        txMessage.addField(OrderQty).setFloat(orderQty, sizeFractionDigits);
        txMessage.addField(LeavesQty).setFloat(0.0, sizeFractionDigits);
        txMessage.addField(CumQty).setFloat(0.0, sizeFractionDigits);
        txMessage.addField(AvgPx).setFloat(0.0, priceFractionDigits);

        fix.send(txMessage);
    }

    private void sendOrderExecuted(Order order, double lastQty, double lastPx, Instrument config) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        int priceFractionDigits = config.getPriceFractionDigits();
        int sizeFractionDigits  = config.getSizeFractionDigits();

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(ExecTypeValues.Trade);
        txMessage.addField(OrdStatus).setChar(order.getOrdStatus());

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(order.getSymbol());
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setFloat(order.getOrderQty(), sizeFractionDigits);
        txMessage.addField(LastQty).setFloat(lastQty, sizeFractionDigits);
        txMessage.addField(LastPx).setFloat(lastPx, priceFractionDigits);
        txMessage.addField(LeavesQty).setFloat(order.getLeavesQty(), sizeFractionDigits);
        txMessage.addField(CumQty).setFloat(order.getCumQty(), sizeFractionDigits);
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), priceFractionDigits);

        fix.send(txMessage);
    }

    private void sendOrderCancelAcknowledgement(Order order, char execType, char ordStatus) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        String symbol = order.getSymbol();

        Instrument config = instruments.get(symbol);

        int priceFractionDigits = config.getPriceFractionDigits();
        int sizeFractionDigits  = config.getSizeFractionDigits();

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getNextClOrdID());
        txMessage.addField(OrigClOrdID).setString(order.getClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(execType);
        txMessage.addField(OrdStatus).setChar(ordStatus);

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(symbol);
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setFloat(order.getOrderQty(), sizeFractionDigits);
        txMessage.addField(LeavesQty).setFloat(order.getLeavesQty(), sizeFractionDigits);
        txMessage.addField(CumQty).setFloat(order.getCumQty(), sizeFractionDigits);
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), priceFractionDigits);

        fix.send(txMessage);
    }

    private void sendOrderCanceled(Order order, Instrument config) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        int priceFractionDigits = config.getPriceFractionDigits();
        int sizeFractionDigits  = config.getSizeFractionDigits();

        char execType  = ExecTypeValues.Canceled;
        char ordStatus = OrdStatusValues.Canceled;

        if (order.getLeavesQty() > 0) {
            execType  = ExecTypeValues.Replaced;
            ordStatus = order.getOrdStatus();
        }

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getClOrdID());
        txMessage.addField(OrigClOrdID).setString(order.getOrigClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(execType);
        txMessage.addField(OrdStatus).setChar(ordStatus);

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(order.getSymbol());
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setFloat(order.getOrderQty(), sizeFractionDigits);
        txMessage.addField(LeavesQty).setFloat(order.getLeavesQty(), sizeFractionDigits);
        txMessage.addField(CumQty).setFloat(order.getCumQty(), sizeFractionDigits);
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), priceFractionDigits);

        fix.send(txMessage);
    }

    private class OrderEntryListener implements POEClientListener, SoupBinTCPClientStatusListener {

        @Override
        public void orderAccepted(POE.OrderAccepted message) throws IOException {
            long orderEntryId = ASCII.getLong(message.orderId);

            Order order = orders.findByOrderEntryID(orderEntryId);
            if (order == null)
                return;

            order.orderAccepted(message.orderNumber);

            sendOrderAccepted(order);
        }

        @Override
        public void orderRejected(POE.OrderRejected message) throws IOException {
            long orderEntryId = ASCII.getLong(message.orderId);

            Order order = orders.findByOrderEntryID(orderEntryId);
            if (order == null)
                return;

            switch (message.reason) {
            case POE.ORDER_REJECT_REASON_UNKNOWN_INSTRUMENT:
                sendOrderRejected(order, OrdRejReasonValues.UnknownSymbol);
                break;
            case POE.ORDER_REJECT_REASON_INVALID_PRICE:
                sendOrderRejected(order, OrdRejReasonValues.Other);
                break;
            case POE.ORDER_REJECT_REASON_INVALID_QUANTITY:
                sendOrderRejected(order, OrdRejReasonValues.IncorrectQuantity);
                break;
            default:
                sendOrderRejected(order, OrdRejReasonValues.Other);
                break;
            }

            orders.removeByOrderEntryID(orderEntryId);
        }

        @Override
        public void orderExecuted(POE.OrderExecuted message) throws IOException {
            long orderEntryId = ASCII.getLong(message.orderId);

            Order order = orders.findByOrderEntryID(orderEntryId);
            if (order == null)
                return;

            Instrument config = instruments.get(order.getSymbol());

            double lastQty = message.quantity / config.getSizeFactor();
            double lastPx  = message.price    / config.getPriceFactor();

            order.orderExecuted(lastQty, lastPx);

            sendOrderExecuted(order, lastQty, lastPx, config);

            if (order.getLeavesQty() == 0) {
                orders.removeByOrderEntryID(orderEntryId);

                if (order.isInPendingStatus())
                    sendOrderCancelReject(order);
            }
        }

        @Override
        public void orderCanceled(POE.OrderCanceled message) throws IOException {
            long orderEntryId = ASCII.getLong(message.orderId);

            Order order = orders.findByOrderEntryID(orderEntryId);
            if (order == null)
                return;

            Instrument config = instruments.get(order.getSymbol());

            order.orderCanceled(message.canceledQuantity / config.getSizeFactor());

            sendOrderCanceled(order, config);

            if (order.getLeavesQty() == 0)
                orders.removeByOrderEntryID(orderEntryId);
        }

        @Override
        public void heartbeatTimeout(SoupBinTCPClient session) throws IOException {
            fix.sendLogout("Trading system not available");
        }

        @Override
        public void loginAccepted(SoupBinTCPClient session, SoupBinTCP.LoginAccepted payload) throws IOException {
            fix.sendLogon(false);
        }

        @Override
        public void loginRejected(SoupBinTCPClient session, SoupBinTCP.LoginRejected payload) throws IOException {
            switch (payload.rejectReasonCode) {
            case SoupBinTCP.LOGIN_REJECT_CODE_NOT_AUTHORIZED:
                fix.sendLogout("Not authorized");
                break;
            case SoupBinTCP.LOGIN_REJECT_CODE_SESSION_NOT_AVAILABLE:
                fix.sendLogout("Session not available");
                break;
            }
        }

        @Override
        public void endOfSession(SoupBinTCPClient session) throws IOException {
            fix.sendLogout();
        }

    }

}
