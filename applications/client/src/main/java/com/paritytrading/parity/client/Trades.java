package com.paritytrading.parity.client;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trades extends DefaultEventVisitor {

    private Map<String, Order> orders;

    private Map<Long, List<Trade>> trades;

    private Trades() {
        orders = new HashMap<>();
        trades = new HashMap<>();
    }

    public static List<Trade> collect(Events events) {
        Trades visitor = new Trades();

        events.accept(visitor);

        return visitor.getEvents();
    }

    private List<Trade> getEvents() {
        return trades.values()
                .stream()
                .flatMap(List::stream)
                .sorted(comparing(Trade::getTimestamp))
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

        List<Trade> trade = trades.computeIfAbsent(event.matchNumber, key -> new ArrayList<>());

        trade.add(new Trade(order, event));
    }

}
