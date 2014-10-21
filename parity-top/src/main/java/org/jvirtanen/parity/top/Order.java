package org.jvirtanen.parity.top;

class Order {

    private Level parent;

    private long remainingQuantity;

    public Order(Level parent, long size) {
        this.parent = parent;

        this.remainingQuantity = size;
    }

    public OrderBook getOrderBook() {
        return parent.getParent().getParent();
    }

    public long getPrice() {
        return parent.getPrice();
    }

    public Side getSide() {
        return parent.getParent().getSide();
    }

    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    public boolean isOnBestLevel() {
        return parent.isBestLevel();
    }

    public void reduce(long quantity) {
        remainingQuantity -= quantity;
    }

    public void delete() {
        parent.delete(this);
    }

}
