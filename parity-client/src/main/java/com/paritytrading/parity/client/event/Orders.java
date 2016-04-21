package com.paritytrading.parity.client.event;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

public class Orders extends DefaultEventVisitor {

    private MutableMap<String, Order> orders;

    private Orders() {
        orders = Maps.mutable.with();
    }

    public static ImmutableList<Order> collect(Events events) {
        Orders visitor = new Orders();

        events.accept(visitor);

        return visitor.getEvents();
    }

    private ImmutableList<Order> getEvents() {
        return orders.valuesView().toSortedList((a, b) ->
                Long.compare(a.getTimestamp(), b.getTimestamp())).toImmutable();
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
