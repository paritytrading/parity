package com.paritytrading.parity.match;

class Order {

    private Level level;

    private long id;

    private long remainingQuantity;

    public Order(Level level, long id, long size) {
        this.level = level;

        this.id = id;

        this.remainingQuantity = size;
    }

    public Level getLevel() {
        return level;
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

}
