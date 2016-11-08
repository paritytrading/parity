package com.paritytrading.parity.file.taq;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.nio.charset.Charset;

/**
 * A configuration.
 */
public class TAQConfig {

    /**
     * The default configuration.
     */
    public static final TAQConfig DEFAULTS = new TAQConfig.Builder().build();

    private Charset encoding;

    private int priceFractionDigits;
    private int sizeFractionDigits;

    /**
     * Create a new configuration.
     *
     * @param encoding the encoding
     * @param priceFractionDigits the number of digits in the fractional part
     *   of a price
     * @param sizeFractionDigits the number of digits in the fractional part
     *   of a size
     */
    public TAQConfig(Charset encoding, int priceFractionDigits, int sizeFractionDigits) {
        this.encoding = encoding;

        this.priceFractionDigits = priceFractionDigits;
        this.sizeFractionDigits  = sizeFractionDigits;
    }

    /**
     * Get the encoding.
     *
     * @return the encoding
     */
    public Charset getEncoding() {
        return encoding;
    }

    /**
     * Get the number of digits in the fractional part of a price.
     *
     * @return the number of digits in the fractional part of a size
     */
    public int getPriceFractionDigits() {
        return priceFractionDigits;
    }

    /**
     * Get the number of digits in the fractional part of a size.
     *
     * @return the number of digits in the fractional part of a size
     */
    public int getSizeFractionDigits() {
        return sizeFractionDigits;
    }

    /**
     * A configuration builder. The builder uses the following default values:
     *
     * <ul>
     *   <li>the encoding: US-ASCII</li>
     *   <li>the number of digits in the fractional part of a price: 2</li>
     *   <li>the number of digits in the fractional part of a size: 0</li>
     * </ul>
     */
    public static class Builder {

        private Charset encoding;

        private int priceFractionDigits;
        private int sizeFractionDigits;

        /**
         * Create a configuration builder.
         */
        public Builder() {
            encoding = US_ASCII;

            priceFractionDigits = 2;
            sizeFractionDigits  = 0;
        }

        /**
         * Set the character set.
         *
         * @param cs the character set
         * @return this instance
         */
        public Builder setEncoding(Charset encoding) {
            this.encoding = encoding;

            return this;
        }

        /**
         * Set the number of digits in the fractional part of a price.
         *
         * @param priceFractionDigits the number of digits in the fractional
         *     part of a price
         * @return this instance
         */
        public Builder setPriceFractionDigits(int priceFractionDigits) {
            this.priceFractionDigits = priceFractionDigits;

            return this;
        }

        /**
         * Set the number of digits in the fractional part of a size.
         *
         * @param sizeFractionDigits the number of digits in the fractional
         *     part of a size
         * @return this instance
         */
        public Builder setSizeFractionDigits(int sizeFractionDigits) {
            this.sizeFractionDigits = sizeFractionDigits;

            return this;
        }

        /**
         * Build the configuration.
         *
         * @return the configuration
         */
        public TAQConfig build() {
            return new TAQConfig(encoding, priceFractionDigits, sizeFractionDigits);
        }

    }

}
