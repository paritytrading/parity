package com.paritytrading.parity.match;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.longs.LongComparators;

/**
 * An order book.
 */
public class OrderBook {

    private Long2ObjectRBTreeMap<Level> bids;
    private Long2ObjectRBTreeMap<Level> asks;

    private Long2ObjectOpenHashMap<Order> orders;

    private OrderBookListener listener;

    /**
     * Create an order book.
     *
     * @param listener a listener for outbound events from the order book
     */
    public OrderBook(OrderBookListener listener) {
        this.bids = new Long2ObjectRBTreeMap<>(LongComparators.OPPOSITE_COMPARATOR);
        this.asks = new Long2ObjectRBTreeMap<>(LongComparators.NATURAL_COMPARATOR);

        this.orders = new Long2ObjectOpenHashMap<>();

        this.listener = listener;
    }

    /**
     * Enter a market order.
     *
     * <p>The incoming order is matched against resting orders in this order
     * book. This operation results in zero or more Match events.</p>
     *
     * <p>If the remaining quantity is not zero after the matching operation,
     * a Cancel event is triggered for the remaining quantity.</p>
     *
     * <p>If the order identifier is known, do nothing.</p>
     *
     * @param orderId the order identifier
     * @param side the side
     * @param size the size
     */
    public void enter(long orderId, Side side, long size) {
        if (orders.containsKey(orderId))
            return;

        match(orderId, side, side == Side.BUY ? asks : bids, size);
    }

    private void match(long orderId, Side side, Long2ObjectRBTreeMap<Level> levels, long size) {
        long remainingQuantity = size;

        Level bestLevel = getBestLevel(levels);

        while (remainingQuantity > 0 && bestLevel != null) {
            remainingQuantity = bestLevel.match(orderId, side, remainingQuantity, listener);

            if (bestLevel.isEmpty())
                levels.remove(bestLevel.getPrice());

            bestLevel = getBestLevel(levels);
        }

        if (remainingQuantity > 0)
            listener.cancel(orderId, remainingQuantity, 0);
    }

    /**
     * Enter a limit order.
     *
     * <p>The incoming order is first matched against resting orders in this
     * order book. This operation results in zero or more Match events.</p>
     *
     * <p>If the remaining quantity is not zero after the matching operation,
     * the remaining quantity is added to this order book and an Add event is
     * triggered.</p>
     *
     * <p>If the order identifier is known, do nothing.</p>
     *
     * @param orderId an order identifier
     * @param side the side
     * @param price the limit price
     * @param size the size
     */
    public void enter(long orderId, Side side, long price, long size) {
        if (orders.containsKey(orderId))
            return;

        if (side == Side.BUY)
            buy(orderId, price, size);
        else
            sell(orderId, price, size);
    }

    private void buy(long orderId, long price, long size) {
        long remainingQuantity = size;

        Level bestLevel = getBestLevel(asks);

        while (remainingQuantity > 0 && bestLevel != null && bestLevel.getPrice() <= price) {
            remainingQuantity = bestLevel.match(orderId, Side.BUY, remainingQuantity, listener);

            if (bestLevel.isEmpty())
                asks.remove(bestLevel.getPrice());

            bestLevel = getBestLevel(asks);
        }

        if (remainingQuantity > 0) {
            orders.put(orderId, add(bids, orderId, Side.BUY, price, remainingQuantity));

            listener.add(orderId, Side.BUY, price, remainingQuantity);
        }
    }

    private void sell(long orderId, long price, long size) {
        long remainingQuantity = size;

        Level bestLevel = getBestLevel(bids);

        while (remainingQuantity > 0 && bestLevel != null && bestLevel.getPrice() >= price) {
            remainingQuantity = bestLevel.match(orderId, Side.SELL, remainingQuantity, listener);

            if (bestLevel.isEmpty())
                bids.remove(bestLevel.getPrice());

            bestLevel = getBestLevel(bids);
        }

        if (remainingQuantity > 0) {
            orders.put(orderId, add(asks, orderId, Side.SELL, price, remainingQuantity));

            listener.add(orderId, Side.SELL, price, remainingQuantity);
        }
    }

    /**
     * Cancel a quantity of an order in this order book. The size refers
     * to the new order size. If the new order size is set to zero, the
     * order is deleted from this order book.
     *
     * <p>A Cancel event is triggered.</p>
     *
     * <p>If the order identifier is unknown, do nothing.</p>
     *
     * @param orderId the order identifier
     * @param size the new size
     */
    public void cancel(long orderId, long size) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        long remainingQuantity = order.getRemainingQuantity();

        if (size >= remainingQuantity)
            return;

        if (size > 0) {
            order.resize(size);
        } else {
            delete(order);

            orders.remove(orderId);
        }

        listener.cancel(orderId, remainingQuantity - size, size);
    }

    private Level getBestLevel(Long2ObjectRBTreeMap<Level> levels) {
        if (levels.isEmpty())
            return null;

        return levels.get(levels.firstLongKey());
    }

    private Order add(Long2ObjectRBTreeMap<Level> levels, long orderId, Side side, long price, long size) {
        Level level = levels.get(price);
        if (level == null) {
            level = new Level(side, price);
            levels.put(price, level);
        }

        return level.add(orderId, size);
    }

    private void delete(Order order) {
        Level level = order.getLevel();

        level.delete(order);

        if (level.isEmpty())
            delete(level);
    }

    private void delete(Level level) {
        switch (level.getSide()) {
        case BUY:
            bids.remove(level.getPrice());
            break;
        case SELL:
            asks.remove(level.getPrice());
            break;
        }
    }

}
