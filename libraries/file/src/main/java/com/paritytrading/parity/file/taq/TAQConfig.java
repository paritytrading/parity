package com.paritytrading.parity.file.taq;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    private Map<String, DecimalFormat> priceFormats;
    private Map<String, DecimalFormat> sizeFormats;

    private DecimalFormat defaultPriceFormat;
    private DecimalFormat defaultSizeFormat;

    private TAQConfig(Charset encoding, Map<String, DecimalFormat> priceFormats,
            Map<String, DecimalFormat> sizeFormats, DecimalFormat defaultPriceFormat,
            DecimalFormat defaultSizeFormat) {
        this.encoding = encoding;

        this.priceFormats = priceFormats;
        this.sizeFormats  = sizeFormats;

        this.defaultPriceFormat = defaultPriceFormat;
        this.defaultSizeFormat  = defaultSizeFormat;
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
     * @param instrument the instrument
     * @return the price format
     */
    public DecimalFormat getPriceFormat(String instrument) {
        return priceFormats.getOrDefault(instrument, defaultPriceFormat);
    }

    /**
     * Get the size format.
     *
     * @param instrument the instrument
     * @return the size format
     */
    public DecimalFormat getSizeFormat(String instrument) {
        return sizeFormats.getOrDefault(instrument, defaultSizeFormat);
    }

    /**
     * A configuration builder. The builder uses the following default values:
     *
     * <ul>
     *   <li>the encoding: US-ASCII</li>
     *   <li>the default number of digits in the fractional part of a price: 2</li>
     *   <li>the default number of digits in the fractional part of a size: 0</li>
     * </ul>
     */
    public static class Builder {

        private Charset encoding;

        private Map<String, DecimalFormat> priceFormats;
        private Map<String, DecimalFormat> sizeFormats;

        private DecimalFormat defaultPriceFormat;
        private DecimalFormat defaultSizeFormat;

        /**
         * Create a configuration builder.
         */
        public Builder() {
            encoding = US_ASCII;

            priceFormats = new HashMap<>();
            sizeFormats  = new HashMap<>();

            defaultPriceFormat = newFormat();
            defaultSizeFormat  = newFormat();

            setFractionDigits(defaultPriceFormat, 2);
            setFractionDigits(defaultSizeFormat,  0);
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
         * @param instrument the instrument
         * @param fractionDigits the number of digits in the fractional part
         *     of a price
         * @return this instance
         */
        public Builder setPriceFractionDigits(String instrument, int fractionDigits) {
            setFractionDigits(getFormat(priceFormats, instrument), fractionDigits);

            return this;
        }

        /**
         * Set the number of digits in the fractional part of a size.
         *
         * @param instrument the instrument
         * @param fractionDigits the number of digits in the fractional part
         *     of a size
         * @return this instance
         */
        public Builder setSizeFractionDigits(String instrument, int fractionDigits) {
            setFractionDigits(getFormat(sizeFormats, instrument), fractionDigits);

            return this;
        }

        /**
         * Set the default number of digits in the fractional part of a price.
         *
         * @param fractionDigits the default number of digits in the
         *     fractional part of a price
         * @return this instance
         */
        public Builder setPriceFractionDigits(int fractionDigits) {
            setFractionDigits(defaultPriceFormat, fractionDigits);

            return this;
        }

        /**
         * Set the default number of digits in the fractional part of a size.
         *
         * @param fractionDigits the default number of digits in the
         *     fractional part of a size
         * @return this instance
         */
        public Builder setSizeFractionDigits(int fractionDigits) {
            setFractionDigits(defaultSizeFormat, fractionDigits);

            return this;
        }

        /**
         * Build the configuration.
         *
         * @return the configuration
         */
        public TAQConfig build() {
            return new TAQConfig(encoding, priceFormats, sizeFormats,
                    defaultPriceFormat, defaultSizeFormat);
        }

    }

    private static DecimalFormat newFormat() {
        return new DecimalFormat("0", SYMBOLS);
    }

    private static DecimalFormat getFormat(Map<String, DecimalFormat> formats, String instrument) {
        DecimalFormat format = formats.get(instrument);
        if (format == null) {
            format = newFormat();

            formats.put(instrument, format);
        }

        return format;
    }

    private static void setFractionDigits(DecimalFormat format, int fractionDigits) {
        format.setMinimumFractionDigits(fractionDigits);
        format.setMaximumFractionDigits(fractionDigits);
    }

}
