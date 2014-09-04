package org.jvirtanen.parity.top;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.jvirtanen.parity.top.MarketEvents.*;

import org.junit.Before;
import org.junit.Test;

public class MarketTest {

    private static final long INSTRUMENT = 1;

    private MarketEvents events;

    private Market market;

    @Before
    public void setUp() {
        events = new MarketEvents();
	market = new Market(events);

        market.open(INSTRUMENT);
    }

    @Test
    public void bbo() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);

        Event bbo = new BBO(INSTRUMENT, 999, 100, 1001, 200);

        assertEquals(asList(bbo), events.collect());
    }

    @Test
    public void addition() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.BUY,  1000,  50);

        Event bboBeforeAdd = new BBO(INSTRUMENT,  999, 100, 1001, 200);
        Event bboAfterAdd  = new BBO(INSTRUMENT, 1000,  50, 1001, 200);

        assertEquals(asList(bboBeforeAdd, bboAfterAdd), events.collect());
    }

    @Test
    public void execution() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        market.execute(2, 200);

        Event bboBeforeTrade = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event trade          = new Trade(INSTRUMENT, Side.SELL, 1001, 200);
        Event bboAfterTrade  = new BBO(INSTRUMENT, 999, 100, 1002,  50);

        assertEquals(asList(bboBeforeTrade, trade, bboAfterTrade), events.collect());
    }

    @Test
    public void partialExecution() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.execute(2, 100);

        Event bboBeforeTrade = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event trade          = new Trade(INSTRUMENT, Side.SELL, 1001, 100);
        Event bboAfterTrade  = new BBO(INSTRUMENT, 999, 100, 1001, 100);

        assertEquals(asList(bboBeforeTrade, trade, bboAfterTrade), events.collect());
    }

    @Test
    public void cancellation() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        market.cancel(2, 200);

        Event bboBeforeCancel = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event bboAfterCancel  = new BBO(INSTRUMENT, 999, 100, 1002,  50);

        assertEquals(asList(bboBeforeCancel, bboAfterCancel), events.collect());
    }

    @Test
    public void partialCancellation() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.cancel(2, 100);

        Event bboBeforeCancel = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event bboAfterCancel  = new BBO(INSTRUMENT, 999, 100, 1001, 100);

        assertEquals(asList(bboBeforeCancel, bboAfterCancel), events.collect());
    }

    @Test
    public void deletion() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        market.delete(2);

        Event bboBeforeDelete = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event bboAfterDelete  = new BBO(INSTRUMENT, 999, 100, 1002,  50);

        assertEquals(asList(bboBeforeDelete, bboAfterDelete), events.collect());
    }

}
