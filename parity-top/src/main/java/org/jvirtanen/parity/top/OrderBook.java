package org.jvirtanen.parity.top;

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

        if (bestBidLevel != null && bestAskLevel != null)
            listener.bbo(instrument, bestBidLevel.getPrice(), bestBidLevel.getSize(),
                    bestAskLevel.getPrice(), bestAskLevel.getSize());
    }

}
