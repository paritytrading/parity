package com.paritytrading.parity.system;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.match.OrderBook;
import com.paritytrading.parity.match.OrderBookListener;
import com.paritytrading.parity.match.Side;
import com.paritytrading.parity.net.pmd.PMD;
import com.paritytrading.parity.net.pmr.PMR;
import com.paritytrading.parity.net.poe.POE;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;

class MatchingEngine {

    private enum CancelReason {
        REQUEST,
        SYSTEM,
    }

    private Long2ObjectArrayMap<OrderBook> books;
    private Long2ObjectOpenHashMap<Order>  orders;

    private MarketData      marketData;
    private MarketReporting marketReporting;

    private long nextOrderNumber;
    private long nextMatchNumber;

    private Order handling;

    private long instrument;

    private CancelReason cancelReason;

    public MatchingEngine(List<String> instruments, MarketData marketData, MarketReporting marketReporting) {
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

    public void enterOrder(POE.EnterOrder message, String orderId, Session session) {
        OrderBook book = books.get(message.instrument);
        if (book == null) {
            session.orderRejected(message, POE.ORDER_REJECT_REASON_UNKNOWN_INSTRUMENT);
            return;
        }

        long orderNumber = nextOrderNumber++;

        handling = new Order(orderId, orderNumber, session, book);

        instrument = message.instrument;

        session.orderAccepted(message, orderId, handling);

        marketReporting.order(session.getUsername(), orderNumber, pmr(message.side),
                instrument, message.quantity, message.price);

        book.enter(orderNumber, poe(message.side), message.price, message.quantity);
    }

    public void cancelOrder(POE.CancelOrder message, Order order) {
        handling = order;

        cancelReason = CancelReason.REQUEST;

        order.getBook().cancel(order.getOrderNumber(), message.quantity);
    }

    public void cancel(Order order) {
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

            resting.getSession().orderExecuted(price, executedQuantity, POE.LIQUIDITY_FLAG_ADDED_LIQUIDITY,
                    matchNumber, resting);

            handling.getSession().orderExecuted(price, executedQuantity, POE.LIQUIDITY_FLAG_REMOVED_LIQUIDITY,
                    matchNumber, handling);

            marketData.orderExecuted(resting.getOrderNumber(), executedQuantity, matchNumber);

            long restingUsername  = resting.getSession().getUsername();
            long incomingUsername = handling.getSession().getUsername();

            long buyer  = incomingSide == Side.BUY  ? incomingUsername : restingUsername;
            long seller = incomingSide == Side.SELL ? incomingUsername : restingUsername;

            long buyOrderNumber  = incomingSide == Side.BUY  ? incomingOrderNumber : restingOrderNumber;
            long sellOrderNumber = incomingSide == Side.SELL ? incomingOrderNumber : restingOrderNumber;

            marketReporting.trade(matchNumber, instrument, executedQuantity, price, buyer, buyOrderNumber,
                    seller, sellOrderNumber);

            if (remainingQuantity == 0)
                release(resting);
        }

        @Override
        public void add(long orderNumber, Side side, long price, long size) {
            marketData.orderAdded(orderNumber, pmd(side), instrument, size, price);

            track(handling);
        }

        @Override
        public void cancel(long orderNumber, long canceledQuantity, long remainingQuantity) {
            if (cancelReason == CancelReason.REQUEST)
                handling.getSession().orderCanceled(canceledQuantity, POE.ORDER_CANCEL_REASON_REQUEST, handling);

            if (remainingQuantity > 0) {
                marketData.orderCanceled(orderNumber, canceledQuantity);
            } else {
                marketData.orderDeleted(orderNumber);

                if (remainingQuantity == 0 && cancelReason == CancelReason.REQUEST)
                    release(handling);
            }

            marketReporting.cancel(handling.getSession().getUsername(), orderNumber, canceledQuantity);
        }

    }

    private Side poe(byte side) {
        switch (side) {
        case POE.BUY:
            return Side.BUY;
        case POE.SELL:
            return Side.SELL;
        }

        return null;
    }

    private byte pmd(Side side) {
        switch (side) {
        case BUY:
            return PMD.BUY;
        case SELL:
            return PMD.SELL;
        }

        return 0;
    }

    private byte pmr(byte side) {
        switch (side) {
        case POE.BUY:
            return PMR.BUY;
        case POE.SELL:
            return PMR.SELL;
        }

        return 0;
    }

}
