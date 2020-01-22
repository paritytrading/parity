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
package com.paritytrading.parity.file.taq;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import org.junit.Test;

public class TAQWriterTest {

    @Test
    public void writeWithDefaultConfiguration() throws Exception {
        TAQ.Quote quote = new TAQ.Quote();

        quote.date            = "2016-01-01";
        quote.timestampMillis = 8 * 60 * 60 * 1000;
        quote.instrument      = "FOO";
        quote.bidPrice        = 100.50;
        quote.bidSize         = 1000;
        quote.askPrice        = 100.75;
        quote.askSize         = 250;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                TAQWriter writer = new TAQWriter(out)) {
            writer.write(quote);
            writer.flush();
    
            String output = "" +
                "Date\t" +
                "Timestamp\t" +
                "Instrument\t" +
                "Record Type\t" +
                "Bid Price\t" +
                "Bid Size\t" +
                "Ask Price\t" +
                "Ask Size\t" +
                "Trade Price\t" +
                "Trade Size\t" +
                "Trade Side\n" +
                "2016-01-01\t" +
                "08:00:00.000\t" +
                "FOO\t" +
                "Q\t" +
                "100.50\t" +
                "1000\t" +
                "100.75\t" +
                "250\t" +
                "\t" +
                "\t" +
                "\n";
    
            assertEquals(output, out.toString("US-ASCII"));
        }
    }

    @Test
    public void writeWithCustomConfiguration() throws Exception {
        TAQ.Trade trade = new TAQ.Trade();

        trade.date            = "2016-01-01";
        trade.timestampMillis = 8 * 60 * 60 * 1000 + 5 * 1000;
        trade.instrument      = "FOO";
        trade.price           = 0.975000;
        trade.size            = 0.00000100;
        trade.side            = TAQ.SELL;

        TAQConfig config = new TAQConfig.Builder()
            .setPriceFractionDigits("FOO", 6)
            .setPriceFractionDigits("BAR", 2)
            .setSizeFractionDigits(8)
            .build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                TAQWriter writer = new TAQWriter(out, config)) {
            writer.write(trade);
            writer.flush();
    
            String output = "" +
                "Date\t" +
                "Timestamp\t" +
                "Instrument\t" +
                "Record Type\t" +
                "Bid Price\t" +
                "Bid Size\t" +
                "Ask Price\t" +
                "Ask Size\t" +
                "Trade Price\t" +
                "Trade Size\t" +
                "Trade Side\n" +
                "2016-01-01\t" +
                "08:00:05.000\t" +
                "FOO\t" +
                "T\t" +
                "\t" +
                "\t" +
                "\t" +
                "\t" +
                "0.975000\t" +
                "0.00000100\t" +
                "S\n";

            assertEquals(output, out.toString("US-ASCII"));
        }
    }

}
