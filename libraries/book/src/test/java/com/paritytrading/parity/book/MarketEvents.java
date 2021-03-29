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
package com.paritytrading.parity.book;

import java.util.ArrayList;
import java.util.List;

class MarketEvents implements MarketListener {

    private List<Event> events;

    public MarketEvents() {
        this.events = new ArrayList<>();
    }

    public List<Event> collect() {
        return events;
    }

    @Override
    public void update(OrderBook book, boolean bbo) {
        events.add(new Update(book.getInstrument(), bbo));
    }

    @Override
    public void trade(OrderBook book, Side side, long price, long size) {
        events.add(new Trade(book.getInstrument(), side, price, size));
    }

    public interface Event {
    }

    public static class Update extends Value implements Event {
        public final long    instrument;
        public final boolean bbo;

        public Update(long instrument, boolean bbo) {
            this.instrument = instrument;
            this.bbo        = bbo;
        }
    }

    public static class Trade extends Value implements Event {
        public final long instrument;
        public final Side side;
        public final long price;
        public final long size;

        public Trade(long instrument, Side side, long price, long size) {
            this.instrument = instrument;
            this.side       = side;
            this.price      = price;
            this.size       = size;
        }
    }

}
