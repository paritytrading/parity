package org.jvirtanen.parity.ticker;

import static org.jvirtanen.parity.util.Strings.*;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import java.util.List;
import java.util.Locale;
import org.jvirtanen.parity.top.Side;

class Display implements MarketDataListener {

    private static final double PRICE_FACTOR = 10000.0;

    private static final String HEADER = "" +
        "Timestamp    Inst     Bid Px    Bid Size   Ask Px    Ask Size   Last Px   Last Size\n" +
        "------------ -------- --------- ---------- --------- ---------- --------- ----------";

    private static final String MISSING = String.format("%9s %10s", "-", "-");

    private Long2ObjectArrayMap<BBO>   bbos;
    private Long2ObjectArrayMap<Trade> trades;

    private long second;
    private long timestamp;

    private int counter;

    public Display(List<String> instruments) {
        bbos   = new Long2ObjectArrayMap<>();
        trades = new Long2ObjectArrayMap<>();

        for (String instrument : instruments) {
            bbos.put(encodeLong(instrument), new BBO());
            trades.put(encodeLong(instrument), new Trade());
        }

        printf("\n%s\n", HEADER);
    }

    @Override
    public void seconds(long second) {
        this.second = second;
    }

    @Override
    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void bbo(long instrument, long bidPrice, long bidSize, long askPrice, long askSize) {
        BBO bbo = bbos.get(instrument);

        bbo.bidPrice = bidPrice;
        bbo.bidSize  = bidSize;
        bbo.askPrice = askPrice;
        bbo.askSize  = askSize;

        update(instrument);
    }

    @Override
    public void trade(long instrument, Side side, long price, long size) {
        Trade trade = trades.get(instrument);

        trade.price = price;
        trade.size  = size;

        update(instrument);
    }

    private void update(long instrument) {
        BBO   bbo   = bbos.get(instrument);
        Trade trade = trades.get(instrument);

        printf("%12s %8s ", Timestamps.format(second, timestamp), decodeLong(instrument));

        if (bbo.bidSize != 0)
            printf("%9.2f %10d ", bbo.bidPrice / PRICE_FACTOR, bbo.bidSize);
        else
            printf("%s ", MISSING);

        if (bbo.askSize != 0)
            printf("%9.2f %10d ", bbo.askPrice / PRICE_FACTOR, bbo.askSize);
        else
            printf("%s ", MISSING);

        if (trade.size != 0)
            printf("%9.2f %10d\n", trade.price / PRICE_FACTOR, trade.size);
        else
            printf("%s\n", MISSING);
    }

    private void printf(String format, Object... args) {
        System.out.printf(Locale.US, format, args);
    }

}
