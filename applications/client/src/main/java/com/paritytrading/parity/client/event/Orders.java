package com.paritytrading.parity.client.event;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Orders extends DefaultEventVisitor {

    private Map<String, Order> orders;

    private Orders() {
        orders = new HashMap<>();
    }

    public static List<Order> collect(Events events) {
        Orders visitor = new Orders();

        events.accept(visitor);

        return visitor.getEvents();
    }

    private List<Order> getEvents() {
        return orders.values()
                .stream()
                .sorted(comparing(Order::getTimestamp))
                .collect(toList());
    }

    @Override
    public void visit(Event.OrderAccepted event) {
        orders.put(event.orderId, new Order(event));
    }

    @Override
    public void visit(Event.OrderExecuted event) {
        Order order = orders.get(event.orderId);
        if (order == null)
            return;

        order.apply(event);

        if (order.getQuantity() <= 0)
            orders.remove(event.orderId);
    }

    @Override
    public void visit(Event.OrderCanceled event) {
        Order order = orders.get(event.orderId);
        if (order == null)
            return;

        order.apply(event);

        if (order.getQuantity() <= 0)
            orders.remove(event.orderId);
    }

}
