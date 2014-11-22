package org.jvirtanen.parity.top;

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
    public void bbo(long instrument, long bidPrice, long bidSize, long askPrice, long askSize) {
        events.add(new BBO(instrument, bidPrice, bidSize, askPrice, askSize));
    }

    @Override
    public void trade(long instrument, Side side, long price, long size) {
        events.add(new Trade(instrument, side, price, size));
    }

    public interface Event {
    }

    public static class BBO extends Value implements Event {
        public final long instrument;
        public final long bidPrice;
        public final long bidSize;
        public final long askPrice;
        public final long askSize;

        public BBO(long instrument, long bidPrice, long bidSize, long askPrice, long askSize) {
            this.instrument = instrument;
            this.bidPrice   = bidPrice;
            this.bidSize    = bidSize;
            this.askPrice   = askPrice;
            this.askSize    = askSize;
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
