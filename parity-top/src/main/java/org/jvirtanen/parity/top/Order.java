package org.jvirtanen.parity.top;

/**
 * An order in an order book.
 */
public class Order {

    private Level parent;

    private long remainingQuantity;

    Order(Level parent, long size) {
        this.parent = parent;

        this.remainingQuantity = size;
    }

    OrderBook getOrderBook() {
        return parent.getParent().getParent();
    }

    /**
     * Get the instrument.
     *
     * @return the instrument
     */
    public long getInstrument() {
        return getOrderBook().getInstrument();
    }

    /**
     * Get the price.
     *
     * @return the price
     */
    public long getPrice() {
        return parent.getPrice();
    }

    /**
     * Get the side.
     *
     * @return the side
     */
    public Side getSide() {
        return parent.getParent().getSide();
    }

    /**
     * Get the remaining quantity.
     *
     * @return the remaining quantity
     */
    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    boolean isOnBestLevel() {
        return parent.isBestLevel();
    }

    void reduce(long quantity) {
        remainingQuantity -= quantity;
    }

    void delete() {
        parent.delete(this);
    }

}
