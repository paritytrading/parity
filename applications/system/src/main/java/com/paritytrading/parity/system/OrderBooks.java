package com.paritytrading.parity.system;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.match.OrderBook;
import com.paritytrading.parity.match.OrderBookListener;
import com.paritytrading.parity.match.Side;
import com.paritytrading.parity.net.pmd.PMD;
import com.paritytrading.parity.net.poe.POE;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;

class OrderBooks {

    private enum CancelReason {
        REQUEST,
        SYSTEM,
    }

    private final Long2ObjectArrayMap<OrderBook> books;
    private final Long2ObjectOpenHashMap<Order>  orders;

    private final MarketData      marketData;
    private final MarketReporting marketReporting;

    private long nextOrderNumber;
    private long nextMatchNumber;

    private Order handling;

    private long instrument;

    private CancelReason cancelReason;

    OrderBooks(List<String> instruments, MarketData marketData, MarketReporting marketReporting) {
        this.books  = new Long2ObjectArrayMap<>();
        this.orders = new Long2ObjectOpenHashMap<>();

        EventHandler handler = new EventHandler();

        for (String instrument : instruments)
            books.put(ASCII.packLong(instrument), new OrderBook(handler));

        this.marketData      = marketData;
        this.marketReporting = marketReporting;

        this.nextOrderNumber = 1;
        this.nextMatchNumber = 1;
    }

    void enterOrder(POE.EnterOrder message, Session session) {
        OrderBook book = books.get(message.instrument);
        if (book == null) {
            session.orderRejected(message, POE.ORDER_REJECT_REASON_UNKNOWN_INSTRUMENT);
            return;
        }

        if (message.price < 0) {
            session.orderRejected(message, POE.ORDER_REJECT_REASON_INVALID_PRICE);
            return;
        }

        if (message.quantity <= 0) {
            session.orderRejected(message, POE.ORDER_REJECT_REASON_INVALID_QUANTITY);
            return;
        }

        long orderNumber = nextOrderNumber++;

        handling = new Order(message.orderId, orderNumber, session, book);

        instrument = message.instrument;

        session.orderAccepted(message, handling);

        marketReporting.orderEntered(session.getUsername(), orderNumber, message.side,
                instrument, message.quantity, message.price);

        book.enter(orderNumber, side(message.side), message.price, message.quantity);
    }

    void cancelOrder(POE.CancelOrder message, Order order) {
        handling = order;

        cancelReason = CancelReason.REQUEST;

        order.getBook().cancel(order.getOrderNumber(), message.quantity);
    }

    void cancel(Order order) {
        handling = order;

        cancelReason = CancelReason.SYSTEM;

        order.getBook().cancel(order.getOrderNumber(), 0);

        orders.remove(order.getOrderNumber());
    }

    private void track(Order order) {
        orders.put(order.getOrderNumber(), order);

        order.getSession().track(order);
    }

    private void release(Order order) {
        order.getSession().release(order);

        orders.remove(order.getOrderNumber());
    }

    private class EventHandler implements OrderBookListener {

        @Override
        public void match(long restingOrderNumber, long incomingOrderNumber, Side incomingSide,
                long price, long executedQuantity, long remainingQuantity) {
            Order resting = orders.get(restingOrderNumber);
            if (resting == null)
                return;

            long matchNumber = nextMatchNumber++;

            marketData.orderExecuted(restingOrderNumber, executedQuantity, matchNumber);

            resting.getSession().orderExecuted(price, executedQuantity, POE.LIQUIDITY_FLAG_ADDED_LIQUIDITY,
                    matchNumber, resting);

            handling.getSession().orderExecuted(price, executedQuantity, POE.LIQUIDITY_FLAG_REMOVED_LIQUIDITY,
                    matchNumber, handling);

            marketReporting.trade(restingOrderNumber, incomingOrderNumber, executedQuantity, matchNumber);

            if (remainingQuantity == 0)
                release(resting);
        }

        @Override
        public void add(long orderNumber, Side side, long price, long size) {
            marketData.orderAdded(orderNumber, side(side), instrument, size, price);

            marketReporting.orderAdded(orderNumber);

            track(handling);
        }

        @Override
        public void cancel(long orderNumber, long canceledQuantity, long remainingQuantity) {
            marketData.orderCanceled(orderNumber, canceledQuantity);

            if (cancelReason == CancelReason.REQUEST)
                handling.getSession().orderCanceled(canceledQuantity, POE.ORDER_CANCEL_REASON_REQUEST, handling);

            marketReporting.orderCanceled(orderNumber, canceledQuantity);

            if (remainingQuantity == 0 && cancelReason == CancelReason.REQUEST)
                release(handling);
        }

    }

    private Side side(byte side) {
        return side == POE.BUY ? Side.BUY : Side.SELL;
    }

    private byte side(Side side) {
        return side == Side.BUY ? PMD.BUY : PMD.SELL;
    }

}
