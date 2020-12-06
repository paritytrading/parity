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
package com.paritytrading.parity.match;

import static com.paritytrading.parity.match.OrderBookEvents.*;
import static java.util.Arrays.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderBookTest {

    private OrderBookEvents events;

    private OrderBook book;

    @BeforeEach
    void setUp() {
        events = new OrderBookEvents();
        book   = new OrderBook(events);
    }

    @Test
    void bid() {
        book.enter(1, Side.BUY, 1000, 100);

        Event bid = new Add(1, Side.BUY, 1000, 100);

        assertEquals(asList(bid), events.collect());
    }

    @Test
    void ask() {
        book.enter(1, Side.SELL, 1000, 100);

        Event ask = new Add(1, Side.SELL, 1000, 100);

        assertEquals(asList(ask), events.collect());
    }

    @Test
    void buy() {
        book.enter(1, Side.SELL, 1000, 100);
        book.enter(2, Side.BUY,  1000, 100);

        Event ask   = new Add(1, Side.SELL, 1000, 100);
        Event match = new Match(1, 2, Side.BUY, 1000, 100, 0);

        assertEquals(asList(ask, match), events.collect());
    }

    @Test
    void sell() {
        book.enter(1, Side.BUY,  1000, 100);
        book.enter(2, Side.SELL, 1000, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 100);
        Event match = new Match(1, 2, Side.SELL, 1000, 100, 0);

        assertEquals(asList(bid, match), events.collect());
    }

    @Test
    void multiLevelBuy() {
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
    void multiLevelSell() {
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
    void partialBuy() {
        book.enter(1, Side.SELL, 1000,  50);
        book.enter(2, Side.BUY,  1000, 100);

        Event ask   = new Add(1, Side.SELL, 1000, 50);
        Event match = new Match(1, 2, Side.BUY, 1000, 50, 0);
        Event bid   = new Add(2, Side.BUY, 1000, 50);

        assertEquals(asList(ask, match, bid), events.collect());
    }

    @Test
    void partialSell() {
        book.enter(1, Side.BUY,  1000,  50);
        book.enter(2, Side.SELL, 1000, 100);

        Event bid   = new Add(1, Side.BUY, 1000, 50);
        Event match = new Match(1, 2, Side.SELL, 1000, 50, 0);
        Event ask   = new Add(2, Side.SELL, 1000, 50);

        assertEquals(asList(bid, match, ask), events.collect());
    }

    @Test
    void partialBidFill() {
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
    void partialAskFill() {
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
    void cancel() {
        book.enter(1, Side.BUY, 1000, 100);
        book.cancel(1, 0);
        book.enter(2, Side.SELL, 1000, 100);

        Event bid    = new Add(1, Side.BUY, 1000, 100);
        Event cancel = new Cancel(1, 100, 0);
        Event ask    = new Add(2, Side.SELL, 1000, 100);

        assertEquals(asList(bid, cancel, ask), events.collect());
    }

    @Test
    void partialCancel() {
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
    void ineffectiveCancel() {
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
    void unknownOrder() {
        book.enter(1, Side.BUY, 1000, 100);
        book.cancel(1, 0);
        book.cancel(1, 0);

        Event bid    = new Add(1, Side.BUY, 1000, 100);
        Event cancel = new Cancel(1, 100, 0);

        assertEquals(asList(bid, cancel), events.collect());
    }

    @Test
    void reuseOrderId() {
        book.enter(1, Side.BUY,  1000, 100);
        book.enter(2, Side.SELL, 1000, 100);
        book.enter(1, Side.BUY,  1000, 100);

        Event firstBid  = new Add(1, Side.BUY, 1000, 100);
        Event match     = new Match(1, 2, Side.SELL, 1000, 100, 0);
        Event secondBid = new Add(1, Side.BUY, 1000, 100);

        assertEquals(asList(firstBid, match, secondBid), events.collect());
    }

}
