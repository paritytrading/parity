package com.paritytrading.parity.book;

/**
 * An order.
 */
public class Order {

    private final OrderBook book;

    private final Side side;
    private final long price;

    private long remainingQuantity;

    Order(OrderBook book, Side side, long price, long size) {
        this.book = book;

        this.side  = side;
        this.price = price;

        this.remainingQuantity = size;
    }

    /**
     * Get the order book.
     *
     * @return the order book
     */
    public OrderBook getOrderBook() {
        return book;
    }

    /**
     * Get the price.
     *
     * @return the price
     */
    public long getPrice() {
        return price;
    }

    /**
     * Get the side.
     *
     * @return the side
     */
    public Side getSide() {
        return side;
    }

    /**
     * Get the remaining quantity.
     *
     * @return the remaining quantity
     */
    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    void setRemainingQuantity(long remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    void reduce(long quantity) {
        remainingQuantity -= quantity;
    }

}
