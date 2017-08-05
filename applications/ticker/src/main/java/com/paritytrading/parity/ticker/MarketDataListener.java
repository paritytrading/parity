package com.paritytrading.parity.ticker;

import com.paritytrading.parity.book.MarketListener;

abstract class MarketDataListener implements MarketListener {

    private long timestamp;

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long timestampMillis() {
        return timestamp / 1_000_000;
    }

}
