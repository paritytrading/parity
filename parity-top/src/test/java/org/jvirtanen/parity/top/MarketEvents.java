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
    public void bbo(long instrument, long bidPrice, int bidSize, long askPrice, int askSize) {
        events.add(new BBO(instrument, bidPrice, bidSize, askPrice, askSize));
    }

    @Override
    public void trade(long instrument, Side side, long price, int size) {
        events.add(new Trade(instrument, side, price, size));
    }

    public interface Event {
    }

    public static class BBO extends Value implements Event {
        public final long instrument;
        public final long bidPrice;
        public final int  bidSize;
        public final long askPrice;
        public final int  askSize;

        public BBO(long instrument, long bidPrice, int bidSize, long askPrice, int askSize) {
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
        public final int  size;

        public Trade(long instrument, Side side, long price, int size) {
            this.instrument = instrument;
            this.side       = side;
            this.price      = price;
            this.size       = size;
        }
    }

}
