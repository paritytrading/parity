package com.paritytrading.parity.util;

import static com.paritytrading.foundation.Longs.*;
import static com.paritytrading.parity.util.Strings.*;

import com.paritytrading.foundation.ASCII;
import com.typesafe.config.Config;

/**
 * An instrument configuration.
 */
public class Instrument {

    private String asString;
    private long   asLong;

    private int priceFractionDigits;
    private int sizeFractionDigits;

    private long priceFactor;
    private long sizeFactor;

    private String priceFormat;
    private String sizeFormat;

    private Instrument(String asString, int priceFractionDigits, int sizeFractionDigits) {
        this.asString = asString;
        this.asLong   = ASCII.packLong(asString);

        this.priceFractionDigits = priceFractionDigits;
        this.sizeFractionDigits  = sizeFractionDigits;

        this.priceFactor = POWERS_OF_TEN[priceFractionDigits];
        this.sizeFactor  = POWERS_OF_TEN[sizeFractionDigits];

        setPriceFormat(1, priceFractionDigits);
        setSizeFormat(1, sizeFractionDigits);
    }

    /**
     * Get this instrument as a string.
     *
     * @return this instrument as a string
     */
    public String asString() {
        return asString;
    }

    /**
     * Get this instrument as a long integer.
     *
     * @return this instrument as a long integer
     */
    public long asLong() {
        return asLong;
    }

    /**
     * Get the number of digits in the fractional part of a price.
     *
     * @return the number of digits in the fractional part of a price
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
     * Get the multiplication factor for a price.
     *
     * @return the multiplication factor for a price
     */
    public double getPriceFactor() {
        return priceFactor;
    }

    /**
     * Get the multiplication factor for a size.
     *
     * @return the multiplication factor for a size
     */
    public double getSizeFactor() {
        return sizeFactor;
    }

    /**
     * Get a format string for a price.
     *
     * @return a format string for a price
     */
    public String getPriceFormat() {
        return priceFormat;
    }

    /**
     * Get a format string for a size.
     *
     * @return a format string for a size
     */
    public String getSizeFormat() {
        return sizeFormat;
    }

    void setPriceFormat(int integerDigits, int maxFractionDigits) {
        priceFormat = getFormat(integerDigits, priceFractionDigits, maxFractionDigits);
    }

    void setSizeFormat(int integerDigits, int maxFractionDigits) {
        sizeFormat = getFormat(integerDigits, sizeFractionDigits, maxFractionDigits);
    }

    static Instrument fromConfig(Config config, String path) {
        int priceFractionDigits = config.getInt(path + ".price-fraction-digits");
        int sizeFractionDigits  = config.getInt(path + ".size-fraction-digits");

        return new Instrument(path, priceFractionDigits, sizeFractionDigits);
    }

    private static String getFormat(int integerDigits, int fractionDigits, int maxFractionDigits) {
        int width = integerDigits + (fractionDigits > 0 ? 1 : 0) + fractionDigits;

        int padding = maxFractionDigits - fractionDigits;

        if (maxFractionDigits > 0 && fractionDigits == 0)
            padding += 1;

        return "%" + width + "." + fractionDigits + "f" + repeat(' ', padding);
    }

}
