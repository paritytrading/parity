package com.paritytrading.parity.client;

import static com.paritytrading.parity.client.TerminalClient.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.Timestamps;

public class Trade {

    private final long  timestamp;
    private final Order order;
    private final long  quantity;
    private final long  price;

    public Trade(Order order, Event.OrderExecuted event) {
        this.timestamp = event.timestamp;
        this.order     = order;
        this.quantity  = event.quantity;
        this.price     = event.price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String format(Instruments instruments) {
        Instrument config = instruments.get(order.getInstrument());

        String priceFormat = config.getPriceFormat();
        String sizeFormat  = config.getSizeFormat();

        String format = "%12s %16s %c %8s " + sizeFormat + " " + priceFormat;

        return String.format(LOCALE, format,
                Timestamps.format(timestamp / NANOS_PER_MILLI), order.getOrderId(), order.getSide(),
                ASCII.unpackLong(order.getInstrument()), quantity / config.getSizeFactor(),
                price / config.getPriceFactor());
    }

}
