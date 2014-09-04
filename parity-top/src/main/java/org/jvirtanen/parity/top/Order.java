package org.jvirtanen.parity.top;

class Order {

    private Level parent;

    private int remainingQuantity;

    public Order(Level parent, int size) {
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

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public boolean isOnBestLevel() {
        return parent.isBestLevel();
    }

    public void reduce(int quantity) {
        remainingQuantity -= quantity;
    }

    public void delete() {
        parent.delete(this);
    }

}
