package com.paritytrading.parity.top;

import it.unimi.dsi.fastutil.longs.Long2LongRBTreeMap;

class OrderBook {

    private long instrument;

    private Long2LongRBTreeMap bids;
    private Long2LongRBTreeMap asks;

    public OrderBook(long instrument) {
        this.instrument = instrument;

        this.bids = new Long2LongRBTreeMap(BidComparator.INSTANCE);
        this.asks = new Long2LongRBTreeMap(AskComparator.INSTANCE);
    }

    public long getInstrument() {
        return instrument;
    }

    public void add(Side side, long price, long quantity) {
        Long2LongRBTreeMap levels = getLevels(side);

        long size = levels.get(price);

        levels.put(price, size + quantity);
    }

    public void update(Side side, long price, long quantity) {
        Long2LongRBTreeMap levels = getLevels(side);

        long oldSize = levels.get(price);
        long newSize = oldSize + quantity;

        if (newSize > 0)
            levels.put(price, newSize);
        else
            levels.remove(price);
    }

    private Long2LongRBTreeMap getLevels(Side side) {
        switch (side) {
        case BUY:
            return bids;
        case SELL:
            return asks;
        }

        return null;
    }

    public long getBestPrice(Side side) {
        return getLevels(side).firstLongKey();
    }

    public void bbo(MarketListener listener) {
        long bidPrice = 0;
        long bidSize  = 0;

        if (!bids.isEmpty()) {
            bidPrice = bids.firstLongKey();
            bidSize  = bids.get(bidPrice);
        }

        long askPrice = 0;
        long askSize  = 0;

        if (!asks.isEmpty()) {
            askPrice = asks.firstLongKey();
            askSize  = asks.get(askPrice);
        }

        listener.bbo(instrument, bidPrice, bidSize, askPrice, askSize);
    }

}
