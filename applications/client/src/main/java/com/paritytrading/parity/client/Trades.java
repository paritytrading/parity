/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritytrading.parity.client;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trades extends DefaultEventVisitor {

    private final Map<String, Order> orders;

    private final Map<Long, List<Trade>> trades;

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
