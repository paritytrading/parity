package com.paritytrading.parity.reporter;

import java.util.Locale;

abstract class TradeListener {

    public abstract void trade(Trade event);

    protected void printf(String format, Object... args) {
        System.out.printf(Locale.US, format, args);
    }

}
