package org.jvirtanen.parity.client.event;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.bag.MutableBagMultimap;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.factory.Multimaps;

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

    @Override
    public void visit(Event.BrokenTrade event) {
        trades.removeAll(event.matchNumber);
    }

}
