package org.jvirtanen.parity.match;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.jvirtanen.parity.match.MarketEvents.*;

import org.junit.Before;
import org.junit.Test;

public class MarketTest {

    private MarketEvents events;

    private Market market;

    @Before
    public void setUp() {
        events = new MarketEvents();
        market = new Market(events);
    }

    @Test
    public void bid() {
        market.enter(1, Side.BUY, 1000, 100);

        Event bid = new Add(1, Side.BUY, 1000, 100);

        assertEquals(asList(bid), events.collect());
    }

    @Test
    public void ask() {
        market.enter(1, Side.SELL, 1000, 100);

        Event ask = new Add(1, Side.SELL, 1000, 100);

        assertEquals(asList(ask), events.collect());
    }

    @Test
    public void marketBuy() {
        market.enter(1, Side.SELL, 1000, 100);
        market.enter(2, Side.BUY,  100);

        Event ask   = new Add(1, Side.SELL, 1000, 100);
        Event match = new Match(1, 2, Side.BUY, 1000, 100, 0);

        assertEquals(asList(ask, match), events.collect());
    }

    @Test
    public void marketSell() {
        market.enter(1, Side.BUY,  1000, 100);
        market.enter(2, Side.SELL, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 100);
        Event match = new Match(1, 2, Side.SELL, 1000, 100, 0);

        assertEquals(asList(bid, match), events.collect());
    }

    @Test
    public void multiLevelMarketBuy() {
        market.enter(1, Side.SELL, 1001, 100);
        market.enter(2, Side.SELL, 1000,  50);
        market.enter(3, Side.BUY,  100);

        Event firstAsk  = new Add(1, Side.SELL, 1001, 100);
        Event secondAsk = new Add(2, Side.SELL, 1000,  50);

        Event firstMatch  = new Match(2, 3, Side.BUY, 1000, 50,  0);
        Event secondMatch = new Match(1, 3, Side.BUY, 1001, 50, 50);

        assertEquals(asList(firstAsk, secondAsk, firstMatch, secondMatch),
                events.collect());
    }

    @Test
    public void multiLevelMarketSell() {
        market.enter(1, Side.BUY,   999, 100);
        market.enter(2, Side.BUY,  1000,  50);
        market.enter(3, Side.SELL,  100);

        Event firstBid  = new Add(1, Side.BUY,  999, 100);
        Event secondBid = new Add(2, Side.BUY, 1000,  50);

        Event firstMatch  = new Match(2, 3, Side.SELL, 1000, 50,  0);
        Event secondMatch = new Match(1, 3, Side.SELL,  999, 50, 50);

        assertEquals(asList(firstBid, secondBid, firstMatch, secondMatch),
                events.collect());
    }

    @Test
    public void partialMarketBuy() {
        market.enter(1, Side.SELL, 1000, 50);
        market.enter(2, Side.BUY,  100);

        Event bid    = new Add(1, Side.SELL, 1000, 50);
        Event match  = new Match(1, 2, Side.BUY, 1000, 50, 0);
        Event cancel = new Cancel(2, 50, 0);

        assertEquals(asList(bid, match, cancel), events.collect());
    }

    @Test
    public void partialMarketSell() {
        market.enter(1, Side.BUY,  1000, 50);
        market.enter(2, Side.SELL, 100);

        Event bid    = new Add(1, Side.BUY, 1000, 50);
        Event match  = new Match(1, 2, Side.SELL, 1000, 50, 0);
        Event cancel = new Cancel(2, 50, 0);

        assertEquals(asList(bid, match, cancel), events.collect());
    }

    @Test
    public void limitBuy() {
        market.enter(1, Side.SELL, 1000, 100);
        market.enter(2, Side.BUY,  1000, 100);

        Event ask   = new Add(1, Side.SELL, 1000, 100);
        Event match = new Match(1, 2, Side.BUY, 1000, 100, 0);

        assertEquals(asList(ask, match), events.collect());
    }

    @Test
    public void limitSell() {
        market.enter(1, Side.BUY,  1000, 100);
        market.enter(2, Side.SELL, 1000, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 100);
        Event match = new Match(1, 2, Side.SELL, 1000, 100, 0);

        assertEquals(asList(bid, match), events.collect());
    }

    @Test
    public void multiLevelLimitBuy() {
        market.enter(1, Side.SELL, 1000, 100);
        market.enter(2, Side.SELL, 1001, 100);
        market.enter(3, Side.SELL,  999,  50);
        market.enter(4, Side.BUY,  1000, 100);

        Event firstAsk  = new Add(1, Side.SELL, 1000, 100);
        Event secondAsk = new Add(2, Side.SELL, 1001, 100);
        Event thirdAsk  = new Add(3, Side.SELL,  999,  50);

        Event firstMatch  = new Match(3, 4, Side.BUY,  999, 50,  0);
        Event secondMatch = new Match(1, 4, Side.BUY, 1000, 50, 50);

        assertEquals(asList(firstAsk, secondAsk, thirdAsk, firstMatch, secondMatch),
                events.collect());
    }

    @Test
    public void multiLevelLimitSell() {
        market.enter(1, Side.BUY,  1000, 100);
        market.enter(2, Side.BUY,   999, 100);
        market.enter(3, Side.BUY,  1001,  50);
        market.enter(4, Side.SELL, 1000, 100);

        Event firstBid  = new Add(1, Side.BUY, 1000, 100);
        Event secondBid = new Add(2, Side.BUY,  999, 100);
        Event thirdBid  = new Add(3, Side.BUY, 1001,  50);

        Event firstMatch  = new Match(3, 4, Side.SELL, 1001, 50,  0);
        Event secondMatch = new Match(1, 4, Side.SELL, 1000, 50, 50);

        assertEquals(asList(firstBid, secondBid, thirdBid, firstMatch, secondMatch),
                events.collect());
    }

    @Test
    public void partialLimitBuy() {
        market.enter(1, Side.SELL, 1000,  50);
        market.enter(2, Side.BUY,  1000, 100);

        Event ask   = new Add(1, Side.SELL, 1000, 50);
        Event match = new Match(1, 2, Side.BUY, 1000, 50, 0);
        Event bid   = new Add(2, Side.BUY, 1000, 50);

        assertEquals(asList(ask, match, bid), events.collect());
    }

    @Test
    public void partialLimitSell() {
        market.enter(1, Side.BUY,  1000,  50);
        market.enter(2, Side.SELL, 1000, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 50);
        Event match = new Match(1, 2, Side.SELL, 1000, 50, 0);
        Event ask   = new Add(2, Side.SELL, 1000, 50);

        assertEquals(asList(bid, match, ask), events.collect());
    }

    @Test
    public void partialBidFill() {
        market.enter(1, Side.BUY,  1000, 100);
        market.enter(2, Side.SELL, 1000,  50);
        market.enter(3, Side.SELL, 1000,  50);
        market.enter(4, Side.SELL, 1000,  50);

        Event bid = new Add(1, Side.BUY, 1000, 100);

        Event firstMatch  = new Match(1, 2, Side.SELL, 1000, 50, 50);
        Event secondMatch = new Match(1, 3, Side.SELL, 1000, 50,  0);

        Event ask = new Add(4, Side.SELL, 1000, 50);

        assertEquals(asList(bid, firstMatch, secondMatch, ask), events.collect());
    }

    @Test
    public void partialAskFill() {
        market.enter(1, Side.SELL, 1000, 100);
        market.enter(2, Side.BUY,  1000,  50);
        market.enter(3, Side.BUY,  1000,  50);
        market.enter(4, Side.BUY,  1000,  50);

        Event ask = new Add(1, Side.SELL, 1000, 100);

        Event firstMatch  = new Match(1, 2, Side.BUY, 1000, 50, 50);
        Event secondMatch = new Match(1, 3, Side.BUY, 1000, 50,  0);

        Event bid = new Add(4, Side.BUY, 1000, 50);

        assertEquals(asList(ask, firstMatch, secondMatch, bid), events.collect());
    }

    @Test
    public void cancel() {
        market.enter(1, Side.BUY, 1000, 100);
        market.cancel(1, 0);
        market.enter(2, Side.SELL, 1000, 100);

        Event bid    = new Add(1, Side.BUY, 1000, 100);
        Event cancel = new Cancel(1, 100, 0);
        Event ask    = new Add(2, Side.SELL, 1000, 100);

        assertEquals(asList(bid, cancel, ask), events.collect());
    }

    @Test
    public void partialCancel() {
        market.enter(1, Side.BUY, 1000, 100);
        market.cancel(1, 75);
        market.enter(2, Side.SELL, 1000, 100);

        Event bid    = new Add(1, Side.BUY, 1000, 100);
        Event cancel = new Cancel(1, 25, 75);
        Event match  = new Match(1, 2, Side.SELL, 1000, 75, 0);
        Event ask    = new Add(2, Side.SELL, 1000, 25);

        assertEquals(asList(bid, cancel, match, ask), events.collect());
    }

    @Test
    public void ineffectiveCancel() {
        market.enter(1, Side.BUY, 1000, 100);
        market.cancel(1, 100);
        market.cancel(1, 150);
        market.cancel(1, 100);
        market.enter(2, Side.SELL, 1000, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 100);
        Event match = new Match(1, 2, Side.SELL, 1000, 100, 0);

        assertEquals(asList(bid, match), events.collect());
    }

}
