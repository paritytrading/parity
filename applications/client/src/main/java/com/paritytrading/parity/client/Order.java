package com.paritytrading.parity.client;

import static com.paritytrading.parity.client.TerminalClient.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.Timestamps;

public class Order {

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

    public String format(Instruments instruments) {
        Instrument config = instruments.get(instrument);

        String priceFormat = config.getPriceFormat();
        String sizeFormat  = config.getSizeFormat();

        String format = "%12s %16s %c %8s " + sizeFormat + " " + priceFormat;

        return String.format(LOCALE, format,
                Timestamps.format(timestamp / NANOS_PER_MILLI), orderId, side,
                ASCII.unpackLong(instrument), quantity / config.getSizeFactor(),
                price / config.getPriceFactor());
    }

}
