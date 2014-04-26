package org.jvirtanen.parity.match;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * <code>Market</code> represents the matching engine.
 */
public class Market {

    private Orders bids;
    private Orders asks;

    private Long2ObjectOpenHashMap<Order> orders;

    private MarketListener listener;

    /**
     * Create a matching engine.
     *
     * @param listener a listener for outbound events from the matching engine
     */
    public Market(MarketListener listener) {
        this.bids = new Orders(BidComparator.INSTANCE);
        this.asks = new Orders(AskComparator.INSTANCE);

        this.orders = new Long2ObjectOpenHashMap<>();

        this.listener = listener;
    }

    /**
     * Enter a market order.
     *
     * <p>The incoming order is matched against resting orders in the order
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
    public void enter(long orderId, Side side, int size) {
        if (orders.containsKey(orderId))
            return;

        switch (side) {
        case BUY:
            match(orderId, asks, size);
            break;
        case SELL:
            match(orderId, bids, size);
            break;
        }
    }

    private void match(long orderId, Orders orders, int size) {
        int remainingQuantity = size;

        Level top = orders.getBestLevel();

        while (remainingQuantity > 0 && top != null) {
            remainingQuantity = top.match(orderId, remainingQuantity, listener);

            top = orders.getBestLevel();
        }

        if (remainingQuantity > 0)
            listener.cancel(orderId, remainingQuantity);
    }

    /**
     * Enter a limit order.
     *
     * <p>The incoming order is first matched against resting orders in the
     * order book. This operation results in zero or more Match events.</p>
     *
     * <p>If the remaining quantity is not zero after the matching operation,
     * the remaining quantity is added to the order book and an Add event is
     * triggered.</p>
     *
     * <p>If the order identifier is known, do nothing.</p>
     *
     * @param orderId an order identifier
     * @param side the side
     * @param price the limit price
     * @param size the size
     */
    public void enter(long orderId, Side side, long price, int size) {
        if (orders.containsKey(orderId))
            return;

        switch (side) {
        case BUY:
            buy(orderId, price, size);
            break;
        case SELL:
            sell(orderId, price, size);
            break;
        }
    }

    private void buy(long orderId, long price, int size) {
        int remainingQuantity = size;

        Level top = asks.getBestLevel();

        while (remainingQuantity > 0 && top != null && top.getPrice() <= price) {
            remainingQuantity = top.match(orderId, remainingQuantity, listener);

            top = asks.getBestLevel();
        }

        if (remainingQuantity > 0) {
            orders.put(orderId, bids.add(orderId, price, remainingQuantity));

            listener.add(orderId, Side.BUY, price, remainingQuantity);
        }
    }

    private void sell(long orderId, long price, int size) {
        int remainingQuantity = size;

        Level top = bids.getBestLevel();

        while (remainingQuantity > 0 && top != null && top.getPrice() >= price) {
            remainingQuantity = top.match(orderId, remainingQuantity, listener);

            top = bids.getBestLevel();
        }

        if (remainingQuantity > 0) {
            orders.put(orderId, asks.add(orderId, price, remainingQuantity));

            listener.add(orderId, Side.SELL, price, remainingQuantity);
        }
    }

    /**
     * Cancel a quantity of an order in the order book. If the remaining
     * quantity reaches zero, the order is deleted from the order book.
     *
     * <p>A Cancel event is triggered.</p>
     *
     * <p>If the order identifier is unknown, do nothing.</p>
     *
     * @param orderId the order identifier
     * @param quantity the canceled quantity
     */
    public void cancel(long orderId, int quantity) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        if (quantity < order.getRemainingQuantity())
            order.reduce(quantity);
        else
            order.delete();

        listener.cancel(orderId, quantity);
    }

    /**
     * Delete an order from the order book.
     *
     * <p>A Delete event is triggered.</p>
     *
     * <p>If the order identifier is unknown, do nothing.</p>
     *
     * @param orderId the order identifier
     */
    public void delete(long orderId) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        order.delete();

        listener.delete(orderId);
    }

}
