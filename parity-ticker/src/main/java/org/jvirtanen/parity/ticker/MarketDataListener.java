package org.jvirtanen.parity.ticker;

import org.jvirtanen.parity.top.MarketListener;

abstract class MarketDataListener implements MarketListener {

    private static final long MILLIS_PER_SEC = 1000;

    private static final long NANOS_PER_MILLI = 1000 * 1000;

    private long second;

    private long timestamp;

    public void seconds(long second) {
        this.second = second;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long timestampMillis() {
        return MILLIS_PER_SEC * second + timestamp / NANOS_PER_MILLI;
    }

}
