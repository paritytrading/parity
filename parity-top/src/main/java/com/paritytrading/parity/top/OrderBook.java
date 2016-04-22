package com.paritytrading.parity.top;

class OrderBook {

    private long instrument;

    private Orders bids;
    private Orders asks;

    public OrderBook(long instrument) {
        this.instrument = instrument;

        this.bids = new Orders(this, Side.BUY,  BidComparator.INSTANCE);
        this.asks = new Orders(this, Side.SELL, AskComparator.INSTANCE);
    }

    public long getInstrument() {
        return instrument;
    }

    public Order add(Side side, long price, long size) {
        switch (side) {
            case BUY:
                return bids.add(price, size);
            case SELL:
                return asks.add(price, size);
        }

        return null;
    }

    public void bbo(MarketListener listener) {
        Level bestBidLevel = bids.getBestLevel();
        Level bestAskLevel = asks.getBestLevel();

        long bidPrice = 0;
        long bidSize  = 0;

        if (bestBidLevel != null) {
            bidPrice = bestBidLevel.getPrice();
            bidSize  = bestBidLevel.getSize();
        }

        long askPrice = 0;
        long askSize  = 0;

        if (bestAskLevel != null) {
            askPrice = bestAskLevel.getPrice();
            askSize  = bestAskLevel.getSize();
        }

        listener.bbo(instrument, bidPrice, bidSize, askPrice, askSize);
    }

}
