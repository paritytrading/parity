package org.jvirtanen.parity.client.event;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.factory.Maps;
import java.util.Comparator;

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
        return orders.valuesView().toSortedList(new Comparator<Order>() {

            @Override
            public int compare(Order a, Order b) {
                return Long.compare(a.getTimestamp(), b.getTimestamp());
            }

        }).toImmutable();
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
