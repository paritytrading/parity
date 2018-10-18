package com.paritytrading.parity.match;

import static com.paritytrading.parity.match.OrderBookEvents.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class OrderBookTest {

    private OrderBookEvents events;

    private OrderBook book;

    @Before
    public void setUp() {
        events = new OrderBookEvents();
        book   = new OrderBook(events);
    }

    @Test
    public void bid() {
        book.enter(1, Side.BUY, 1000, 100);

        Event bid = new Add(1, Side.BUY, 1000, 100);

        assertEquals(asList(bid), events.collect());
    }

    @Test
    public void ask() {
        book.enter(1, Side.SELL, 1000, 100);

        Event ask = new Add(1, Side.SELL, 1000, 100);

        assertEquals(asList(ask), events.collect());
    }

    @Test
    public void buy() {
        book.enter(1, Side.SELL, 1000, 100);
        book.enter(2, Side.BUY,  1000, 100);

        Event ask   = new Add(1, Side.SELL, 1000, 100);
        Event match = new Match(1, 2, Side.BUY, 1000, 100, 0);

        assertEquals(asList(ask, match), events.collect());
    }

    @Test
    public void sell() {
        book.enter(1, Side.BUY,  1000, 100);
        book.enter(2, Side.SELL, 1000, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 100);
        Event match = new Match(1, 2, Side.SELL, 1000, 100, 0);

        assertEquals(asList(bid, match), events.collect());
    }

    @Test
    public void multiLevelBuy() {
        book.enter(1, Side.SELL, 1000, 100);
        book.enter(2, Side.SELL, 1001, 100);
        book.enter(3, Side.SELL,  999,  50);
        book.enter(4, Side.BUY,  1000, 100);

        Event firstAsk  = new Add(1, Side.SELL, 1000, 100);
        Event secondAsk = new Add(2, Side.SELL, 1001, 100);
        Event thirdAsk  = new Add(3, Side.SELL,  999,  50);

        Event firstMatch  = new Match(3, 4, Side.BUY,  999, 50,  0);
        Event secondMatch = new Match(1, 4, Side.BUY, 1000, 50, 50);

        assertEquals(asList(firstAsk, secondAsk, thirdAsk, firstMatch, secondMatch),
                events.collect());
    }

    @Test
    public void multiLevelSell() {
        book.enter(1, Side.BUY,  1000, 100);
        book.enter(2, Side.BUY,   999, 100);
        book.enter(3, Side.BUY,  1001,  50);
        book.enter(4, Side.SELL, 1000, 100);

        Event firstBid  = new Add(1, Side.BUY, 1000, 100);
        Event secondBid = new Add(2, Side.BUY,  999, 100);
        Event thirdBid  = new Add(3, Side.BUY, 1001,  50);

        Event firstMatch  = new Match(3, 4, Side.SELL, 1001, 50,  0);
        Event secondMatch = new Match(1, 4, Side.SELL, 1000, 50, 50);

        assertEquals(asList(firstBid, secondBid, thirdBid, firstMatch, secondMatch),
                events.collect());
    }

    @Test
    public void partialBuy() {
        book.enter(1, Side.SELL, 1000,  50);
        book.enter(2, Side.BUY,  1000, 100);

        Event ask   = new Add(1, Side.SELL, 1000, 50);
        Event match = new Match(1, 2, Side.BUY, 1000, 50, 0);
        Event bid   = new Add(2, Side.BUY, 1000, 50);

        assertEquals(asList(ask, match, bid), events.collect());
    }

    @Test
    public void partialSell() {
        book.enter(1, Side.BUY,  1000,  50);
        book.enter(2, Side.SELL, 1000, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 50);
        Event match = new Match(1, 2, Side.SELL, 1000, 50, 0);
        Event ask   = new Add(2, Side.SELL, 1000, 50);

        assertEquals(asList(bid, match, ask), events.collect());
    }

    @Test
    public void partialBidFill() {
        book.enter(1, Side.BUY,  1000, 100);
        book.enter(2, Side.SELL, 1000,  50);
        book.enter(3, Side.SELL, 1000,  50);
        book.enter(4, Side.SELL, 1000,  50);

        Event bid = new Add(1, Side.BUY, 1000, 100);

        Event firstMatch  = new Match(1, 2, Side.SELL, 1000, 50, 50);
        Event secondMatch = new Match(1, 3, Side.SELL, 1000, 50,  0);

        Event ask = new Add(4, Side.SELL, 1000, 50);

        assertEquals(asList(bid, firstMatch, secondMatch, ask), events.collect());
    }

    @Test
    public void partialAskFill() {
        book.enter(1, Side.SELL, 1000, 100);
        book.enter(2, Side.BUY,  1000,  50);
        book.enter(3, Side.BUY,  1000,  50);
        book.enter(4, Side.BUY,  1000,  50);

        Event ask = new Add(1, Side.SELL, 1000, 100);

        Event firstMatch  = new Match(1, 2, Side.BUY, 1000, 50, 50);
        Event secondMatch = new Match(1, 3, Side.BUY, 1000, 50,  0);

        Event bid = new Add(4, Side.BUY, 1000, 50);

        assertEquals(asList(ask, firstMatch, secondMatch, bid), events.collect());
    }

    @Test
    public void cancel() {
        book.enter(1, Side.BUY, 1000, 100);
        book.cancel(1, 0);
        book.enter(2, Side.SELL, 1000, 100);

        Event bid    = new Add(1, Side.BUY, 1000, 100);
        Event cancel = new Cancel(1, 100, 0);
        Event ask    = new Add(2, Side.SELL, 1000, 100);

        assertEquals(asList(bid, cancel, ask), events.collect());
    }

    @Test
    public void partialCancel() {
        book.enter(1, Side.BUY, 1000, 100);
        book.cancel(1, 75);
        book.enter(2, Side.SELL, 1000, 100);

        Event bid    = new Add(1, Side.BUY, 1000, 100);
        Event cancel = new Cancel(1, 25, 75);
        Event match  = new Match(1, 2, Side.SELL, 1000, 75, 0);
        Event ask    = new Add(2, Side.SELL, 1000, 25);

        assertEquals(asList(bid, cancel, match, ask), events.collect());
    }

    @Test
    public void ineffectiveCancel() {
        book.enter(1, Side.BUY, 1000, 100);
        book.cancel(1, 100);
        book.cancel(1, 150);
        book.cancel(1, 100);
        book.enter(2, Side.SELL, 1000, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 100);
        Event match = new Match(1, 2, Side.SELL, 1000, 100, 0);

        assertEquals(asList(bid, match), events.collect());
    }

    @Test
    public void unknownOrder() {
        book.enter(1, Side.BUY, 1000, 100);
        book.cancel(1, 0);
        book.cancel(1, 0);

        Event bid    = new Add(1, Side.BUY, 1000, 100);
        Event cancel = new Cancel(1, 100, 0);

        assertEquals(asList(bid, cancel), events.collect());
    }

    @Test
    public void reuseOrderId() {
        book.enter(1, Side.BUY,  1000, 100);
        book.enter(2, Side.SELL, 1000, 100);
        book.enter(1, Side.BUY,  1000, 100);

        Event firstBid  = new Add(1, Side.BUY, 1000, 100);
        Event match     = new Match(1, 2, Side.SELL, 1000, 100, 0);
        Event secondBid = new Add(1, Side.BUY, 1000, 100);

        assertEquals(asList(firstBid, match, secondBid), events.collect());
    }

}
