package com.paritytrading.parity.match;

class Order {

    private Level parent;

    private long id;

    private long remainingQuantity;

    public Order(Level parent, long id, long size) {
        this.parent = parent;

        this.id = id;

        this.remainingQuantity = size;
    }

    public long getId() {
        return id;
    }

    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    public void reduce(long quantity) {
        remainingQuantity -= quantity;
    }

    public void resize(long size) {
        remainingQuantity = size;
    }

    public void delete() {
        parent.delete(this);
    }

}
