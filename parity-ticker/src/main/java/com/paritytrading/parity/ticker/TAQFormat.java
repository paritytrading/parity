package com.paritytrading.parity.ticker;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.book.OrderBook;
import com.paritytrading.parity.book.Side;
import com.paritytrading.parity.file.taq.TAQ;
import com.paritytrading.parity.file.taq.TAQWriter;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import java.nio.charset.Charset;
import java.time.LocalDate;

class TAQFormat extends MarketDataListener {

    private Long2ObjectArrayMap<String> instruments;

    private TAQ.Quote quote;
    private TAQ.Trade trade;

    private TAQWriter writer;

    public TAQFormat() {
        instruments = new Long2ObjectArrayMap<>();

        quote = new TAQ.Quote();
        trade = new TAQ.Trade();

        String date = ISO_LOCAL_DATE.format(LocalDate.now());

        quote.date = date;
        trade.date = date;

        writer = new TAQWriter(System.out, Charset.defaultCharset());
        writer.flush();
    }

    @Override
    public void update(OrderBook book, boolean bbo) {
        if (!bbo)
            return;

        quote.timestampMillis = timestampMillis();
        quote.instrument      = instrument(book.getInstrument());
        quote.bidPrice        = book.getBestBidPrice();
        quote.bidSize         = book.getBidSize(quote.bidPrice);
        quote.askPrice        = book.getBestAskPrice();
        quote.askSize         = book.getAskSize(quote.askPrice);

        writer.write(quote);
        writer.flush();
    }

    @Override
    public void trade(OrderBook book, Side side, long price, long size) {
        trade.timestampMillis = timestampMillis();
        trade.instrument      = instrument(book.getInstrument());
        trade.price           = price;
        trade.size            = size;
        trade.side            = side(side);

        writer.write(trade);
        writer.flush();
    }

    private String instrument(long instrument) {
        String cached = instruments.get(instrument);
        if (cached == null) {
            cached = ASCII.unpackLong(instrument).trim();

            instruments.put(instrument, cached);
        }

        return cached;
    }

    private char side(Side side) {
        switch (side) {
        case BUY:
            return TAQ.BUY;
        case SELL:
            return TAQ.SELL;
        }

        return 0;
    }

}
