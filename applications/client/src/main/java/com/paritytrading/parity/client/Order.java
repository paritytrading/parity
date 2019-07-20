/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritytrading.parity.client;

import static com.paritytrading.parity.client.TerminalClient.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.Timestamps;

class Order {

    private final long   timestamp;
    private final String orderId;
    private final byte   side;
    private final long   instrument;
    private       long   quantity;
    private final long   price;

    Order(Event.OrderAccepted event) {
        this.timestamp   = event.timestamp;
        this.orderId     = event.orderId;
        this.side        = event.side;
        this.instrument  = event.instrument;
        this.quantity    = event.quantity;
        this.price       = event.price;
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
