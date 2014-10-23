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
    public void match(long restingOrderId, long incomingOrderId, long price,
            long executedQuantity, long remainingQuantity) {
        events.add(new Match(restingOrderId, incomingOrderId, price,
                    executedQuantity, remainingQuantity));
    }

    @Override
    public void add(long orderId, Side side, long price, long size) {
        events.add(new Add(orderId, side, price, size));
    }

    @Override
    public void cancel(long orderId, long canceledQuantity, long remainingQuantity) {
        events.add(new Cancel(orderId, canceledQuantity, remainingQuantity));
    }

    public interface Event {
    }

    public static class Match extends Value implements Event {
        public final long restingOrderId;
        public final long incomingOrderId;
        public final long price;
        public final long executedQuantity;
        public final long remainingQuantity;

        public Match(long restingOrderId, long incomingOrderId, long price,
                long executedQuantity, long remainingQuantity) {
            this.restingOrderId    = restingOrderId;
            this.incomingOrderId   = incomingOrderId;
            this.price             = price;
            this.executedQuantity  = executedQuantity;
            this.remainingQuantity = remainingQuantity;
        }
    }

    public static class Add extends Value implements Event {
        public final long orderId;
        public final Side side;
        public final long price;
        public final long size;

        public Add(long orderId, Side side, long price, long size) {
            this.orderId = orderId;
            this.side    = side;
            this.price   = price;
            this.size    = size;
        }
    }

    public static class Cancel extends Value implements Event {
        public final long orderId;
        public final long canceledQuantity;
        public final long remainingQuantity;

        public Cancel(long orderId, long canceledQuantity, long remainingQuantity) {
            this.orderId           = orderId;
            this.canceledQuantity  = canceledQuantity;
            this.remainingQuantity = remainingQuantity;
        }
    }

}
