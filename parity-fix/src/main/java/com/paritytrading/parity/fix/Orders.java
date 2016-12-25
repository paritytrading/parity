package com.paritytrading.parity.fix;

import java.util.ArrayList;
import java.util.Arrays;
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

    public Order findByOrderEntryID(byte[] orderEntryId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (Arrays.equals(orderEntryId, order.getOrderEntryID()))
                return order;
        }

        return null;
    }

    public void removeByOrderEntryID(byte[] orderEntryId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (Arrays.equals(orderEntryId, order.getOrderEntryID())) {
                orders.remove(i);
                break;
            }
        }
    }

}
