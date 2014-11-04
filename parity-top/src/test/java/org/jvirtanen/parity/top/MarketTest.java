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

        Event bboAfterBid = new BBO(INSTRUMENT, 999, 100,    0,   0);
        Event bboAfterAsk = new BBO(INSTRUMENT, 999, 100, 1001, 200);

        assertEquals(asList(bboAfterBid, bboAfterAsk), events.collect());
    }

    @Test
    public void addition() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.BUY,  1000,  50);

        Event bboAfterFirstBid  = new BBO(INSTRUMENT,  999, 100,    0,   0);
        Event bboAfterAsk       = new BBO(INSTRUMENT,  999, 100, 1001, 200);
        Event bboAfterSecondBid = new BBO(INSTRUMENT, 1000,  50, 1001, 200);

        assertEquals(asList(bboAfterFirstBid, bboAfterAsk, bboAfterSecondBid), events.collect());
    }

    @Test
    public void execution() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        market.execute(2, 200);

        Event bboAfterBid      = new BBO(INSTRUMENT, 999, 100,    0,   0);
        Event bboAfterFirstAsk = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event trade            = new Trade(INSTRUMENT, Side.SELL, 1001, 200);
        Event bboAfterTrade    = new BBO(INSTRUMENT, 999, 100, 1002,  50);

        assertEquals(asList(bboAfterBid, bboAfterFirstAsk, trade, bboAfterTrade), events.collect());
    }

    @Test
    public void partialExecution() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.execute(2, 100);

        Event bboAfterBid   = new BBO(INSTRUMENT, 999, 100,    0,   0);
        Event bboAfterAsk   = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event trade         = new Trade(INSTRUMENT, Side.SELL, 1001, 100);
        Event bboAfterTrade = new BBO(INSTRUMENT, 999, 100, 1001, 100);

        assertEquals(asList(bboAfterBid, bboAfterAsk, trade, bboAfterTrade), events.collect());
    }

    @Test
    public void cancellation() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        market.cancel(2, 200);

        Event bboAfterBid      = new BBO(INSTRUMENT, 999, 100,    0,   0);
        Event bboAfterFirstAsk = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event bboAfterCancel   = new BBO(INSTRUMENT, 999, 100, 1002,  50);

        assertEquals(asList(bboAfterBid, bboAfterFirstAsk, bboAfterCancel), events.collect());
    }

    @Test
    public void partialCancellation() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.cancel(2, 100);

        Event bboAfterBid    = new BBO(INSTRUMENT, 999, 100,    0,   0);
        Event bboAfterAsk    = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event bboAfterCancel = new BBO(INSTRUMENT, 999, 100, 1001, 100);

        assertEquals(asList(bboAfterBid, bboAfterAsk, bboAfterCancel), events.collect());
    }

    @Test
    public void deletion() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        market.delete(2);

        Event bboAfterBid      = new BBO(INSTRUMENT, 999, 100,    0,   0);
        Event bboAfterFirstAsk = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event bboAfterDelete   = new BBO(INSTRUMENT, 999, 100, 1002,  50);

        assertEquals(asList(bboAfterBid, bboAfterFirstAsk, bboAfterDelete), events.collect());
    }

    @Test
    public void empty() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.delete(2);
        market.delete(1);

        Event bboAfterBid          = new BBO(INSTRUMENT, 999, 100,    0,   0);
        Event bboAfterAsk          = new BBO(INSTRUMENT, 999, 100, 1001, 200);
        Event bboAfterFirstDelete  = new BBO(INSTRUMENT, 999, 100,    0,   0);
        Event bboAfterSecondDelete = new BBO(INSTRUMENT,   0,   0,    0,   0);

        assertEquals(asList(bboAfterBid, bboAfterAsk, bboAfterFirstDelete, bboAfterSecondDelete),
                events.collect());
    }

}
