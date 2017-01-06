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
import com.paritytrading.parity.util.OrderIDGenerator;
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
import java.util.ArrayList;
import java.util.List;

class Session implements Closeable {

    private static final String UNKNOWN_ORDER_ID = "NONE";

    private static SoupBinTCP.LoginRequest loginRequest = new SoupBinTCP.LoginRequest();

    private static POE.EnterOrder enterOrder = new POE.EnterOrder();

    private static POE.CancelOrder cancelOrder = new POE.CancelOrder();

    private static FIXMessage txMessage = new FIXMessage(64, 64);

    private static ByteBuffer txBuffer = ByteBuffer.allocate(POE.MAX_INBOUND_MESSAGE_LENGTH);

    private OrderIDGenerator orderEntryIds;

    private Orders orders;

    private FIXSession fix;

    private SoupBinTCPClient orderEntry;

    public Session(OrderEntryFactory orderEntry, SocketChannel fix,
            FIXConfig config) throws IOException {
        this.orderEntryIds = new OrderIDGenerator();

        this.orders = new Orders();

        OrderEntryListener orderEntryListener = new OrderEntryListener();

        this.orderEntry = orderEntry.create(orderEntryListener, orderEntryListener);

        FIXListener fixListener = new FIXListener();

        this.fix = new FIXSession(fix, config, fixListener, fixListener);
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

            String orderEntryId = orderEntryIds.next();

            ASCII.putLeft(enterOrder.orderId, orderEntryId);

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

            long orderQty = 0;

            try {
                orderQty = orderQtyValue.asInt();
            } catch (FIXValueFormatException e) {
                incorrectDataFormatForValue(message, "Expected 'int' in OrderQty(38)");
                return;
            }

            if (orderQty < 0) {
                sendOrderRejected(clOrdId, OrdRejReasonValues.IncorrectQuantity, account,
                        symbol, side, orderQty);
                return;
            }

            enterOrder.quantity = orderQty;

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

            enterOrder.price = (long)(price * 100.0) * 100;

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

            long orderQty = 0;

            if (msgType == OrderCancelReplaceRequest) {
                try {
                    orderQty = orderQtyValue.asInt();
                } catch (FIXValueFormatException e) {
                    incorrectDataFormatForValue(message, "Expected 'int' in OrderQty(38)");
                    return;
                }
            }

            order.setNextClOrdID(clOrdId);
            order.setCxlRejResponseTo(cxlRejResponseTo);

            System.arraycopy(order.getOrderEntryID(), 0, cancelOrder.orderId, 0, cancelOrder.orderId.length);
            cancelOrder.quantity = Math.max(orderQty - order.getCumQty(), 0);

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

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(ExecTypeValues.New);
        txMessage.addField(OrdStatus).setChar(order.getOrdStatus());

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(order.getSymbol());
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setInt(order.getOrderQty());
        txMessage.addField(LeavesQty).setInt(order.getLeavesQty());
        txMessage.addField(CumQty).setInt(order.getCumQty());
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), 2);

        fix.send(txMessage);
    }

    private void sendOrderRejected(Order order, int ordRejReason) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(ExecTypeValues.Rejected);
        txMessage.addField(OrdStatus).setChar(OrdStatusValues.Rejected);
        txMessage.addField(OrdRejReason).setInt(ordRejReason);

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(order.getSymbol());
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setInt(order.getOrderQty());
        txMessage.addField(LeavesQty).setInt(0);
        txMessage.addField(CumQty).setInt(order.getCumQty());
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), 2);

        fix.send(txMessage);
    }

    private void sendOrderRejected(String clOrdId, int ordRejReason,
            String account, String symbol, char side, long orderQty) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

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
        txMessage.addField(OrderQty).setInt(orderQty);
        txMessage.addField(LeavesQty).setInt(0);
        txMessage.addField(CumQty).setInt(0);
        txMessage.addField(AvgPx).setFloat(0.00, 2);

        fix.send(txMessage);
    }

    private void sendOrderExecuted(Order order, long lastQty, double lastPx) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(ExecTypeValues.Trade);
        txMessage.addField(OrdStatus).setChar(order.getOrdStatus());

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(order.getSymbol());
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setInt(order.getOrderQty());
        txMessage.addField(LastQty).setInt(lastQty);
        txMessage.addField(LastPx).setFloat(lastPx, 2);
        txMessage.addField(LeavesQty).setInt(order.getLeavesQty());
        txMessage.addField(CumQty).setInt(order.getCumQty());
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), 2);

        fix.send(txMessage);
    }

    private void sendOrderCancelAcknowledgement(Order order, char execType, char ordStatus) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

        txMessage.addField(OrderID).setInt(order.getOrderID());
        txMessage.addField(ClOrdID).setString(order.getNextClOrdID());
        txMessage.addField(OrigClOrdID).setString(order.getClOrdID());
        txMessage.addField(ExecID).setString(fix.getCurrentTimestamp());
        txMessage.addField(ExecType).setChar(execType);
        txMessage.addField(OrdStatus).setChar(ordStatus);

        if (order.getAccount() != null)
            txMessage.addField(Account).setString(order.getAccount());

        txMessage.addField(Symbol).setString(order.getSymbol());
        txMessage.addField(Side).setChar(order.getSide());
        txMessage.addField(OrderQty).setInt(order.getOrderQty());
        txMessage.addField(LeavesQty).setInt(order.getLeavesQty());
        txMessage.addField(CumQty).setInt(order.getCumQty());
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), 2);

        fix.send(txMessage);
    }

    private void sendOrderCanceled(Order order) throws IOException {
        fix.prepare(txMessage, ExecutionReport);

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
        txMessage.addField(OrderQty).setInt(order.getOrderQty());
        txMessage.addField(LeavesQty).setInt(order.getLeavesQty());
        txMessage.addField(CumQty).setInt(order.getCumQty());
        txMessage.addField(AvgPx).setFloat(order.getAvgPx(), 2);

        fix.send(txMessage);
    }

    private class OrderEntryListener implements POEClientListener, SoupBinTCPClientStatusListener {

        @Override
        public void orderAccepted(POE.OrderAccepted message) throws IOException {
            Order order = orders.findByOrderEntryID(message.orderId);
            if (order == null)
                return;

            order.orderAccepted(message.orderNumber);

            sendOrderAccepted(order);
        }

        @Override
        public void orderRejected(POE.OrderRejected message) throws IOException {
            Order order = orders.findByOrderEntryID(message.orderId);
            if (order == null)
                return;

            sendOrderRejected(order, OrdRejReasonValues.UnknownSymbol);

            orders.removeByOrderEntryID(message.orderId);
        }

        @Override
        public void orderExecuted(POE.OrderExecuted message) throws IOException {
            Order order = orders.findByOrderEntryID(message.orderId);
            if (order == null)
                return;

            long   lastQty = message.quantity;
            double lastPx  = message.price / 10000.0;

            order.orderExecuted(lastQty, lastPx);

            sendOrderExecuted(order, lastQty, lastPx);

            if (order.getLeavesQty() == 0) {
                orders.removeByOrderEntryID(message.orderId);

                if (order.isInPendingStatus())
                    sendOrderCancelReject(order);
            }
        }

        @Override
        public void orderCanceled(POE.OrderCanceled message) throws IOException {
            Order order = orders.findByOrderEntryID(message.orderId);
            if (order == null)
                return;

            order.orderCanceled(message.canceledQuantity);

            sendOrderCanceled(order);

            if (order.getLeavesQty() == 0)
                orders.removeByOrderEntryID(message.orderId);
        }

        @Override
        public void brokenTrade(POE.BrokenTrade message) {
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
