package org.jvirtanen.parity.top;

import java.util.ArrayList;

class Level {

    private Orders parent;

    private long price;

    private ArrayList<Order> orders;

    public Level(Orders parent, long price) {
        this.parent = parent;
        this.price  = price;
        this.orders = new ArrayList<>();
    }

    public Orders getParent() {
        return parent;
    }

    public long getPrice() {
        return price;
    }

    public int getSize() {
        int size = 0;

        for (int i = 0; i < orders.size(); i++) {
            size += orders.get(i).getRemainingQuantity();
        }

        return size;
    }

    public boolean isBestLevel() {
        return this == parent.getBestLevel();
    }

    public Order add(int size) {
        Order order = new Order(this, size);

        orders.add(order);

        return order;
    }

    public void delete(Order order) {
        orders.remove(order);

        if (orders.isEmpty())
            parent.delete(this);
    }

}
