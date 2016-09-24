package com.paritytrading.parity.file.taq;

/**
 * Common definitions.
 */
public class TAQ {

    static final char FIELD_SEPARATOR  = '\t';
    static final char RECORD_SEPARATOR = '\n';

    static final char RECORD_TYPE_QUOTE = 'Q';
    static final char RECORD_TYPE_TRADE = 'T';

    public static final char BUY     = 'B';
    public static final char SELL    = 'S';
    public static final char UNKNOWN = ' ';

    private TAQ() {
    }

    /**
     * A Quote record.
     */
    public static class Quote {

        /**
         * The date.
         */
        public String date;

        /**
         * The timestamp in milliseconds.
         */
        public long timestampMillis;

        /**
         * The instrument.
         */
        public String instrument;

        /**
         * The bid price or zero if no bid price is available.
         */
        public double bidPrice;

        /**
         * The bid size or zero if no bid size is available.
         */
        public long bidSize;

        /**
         * The ask price or zero if no ask price is available.
         */
        public double askPrice;

        /**
         * The ask size or zero if no ask size is available.
         */
        public long askSize;
    }

    /**
     * A Trade record.
     */
    public static class Trade {

        /**
         * The date.
         */
        public String date;

        /**
         * The timestamp in milliseconds.
         */
        public long timestampMillis;

        /**
         * The instrument.
         */
        public String instrument;

        /**
         * The trade price.
         */
        public double price;

        /**
         * The trade size.
         */
        public long size;

        /**
         * The side of the resting order or <code>UNKNOWN</code> if the side
         * of the resting order is not available.
         */
        public char side;
    }

}
