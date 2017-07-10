package com.paritytrading.parity.file.taq;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * A configuration.
 */
public class TAQConfig {

    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.US);

    /**
     * The default configuration.
     */
    public static final TAQConfig DEFAULTS = new TAQConfig.Builder().build();

    private Charset encoding;

    private DecimalFormat priceFormat;
    private DecimalFormat sizeFormat;

    private TAQConfig(Charset encoding, DecimalFormat priceFormat, DecimalFormat sizeFormat) {
        this.encoding = encoding;

        this.priceFormat = priceFormat;
        this.sizeFormat  = sizeFormat;
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
     * Get the price format.
     *
     * @return the price format
     */
    public DecimalFormat getPriceFormat() {
        return priceFormat;
    }

    /**
     * Get the size format.
     *
     * @return the size format
     */
    public DecimalFormat getSizeFormat() {
        return sizeFormat;
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

        private DecimalFormat priceFormat;
        private DecimalFormat sizeFormat;

        /**
         * Create a configuration builder.
         */
        public Builder() {
            encoding = US_ASCII;

            priceFormat = newFormat();
            sizeFormat  = newFormat();

            setFractionDigits(priceFormat, 2);
            setFractionDigits(sizeFormat,  0);
        }

        /**
         * Set the encoding.
         *
         * @param encoding the encoding
         * @return this instance
         */
        public Builder setEncoding(Charset encoding) {
            this.encoding = encoding;

            return this;
        }

        /**
         * Set the number of digits in the fractional part of a price.
         *
         * @param fractionDigits the number of digits in the fractional part
         *     of a price
         * @return this instance
         */
        public Builder setPriceFractionDigits(int fractionDigits) {
            setFractionDigits(priceFormat, fractionDigits);

            return this;
        }

        /**
         * Set the number of digits in the fractional part of a size.
         *
         * @param fractionDigits the number of digits in the fractional part
         *     of a size
         * @return this instance
         */
        public Builder setSizeFractionDigits(int fractionDigits) {
            setFractionDigits(sizeFormat, fractionDigits);

            return this;
        }

        /**
         * Build the configuration.
         *
         * @return the configuration
         */
        public TAQConfig build() {
            return new TAQConfig(encoding, priceFormat, sizeFormat);
        }

    }

    private static DecimalFormat newFormat() {
        return new DecimalFormat("0", SYMBOLS);
    }

    private static void setFractionDigits(DecimalFormat format, int fractionDigits) {
        format.setMinimumFractionDigits(fractionDigits);
        format.setMaximumFractionDigits(fractionDigits);
    }

}
