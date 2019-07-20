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

import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.net.poe.POEClientListener;
import java.util.ArrayList;
import java.util.List;

class Events implements POEClientListener {

    private final List<Event> events;

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
