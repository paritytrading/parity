package org.jvirtanen.parity.client.event;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.impl.factory.Lists;
import org.jvirtanen.parity.net.poe.POE;
import org.jvirtanen.parity.net.poe.POEClientListener;

public class Events implements POEClientListener {

    private volatile ImmutableList<Event> events;

    public Events() {
        events = Lists.immutable.with();
    }

    public void accept(EventVisitor visitor) {
        for (Event event : events)
            event.accept(visitor);
    }

    @Override
    public void orderAccepted(POE.OrderAccepted message) {
        add(new Event.OrderAccepted(message));
    }

    @Override
    public void orderRejected(POE.OrderRejected message) {
        add(new Event.OrderRejected(message));
    }

    @Override
    public void orderExecuted(POE.OrderExecuted message) {
        add(new Event.OrderExecuted(message));
    }

    @Override
    public void orderCanceled(POE.OrderCanceled message) {
        add(new Event.OrderCanceled(message));
    }

    @Override
    public void brokenTrade(POE.BrokenTrade message) {
        add(new Event.BrokenTrade(message));
    }

    private void add(Event event) {
        events = events.newWith(event);
    }

}
