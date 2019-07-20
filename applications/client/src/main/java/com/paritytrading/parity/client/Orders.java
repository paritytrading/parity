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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Orders extends DefaultEventVisitor {

    private final Map<String, Order> orders;

    private Orders() {
        orders = new HashMap<>();
    }

    static List<Order> collect(Events events) {
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
