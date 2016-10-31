package com.paritytrading.parity.file.taq;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import org.junit.Test;

public class TAQWriterTest {

    @Test
    public void write() throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();

	TAQ.Quote quote = new TAQ.Quote();

	quote.date            = "2016-01-01";
	quote.timestampMillis = 8 * 60 * 60 * 1000;
	quote.instrument      = "FOO";
	quote.bidPrice        = 100.50;
	quote.bidSize         = 1000;
	quote.askPrice        = 100.75;
	quote.askSize         = 250;

	TAQ.Trade trade = new TAQ.Trade();

	trade.date            = "2016-01-01";
	trade.timestampMillis = 8 * 60 * 60 * 1000 + 5 * 1000;
	trade.instrument      = "FOO";
	trade.price           = 100.75;
	trade.size            = 100;
	trade.side            = TAQ.SELL;

	TAQWriter writer = new TAQWriter(out);

	writer.write(quote);
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
	    "08:00:00.000\t" +
	    "FOO\t" +
	    "Q\t" +
	    "100.50\t" +
	    "1000\t" +
	    "100.75\t" +
	    "250\t" +
	    "\t" +
	    "\t" +
	    "\n" +
	    "2016-01-01\t" +
	    "08:00:05.000\t" +
	    "FOO\t" +
	    "T\t" +
	    "\t" +
	    "\t" +
	    "\t" +
	    "\t" +
	    "100.75\t" +
	    "100\t" +
	    "S\n";

	assertEquals(output, out.toString("US-ASCII"));
    }

}
