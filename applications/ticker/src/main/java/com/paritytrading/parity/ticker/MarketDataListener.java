package com.paritytrading.parity.ticker;

import com.paritytrading.parity.book.MarketListener;

abstract class MarketDataListener implements MarketListener {

    private long timestamp;

    void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    long timestampMillis() {
        return timestamp / 1_000_000;
    }

}
