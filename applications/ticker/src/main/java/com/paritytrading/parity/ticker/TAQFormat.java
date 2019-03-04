package com.paritytrading.parity.ticker;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import com.paritytrading.parity.book.OrderBook;
import com.paritytrading.parity.book.Side;
import com.paritytrading.parity.file.taq.TAQ;
import com.paritytrading.parity.file.taq.TAQConfig;
import com.paritytrading.parity.file.taq.TAQWriter;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import java.nio.charset.Charset;
import java.time.LocalDate;

class TAQFormat extends MarketDataListener {

    private final Instruments instruments;

    private final TAQ.Quote quote;
    private final TAQ.Trade trade;

    private final TAQWriter writer;

    TAQFormat(Instruments instruments) {
        this.instruments = instruments;

        this.quote = new TAQ.Quote();
        this.trade = new TAQ.Trade();

        String date = ISO_LOCAL_DATE.format(LocalDate.now());

        quote.date = date;
        trade.date = date;

        TAQConfig.Builder builder = new TAQConfig.Builder()
            .setEncoding(Charset.defaultCharset());

        for (Instrument instrument : instruments) {
            int priceFractionDigits = instrument.getPriceFractionDigits();
            int sizeFractionDigits  = instrument.getSizeFractionDigits();

            builder.setPriceFractionDigits(instrument.asString(), priceFractionDigits);
            builder.setSizeFractionDigits(instrument.asString(),  sizeFractionDigits);
        }

        writer = new TAQWriter(System.out, builder.build());
        writer.flush();
    }

    @Override
    public void update(OrderBook book, boolean bbo) {
        if (!bbo)
            return;

        Instrument instrument = instruments.get(book.getInstrument());

        double priceFactor = instrument.getPriceFactor();
        double sizeFactor  = instrument.getSizeFactor();

        long bidPrice = book.getBestBidPrice();
        long askPrice = book.getBestAskPrice();

        quote.timestampMillis = timestampMillis();
        quote.instrument      = instrument.asString();
        quote.bidPrice        = bidPrice / priceFactor;
        quote.bidSize         = book.getBidSize(bidPrice) / sizeFactor;
        quote.askPrice        = askPrice / priceFactor;
        quote.askSize         = book.getAskSize(askPrice) / sizeFactor;

        writer.write(quote);
        writer.flush();
    }

    @Override
    public void trade(OrderBook book, Side side, long price, long size) {
        Instrument instrument = instruments.get(book.getInstrument());

        trade.timestampMillis = timestampMillis();
        trade.instrument      = instrument.asString();
        trade.price           = price / instrument.getPriceFactor();
        trade.size            = size  / instrument.getSizeFactor();
        trade.side            = side(side);

        writer.write(trade);
        writer.flush();
    }

    private char side(Side side) {
        return side == Side.BUY ? TAQ.BUY : TAQ.SELL;
    }

}
