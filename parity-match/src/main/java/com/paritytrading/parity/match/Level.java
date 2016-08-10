package com.paritytrading.parity.match;

import java.util.ArrayList;

class Level {

    private Side side;

    private long price;

    private ArrayList<Order> orders;

    public Level(Side side, long price) {
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

    public long match(long orderId, Side side, long quantity, MarketListener listener,
            ArrayList<Order> toDelete) {
        for (int i = 0; quantity > 0 && i < orders.size(); i++) {
            Order order = orders.get(i);

            long orderQuantity = order.getRemainingQuantity();

            if (orderQuantity > quantity) {
                order.reduce(quantity);

                listener.match(order.getId(), orderId, side, price, quantity, order.getRemainingQuantity());

                quantity = 0;
            } else {
                toDelete.add(order);

                listener.match(order.getId(), orderId, side, price, orderQuantity, 0);

                quantity -= orderQuantity;
            }
        }

        if (!toDelete.isEmpty()) {
            for (int i = 0; i < toDelete.size(); i++)
                orders.remove(toDelete.get(i));

            toDelete.clear();
        }

        return quantity;
    }

    public void delete(Order order) {
        orders.remove(order);
    }

}
