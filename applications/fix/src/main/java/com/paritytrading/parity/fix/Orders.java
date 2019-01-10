package com.paritytrading.parity.fix;

import java.util.ArrayList;
import java.util.List;

class Orders {

    private final List<Order> orders;

    Orders() {
        orders = new ArrayList<>();
    }

    void add(Order order) {
        orders.add(order);
    }

    Order findByClOrdID(String clOrdId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (clOrdId.equals(order.getClOrdID()))
                return order;
        }

        return null;
    }

    Order findByOrderEntryID(long orderEntryId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (orderEntryId == order.getOrderEntryID())
                return order;
        }

        return null;
    }

    void removeByOrderEntryID(long orderEntryId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (orderEntryId == order.getOrderEntryID()) {
                orders.remove(i);
                break;
            }
        }
    }

}
