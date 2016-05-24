package com.paritytrading.parity.fix;

import java.util.ArrayList;
import java.util.List;

class Orders {

    private List<Order> orders;

    public Orders() {
        orders = new ArrayList<>();
    }

    public void add(Order order) {
        orders.add(order);
    }

    public Order findByClOrdID(String clOrdId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (clOrdId.equals(order.getClOrdID()))
                return order;
        }

        return null;
    }

    public Order findByOrigClOrdID(String origClOrdId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (origClOrdId.equals(order.getOrigClOrdID()))
                return order;
        }

        return null;
    }

    public Order findByOrderEntryID(String orderEntryId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (orderEntryId.equals(order.getOrderEntryID()))
                return order;
        }

        return null;
    }

    public void removeByOrderEntryID(String orderEntryId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (orderEntryId.equals(order.getOrderEntryID())) {
                orders.remove(i);
                break;
            }
        }
    }

}
