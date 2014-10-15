package org.jvirtanen.parity.match;

/**
 * <code>MarketListener</code> is the interface for outbound events from the
 * matching engine.
 */
public interface MarketListener {

    /**
     * Match an incoming order to a resting order in the order book. The match
     * occurs at the price of the order in the order book.
     *
     * @param restingOrderId the order identifier of the resting order
     * @param incomingOrderId the order identifier of the incoming order
     * @param price the execution price
     * @param quantity the executed quantity
     */
    void match(long restingOrderId, long incomingOrderId, long price, int quantity);

    /**
     * Add an order to the order book.
     *
     * @param orderId the order identifier
     * @param side the side
     * @param price the limit price
     * @param size the size
     */
    void add(long orderId, Side side, long price, int size);

    /**
     * Cancel a quantity of an order.
     *
     * @param orderId the order identifier
     * @param quantity the canceled quantity
     */
    void cancel(long orderId, int quantity);

    /**
     * Delete an order from the order book.
     *
     * @param orderId the order identifier
     */
    void delete(long orderId);

}
