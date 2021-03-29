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
package com.paritytrading.parity.book;

import static com.paritytrading.parity.book.MarketEvents.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketTest {

    private static final long INSTRUMENT = 1;

    private MarketEvents events;

    private Market market;

    private OrderBook book;

    @BeforeEach
    void setUp() {
        events = new MarketEvents();
        market = new Market(events);

        book = market.open(INSTRUMENT);
    }

    @Test
    void bbo() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);

        assertEquals(asList(new Level(999, 100, 1001, 200)), levels(book));

        Event updateAfterBid = new Update(INSTRUMENT, true);
        Event updateAfterAsk = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterAsk), events.collect());
    }

    @Test
    void addition() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.BUY,  1000,  50);

        assertEquals(asList(new Level(1000, 50, 1001, 200),
                    new Level(999, 100, 0, 0)), levels(book));

        Event updateAfterFirstBid  = new Update(INSTRUMENT, true);
        Event updateAfterAsk       = new Update(INSTRUMENT, true);
        Event updateAfterSecondBid = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterFirstBid, updateAfterAsk, updateAfterSecondBid),
                events.collect());
    }

    @Test
    void modification() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.modify(2, 100);

        assertEquals(asList(new Level(999, 100, 1001, 100)), levels(book));

        Event updateAfterBid          = new Update(INSTRUMENT, true);
        Event updateAfterAsk          = new Update(INSTRUMENT, true);
        Event updateAfterModification = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterAsk, updateAfterModification),
                events.collect());
    }

    @Test
    void execution() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        assertEquals(0, market.execute(2, 200));

        assertEquals(asList(new Level(999, 100, 1002, 50)), levels(book));

        Event updateAfterBid       = new Update(INSTRUMENT, true);
        Event updateAfterFirstAsk  = new Update(INSTRUMENT, true);
        Event updateAfterSecondAsk = new Update(INSTRUMENT, false);
        Event trade                = new Trade(INSTRUMENT, Side.BUY, 1001, 200);
        Event updateAfterTrade     = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterFirstAsk, updateAfterSecondAsk,
                    trade, updateAfterTrade), events.collect());
    }

    @Test
    void executionWithPrice() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        assertEquals(0, market.execute(2, 200, 1000));

        assertEquals(asList(new Level(999, 100, 1002, 50)), levels(book));

        Event updateAfterBid       = new Update(INSTRUMENT, true);
        Event updateAfterFirstAsk  = new Update(INSTRUMENT, true);
        Event updateAfterSecondAsk = new Update(INSTRUMENT, false);
        Event trade                = new Trade(INSTRUMENT, Side.BUY, 1000, 200);
        Event updateAfterTrade     = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterFirstAsk, updateAfterSecondAsk,
                    trade, updateAfterTrade), events.collect());
    }

    @Test
    void partialExecution() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        assertEquals(100, market.execute(2, 100));

        assertEquals(asList(new Level(999, 100, 1001, 100)), levels(book));

        Event updateAfterBid   = new Update(INSTRUMENT, true);
        Event updateAfterAsk   = new Update(INSTRUMENT, true);
        Event trade            = new Trade(INSTRUMENT, Side.BUY, 1001, 100);
        Event updateAfterTrade = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterAsk, trade, updateAfterTrade),
                events.collect());
    }

    @Test
    void cancellation() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        assertEquals(0, market.cancel(2, 200));

        assertEquals(asList(new Level(999, 100, 1002, 50)), levels(book));

        Event updateAfterBid       = new Update(INSTRUMENT, true);
        Event updateAfterFirstAsk  = new Update(INSTRUMENT, true);
        Event updateAfterSecondAsk = new Update(INSTRUMENT, false);
        Event updateAfterCancel    = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterFirstAsk,
                    updateAfterSecondAsk, updateAfterCancel), events.collect());
    }

    @Test
    void partialCancellation() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        assertEquals(100, market.cancel(2, 100));

        assertEquals(asList(new Level(999, 100, 1001, 100)), levels(book));

        Event updateAfterBid    = new Update(INSTRUMENT, true);
        Event updateAfterAsk    = new Update(INSTRUMENT, true);
        Event updateAfterCancel = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterAsk, updateAfterCancel),
                events.collect());
    }

    @Test
    void deletion() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.add(INSTRUMENT, 3, Side.SELL, 1002,  50);
        market.delete(2);

        assertEquals(asList(new Level(999, 100, 1002, 50)), levels(book));

        Event updateAfterBid       = new Update(INSTRUMENT, true);
        Event updateAfterFirstAsk  = new Update(INSTRUMENT, true);
        Event updateAfterSecondAsk = new Update(INSTRUMENT, false);
        Event updateAfterDelete    = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterFirstAsk,
                    updateAfterSecondAsk, updateAfterDelete), events.collect());
    }

    @Test
    void empty() {
        market.add(INSTRUMENT, 1, Side.BUY,   999, 100);
        market.add(INSTRUMENT, 2, Side.SELL, 1001, 200);
        market.delete(2);
        market.delete(1);

        assertEquals(emptyList(), levels(book));

        Event updateAfterBid          = new Update(INSTRUMENT, true);
        Event updateAfterAsk          = new Update(INSTRUMENT, true);
        Event updateAfterFirstDelete  = new Update(INSTRUMENT, true);
        Event updateAfterSecondDelete = new Update(INSTRUMENT, true);

        assertEquals(asList(updateAfterBid, updateAfterAsk,
                    updateAfterFirstDelete, updateAfterSecondDelete), events.collect());
    }

    @SuppressWarnings("unused")
    private static class Level extends Value {
        public final long bidPrice;
        public final long bidSize;
        public final long askPrice;
        public final long askSize;

        public Level(long bidPrice, long bidSize, long askPrice, long askSize) {
            this.bidPrice = bidPrice;
            this.bidSize  = bidSize;
            this.askPrice = askPrice;
            this.askSize  = askSize;
        }
    }

    private static List<Level> levels(OrderBook book) {
        List<Level> levels = new ArrayList<>();

        LongIterator bidPrices = book.getBidPrices().iterator();
        LongIterator askPrices = book.getAskPrices().iterator();

        while (true) {
            long bidPrice = 0;
            long askPrice = 0;

            if (bidPrices.hasNext())
                bidPrice = bidPrices.nextLong();

            if (askPrices.hasNext())
                askPrice = askPrices.nextLong();

            if (bidPrice == 0 && askPrice == 0)
                break;

            long bidSize = book.getBidSize(bidPrice);
            long askSize = book.getAskSize(askPrice);

            levels.add(new Level(bidPrice, bidSize, askPrice, askSize));
        }

        return levels;
    }

}
