package com.paritytrading.parity.book;

import java.util.ArrayList;
import java.util.List;
import org.jvirtanen.value.Value;

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
