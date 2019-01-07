package com.paritytrading.parity.client;

import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.net.poe.POEClientListener;
import java.util.ArrayList;
import java.util.List;

class Events implements POEClientListener {

    private List<Event> events;

    Events() {
        events = new ArrayList<>();
    }

    synchronized void accept(EventVisitor visitor) {
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

    private synchronized void add(Event event) {
        events.add(event);
    }

}
