package org.jvirtanen.parity.match;

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
    public void match(long restingOrderId, long incomingOrderId, long price, int quantity) {
        events.add(new Match(restingOrderId, incomingOrderId, price, quantity));
    }

    @Override
    public void add(long orderId, Side side, long price, int size) {
        events.add(new Add(orderId, side, price, size));
    }

    @Override
    public void cancel(long orderId, int quantity) {
        events.add(new Cancel(orderId, quantity));
    }

    @Override
    public void delete(long orderId) {
        events.add(new Delete(orderId));
    }

    public interface Event {
    }

    public static class Match extends Value implements Event {
        public final long restingOrderId;
        public final long incomingOrderId;
        public final long price;
        public final int  quantity;

        public Match(long restingOrderId, long incomingOrderId, long price, int quantity) {
            this.restingOrderId  = restingOrderId;
            this.incomingOrderId = incomingOrderId;
            this.price           = price;
            this.quantity        = quantity;
        }
    }

    public static class Add extends Value implements Event {
        public final long orderId;
        public final Side side;
        public final long price;
        public final int  size;

        public Add(long orderId, Side side, long price, int size) {
            this.orderId = orderId;
            this.side    = side;
            this.price   = price;
            this.size    = size;
        }
    }

    public static class Cancel extends Value implements Event {
        public final long orderId;
        public final int  quantity;

        public Cancel(long orderId, int quantity) {
            this.orderId  = orderId;
            this.quantity = quantity;
        }
    }

    public static class Delete extends Value implements Event {
        public final long orderId;

        public Delete(long orderId) {
            this.orderId = orderId;
        }
    }

}
