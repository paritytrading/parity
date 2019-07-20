/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        public double bidSize;

        /**
         * The ask price or zero if no ask price is available.
         */
        public double askPrice;

        /**
         * The ask size or zero if no ask size is available.
         */
        public double askSize;
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
        public double size;

        /**
         * The side of the resting order or {@code UNKNOWN} if the side of the
         * resting order is not available.
         */
        public char side;
    }

}
