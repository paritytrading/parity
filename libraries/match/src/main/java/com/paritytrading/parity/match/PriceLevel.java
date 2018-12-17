package com.paritytrading.parity.match;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;

class PriceLevel {

    private final Side side;

    private final long price;

    private final ArrayList<Order> orders;

    public PriceLevel(Side side, long price) {
        this.side   = side;
        this.price  = price;
        this.orders = new ArrayList<>();
    }

    public Side getSide() {
        return side;
    }

    public long getPrice() {
        return price;
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public Order add(long orderId, long size) {
        Order order = new Order(this, orderId, size);

        orders.add(order);

        return order;
    }

    public long match(long orderId, Side side, long quantity, Long2ObjectOpenHashMap<Order> orderIds, OrderBookListener listener) {
        while (quantity > 0 && !orders.isEmpty()) {
            Order resting = orders.get(0);

            long restingId = resting.getId();

            long restingQuantity = resting.getRemainingQuantity();

            if (restingQuantity > quantity) {
                resting.reduce(quantity);

                listener.match(restingId, orderId, side, price, quantity, resting.getRemainingQuantity());

                quantity = 0;
            } else {
                orders.remove(0);

                orderIds.remove(restingId);

                listener.match(restingId, orderId, side, price, restingQuantity, 0);

                quantity -= restingQuantity;
            }
        }

        return quantity;
    }

    public void delete(Order order) {
        orders.remove(order);
    }

}
