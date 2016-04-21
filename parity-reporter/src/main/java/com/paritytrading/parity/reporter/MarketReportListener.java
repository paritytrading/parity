package com.paritytrading.parity.reporter;

import com.paritytrading.parity.net.pmr.PMR;
import com.paritytrading.parity.net.pmr.PMRListener;
import java.util.Locale;

abstract class MarketReportListener implements PMRListener {

    protected static final double PRICE_FACTOR = 10000.0;

    protected static final long NANOS_PER_MILLI = 1000 * 1000;

    @Override
    public void order(PMR.Order message) {
    }

    @Override
    public void cancel(PMR.Cancel message) {
    }

    protected void printf(String format, Object... args) {
        System.out.printf(Locale.US, format, args);
    }

}
