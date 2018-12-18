package com.paritytrading.parity.match;

class Order {

    private final PriceLevel level;

    private final long id;

    private long remainingQuantity;

    Order(PriceLevel level, long id, long size) {
        this.level = level;

        this.id = id;

        this.remainingQuantity = size;
    }

    PriceLevel getLevel() {
        return level;
    }

    long getId() {
        return id;
    }

    long getRemainingQuantity() {
        return remainingQuantity;
    }

    void reduce(long quantity) {
        remainingQuantity -= quantity;
    }

    void resize(long size) {
        remainingQuantity = size;
    }

}
