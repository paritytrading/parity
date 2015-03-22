package org.jvirtanen.parity.client.event;

import static org.jvirtanen.lang.Strings.*;
import static org.jvirtanen.parity.client.TerminalClient.*;

import org.jvirtanen.parity.client.util.Timestamps;

public class Trade {

    public static final String HEADER = "" +
            "Timestamp    Order ID         S Inst     Quantity   Price\n" +
            "------------ ---------------- - -------- ---------- ---------";

    private long  timestamp;
    private Order order;
    private long  quantity;
    private long  price;

    public Trade(Order order, Event.OrderExecuted event) {
        this.timestamp = event.timestamp;
        this.order     = order;
        this.quantity  = event.quantity;
        this.price     = event.price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String format() {
        return String.format(LOCALE, "%12s %16s %c %8s %10d %9.2f",
                Timestamps.format(timestamp), order.getOrderId(), order.getSide(),
                decodeLong(order.getInstrument()), quantity, (double)price / PRICE_FACTOR);
    }

}
