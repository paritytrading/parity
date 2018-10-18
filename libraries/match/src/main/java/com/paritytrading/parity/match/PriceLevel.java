package com.paritytrading.parity.match;

import java.util.ArrayList;

class PriceLevel {

    private Side side;

    private long price;

    private ArrayList<Order> orders;

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

    public long match(long orderId, Side side, long quantity, OrderBookListener listener) {
        while (quantity > 0 && !orders.isEmpty()) {
            Order resting = orders.get(0);

            long restingQuantity = resting.getRemainingQuantity();

            if (restingQuantity > quantity) {
                resting.reduce(quantity);

                listener.match(resting.getId(), orderId, side, price, quantity, resting.getRemainingQuantity());

                quantity = 0;
            } else {
                orders.remove(0);

                listener.match(resting.getId(), orderId, side, price, restingQuantity, 0);

                quantity -= restingQuantity;
            }
        }

        return quantity;
    }

    public void delete(Order order) {
        orders.remove(order);
    }

}
