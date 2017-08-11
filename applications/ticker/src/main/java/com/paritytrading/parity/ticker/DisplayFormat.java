package com.paritytrading.parity.ticker;

import static com.paritytrading.parity.ticker.MarketDataListener.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.book.OrderBook;
import com.paritytrading.parity.book.Side;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.TableHeader;
import com.paritytrading.parity.util.Timestamps;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import java.util.Locale;

class DisplayFormat extends MarketDataListener {

    private Instruments instruments;

    private Long2ObjectArrayMap<Trade> trades;

    private long timestamp;

    private int counter;

    private String placeholder;

    public DisplayFormat(Instruments instruments) {
        this.instruments = instruments;

        this.trades = new Long2ObjectArrayMap<>();

        for (Instrument instrument : instruments)
            trades.put(instrument.asLong(), new Trade());

        int priceWidth = instruments.getPriceWidth();
        int sizeWidth  = instruments.getSizeWidth();

        TableHeader header = new TableHeader();

        header.add("Timestamp",        12);
        header.add("Inst",              8);
        header.add("Bid Px",   priceWidth);
        header.add("Bid Size",  sizeWidth);
        header.add("Ask Px",   priceWidth);
        header.add("Ask Size",  sizeWidth);
        header.add("Last Px",  priceWidth);
        header.add("Last Size", sizeWidth);

        printf("\n");
        printf(header.format());

        String pricePlaceholder = instruments.getPricePlaceholder();
        String sizePlaceholder  = instruments.getSizePlaceholder();

        this.placeholder = String.format("%s %s ", pricePlaceholder, sizePlaceholder);
    }

    @Override
    public void update(OrderBook book, boolean bbo) {
        if (!bbo)
            return;

        Instrument instrument = instruments.get(book.getInstrument());

        long bidPrice = book.getBestBidPrice();
        long bidSize  = book.getBidSize(bidPrice);

        long askPrice = book.getBestAskPrice();
        long askSize  = book.getAskSize(askPrice);

        String priceFormat = instrument.getPriceFormat();
        String sizeFormat  = instrument.getSizeFormat();

        double priceFactor = instrument.getPriceFactor();
        double sizeFactor  = instrument.getSizeFactor();

        printf("%12s %-8s ", Timestamps.format(timestampMillis()), instrument.asString());

        if (bidSize != 0) {
            printf(priceFormat, bidPrice / priceFactor);
            print(" ");
            printf(sizeFormat, bidSize / sizeFactor);
            print(" ");
        }
        else {
            print(placeholder);
        }

        if (askSize != 0) {
            printf(priceFormat, askPrice / priceFactor);
            print(" ");
            printf(sizeFormat, askSize / sizeFactor);
            print(" ");
        }
        else {
            print(placeholder);
        }

        Trade trade = trades.get(instrument.asLong());

        if (trade.size != 0) {
            printf(priceFormat, trade.price / priceFactor);
            print(" ");
            printf(sizeFormat, trade.size / sizeFactor);
            print("\n");
        }
        else {
            println(placeholder);
        }
    }

    @Override
    public void trade(OrderBook book, Side side, long price, long size) {
        Trade trade = trades.get(book.getInstrument());

        trade.price = price;
        trade.size  = size;
    }

    private void printf(String format, Object... args) {
        System.out.printf(Locale.US, format, args);
    }

    private void print(String x) {
        System.out.print(x);
    }

    private void println(String x) {
        System.out.println(x);
    }

    private static class Trade {
        public long price;
        public long size;
    }

}
