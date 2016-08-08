package com.paritytrading.parity.book;

/**
 * An order in an order book.
 */
public class Order {

    private OrderBook book;

    private Side side;
    private long price;

    private long remainingQuantity;

    Order(OrderBook book, Side side, long price, long size) {
        this.book = book;

        this.side  = side;
        this.price = price;

        this.remainingQuantity = size;
    }

    OrderBook getOrderBook() {
        return book;
    }

    /**
     * Get the instrument.
     *
     * @return the instrument
     */
    public long getInstrument() {
        return book.getInstrument();
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
