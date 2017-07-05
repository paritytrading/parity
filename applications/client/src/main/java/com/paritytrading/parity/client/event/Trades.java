package com.paritytrading.parity.client.event;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Multimaps;

public class Trades extends DefaultEventVisitor {

    private MutableMap<String, Order> orders;

    private MutableBagMultimap<Long, Trade> trades;

    private Trades() {
        orders = Maps.mutable.with();
        trades = Multimaps.mutable.bag.with();
    }

    public static ImmutableList<Trade> collect(Events events) {
        Trades visitor = new Trades();

        events.accept(visitor);

        return visitor.getEvents();
    }

    private ImmutableList<Trade> getEvents() {
        return trades.valuesView().toSortedList((a, b) ->
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

        trades.put(event.matchNumber, new Trade(order, event));
    }

}
