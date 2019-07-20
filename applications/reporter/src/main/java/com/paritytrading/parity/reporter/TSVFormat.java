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
package com.paritytrading.parity.reporter;

import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import java.util.HashMap;
import java.util.Map;

class TSVFormat extends TradeListener {

    private static final String HEADER = "" +
        "Timestamp\t" +
        "Match Number\t" +
        "Instrument\t" +
        "Quantity\t" +
        "Price\t" +
        "Buyer\t" +
        "Buy Order Number\t" +
        "Seller\t" +
        "Sell Order Number\n";

    private final Instruments instruments;

    private final Map<String, String> formats;

    TSVFormat(Instruments instruments) {
        this.instruments = instruments;

        this.formats = new HashMap<>();

        for (Instrument instrument : instruments) {
            int priceFractionDigits = instrument.getPriceFractionDigits();
            int sizeFractionDigits  = instrument.getSizeFractionDigits();

            String format = "%." + sizeFractionDigits + "f\t%." + priceFractionDigits + "f\t";

            formats.put(instrument.asString(), format);
        }

        printf(HEADER);
    }

    @Override
    void trade(Trade event) {
        printf("%s\t%d\t%s\t", event.timestamp, event.matchNumber, event.instrument);

        Instrument instrument = instruments.get(event.instrument);

        double priceFactor = instrument.getPriceFactor();
        double sizeFactor  = instrument.getSizeFactor();

        String format = formats.get(event.instrument);

        printf(format, event.quantity / sizeFactor, event.price / priceFactor);

        printf("%s\t%d\t%s\t%d\n", event.buyer, event.buyOrderNumber, event.seller,
                event.sellOrderNumber);
    }

}
