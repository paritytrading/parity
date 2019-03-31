package com.paritytrading.parity.ticker;

import com.paritytrading.parity.book.OrderBook;
import com.paritytrading.parity.book.Side;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.TableHeader;
import com.paritytrading.parity.util.Timestamps;
import java.util.Locale;

class DisplayFormat extends MarketDataListener {

    private final Instruments instruments;

    private final String placeholder;

    DisplayFormat(Instruments instruments) {
        this.instruments = instruments;

        int priceWidth = instruments.getPriceWidth();
        int sizeWidth  = instruments.getSizeWidth();

        TableHeader header = new TableHeader();

        header.add("Timestamp",         12);
        header.add("Inst",               8);
        header.add("Bid Px",    priceWidth);
        header.add("Bid Size",   sizeWidth);
        header.add("Ask Px",    priceWidth);
        header.add("Ask Size",   sizeWidth);
        header.add("T",                  1);
        header.add("Trade Px",  priceWidth);
        header.add("Trade Size", sizeWidth);

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

        print("  ");
        println(placeholder);
    }

    @Override
    public void trade(OrderBook book, Side side, long price, long size) {
        Instrument instrument = instruments.get(book.getInstrument());

        printf("%12s %-8s ", Timestamps.format(timestampMillis()), instrument.asString());

        print(placeholder);
        print(placeholder);

        print(side == Side.BUY ? "B " : "S ");
        printf(instrument.getPriceFormat(), price / instrument.getPriceFactor());
        print(" ");
        printf(instrument.getSizeFormat(), size / instrument.getSizeFactor());
        print("\n");
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

}
