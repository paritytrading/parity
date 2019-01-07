package com.paritytrading.parity.client;

import static com.paritytrading.parity.client.TerminalClient.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.Timestamps;

class Order {

    private long   timestamp;
    private String orderId;
    private byte   side;
    private long   instrument;
    private long   quantity;
    private long   price;
    private long   orderNumber;

    Order(Event.OrderAccepted event) {
        this.timestamp   = event.timestamp;
        this.orderId     = event.orderId;
        this.side        = event.side;
        this.instrument  = event.instrument;
        this.quantity    = event.quantity;
        this.price       = event.price;
        this.orderNumber = event.orderNumber;
    }

    void apply(Event.OrderExecuted event) {
        quantity -= event.quantity;
    }

    void apply(Event.OrderCanceled event) {
        quantity -= event.canceledQuantity;
    }

    long getTimestamp() {
        return timestamp;
    }

    byte getSide() {
        return side;
    }

    long getInstrument() {
        return instrument;
    }

    String getOrderId() {
        return orderId;
    }

    long getQuantity() {
        return quantity;
    }

    String format(Instruments instruments) {
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
