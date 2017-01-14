package com.paritytrading.parity.ticker;

import static com.paritytrading.parity.ticker.MarketDataListener.*;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.book.OrderBook;
import com.paritytrading.parity.book.Side;
import com.paritytrading.parity.file.taq.TAQ;
import com.paritytrading.parity.file.taq.TAQConfig;
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

        TAQConfig config = new TAQConfig.Builder()
            .setEncoding(Charset.defaultCharset())
            .build();

        writer = new TAQWriter(System.out, config);
        writer.flush();
    }

    @Override
    public void update(OrderBook book, boolean bbo) {
        if (!bbo)
            return;

        long bidPrice = book.getBestBidPrice();
        long askPrice = book.getBestAskPrice();

        quote.timestampMillis = timestampMillis();
        quote.instrument      = instrument(book.getInstrument());
        quote.bidPrice        = bidPrice / PRICE_FACTOR;
        quote.bidSize         = book.getBidSize(bidPrice);
        quote.askPrice        = askPrice / PRICE_FACTOR;
        quote.askSize         = book.getAskSize(askPrice);

        writer.write(quote);
        writer.flush();
    }

    @Override
    public void trade(OrderBook book, Side side, long price, long size) {
        trade.timestampMillis = timestampMillis();
        trade.instrument      = instrument(book.getInstrument());
        trade.price           = price / PRICE_FACTOR;
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
        return side == Side.BUY ? TAQ.BUY : TAQ.SELL;
    }

}
