package org.jvirtanen.parity.client.event;

import static org.jvirtanen.lang.Strings.*;
import static org.jvirtanen.parity.client.TerminalClient.*;

import org.jvirtanen.parity.util.Timestamps;

public class Order {

    public static final String HEADER = "" +
            "Timestamp    Order ID         S Inst     Quantity   Price\n" +
            "------------ ---------------- - -------- ---------- ---------";

    private long   timestamp;
    private String orderId;
    private byte   side;
    private long   instrument;
    private long   quantity;
    private long   price;
    private long   orderNumber;

    public Order(Event.OrderAccepted event) {
        this.timestamp   = event.timestamp;
        this.orderId     = event.orderId;
        this.side        = event.side;
        this.instrument  = event.instrument;
        this.quantity    = event.quantity;
        this.price       = event.price;
        this.orderNumber = event.orderNumber;
    }

    public void apply(Event.OrderExecuted event) {
        quantity -= event.quantity;
    }

    public void apply(Event.OrderCanceled event) {
        quantity -= event.canceledQuantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte getSide() {
        return side;
    }

    public long getInstrument() {
        return instrument;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getQuantity() {
        return quantity;
    }

    public String format() {
        return String.format(LOCALE, "%12s %16s %c %8s %10d %9.2f",
                Timestamps.format(timestamp), orderId, side,
                decodeLong(instrument), quantity, (double)price / PRICE_FACTOR);
    }

}
