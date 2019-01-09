package com.paritytrading.parity.util;

import static com.paritytrading.parity.util.Strings.*;
import static java.util.Arrays.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigObject;
import java.util.Iterator;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

/**
 * The instruments configuration.
 */
public class Instruments implements Iterable<Instrument> {

    private final Instrument[] values;

    private final int maxPriceFractionDigits;
    private final int maxSizeFractionDigits;

    private final int priceWidth;
    private final int sizeWidth;

    private final String pricePlaceholder;
    private final String sizePlaceholder;

    private Instruments(Instrument[] values, int priceIntegerDigits, int maxPriceFractionDigits,
            int sizeIntegerDigits, int maxSizeFractionDigits) {
        this.values = values;

        this.maxPriceFractionDigits = maxPriceFractionDigits;
        this.maxSizeFractionDigits  = maxSizeFractionDigits;

        this.priceWidth = priceIntegerDigits + (maxPriceFractionDigits > 0 ? 1 : 0) + maxPriceFractionDigits;
        this.sizeWidth  = sizeIntegerDigits  + (maxSizeFractionDigits  > 0 ? 1 : 0) + maxSizeFractionDigits;

        this.pricePlaceholder = placeholder(priceIntegerDigits, maxPriceFractionDigits);
        this.sizePlaceholder  = placeholder(sizeIntegerDigits,  maxSizeFractionDigits);
    }

    /**
     * Get an instrument.
     *
     * @param instrument an instrument
     * @return the instrument
     */
    public Instrument get(String instrument) {
        for (Instrument value : values) {
            if (instrument.equals(value.asString()))
                return value;
        }

        return null;
    }

    /**
     * Get an instrument.
     *
     * @param instrument an instrument
     * @return the instrument
     */
    public Instrument get(long instrument) {
        for (Instrument value : values) {
            if (instrument == value.asLong())
                return value;
        }

        return null;
    }

    /**
     * Return an iterator over the instruments.
     *
     * @return an iterator over the instruments
     */
    public Iterator<Instrument> iterator() {
        return asList(values).iterator();
    }

    /**
     * Get the maximum number of digits in the fractional part of a price.
     *
     * @return the maximum number of digits in the fractional part of a price
     */
    public int getMaxPriceFractionDigits() {
        return maxPriceFractionDigits;
    }

    /**
     * Get the maximum number of digits in the fractional part of a size.
     *
     * @return the maximum number of digits in the fractional part of a size
     */
    public int getMaxSizeFractionDigits() {
        return maxSizeFractionDigits;
    }

    /**
     * Get the width of a formatted price.
     *
     * @return the width of a formatted price
     */
    public int getPriceWidth() {
        return priceWidth;
    }

    /**
     * Get the width of a formatted size.
     *
     * @return the width of a formatted size
     */
    public int getSizeWidth() {
        return sizeWidth;
    }

    /**
     * Get a placeholder for a price.
     *
     * @return a placeholder for a price
     */
    public String getPricePlaceholder() {
        return pricePlaceholder;
    }

    /**
     * Get a placeholder for a size.
     *
     * @return a placeholder for a size
     */
    public String getSizePlaceholder() {
        return sizePlaceholder;
    }

    /**
     * Get the instruments from a configuration object.
     *
     * @param config a configuration object
     * @param path the path expression
     * @return the instruments
     * @throws ConfigException if a configuration error occurs
     */
    public static Instruments fromConfig(Config config, String path) {
        ConfigObject root = config.getObject(path);

        Config rootConfig = root.toConfig();

        int priceIntegerDigits = getInt(rootConfig, "price-integer-digits", 1);
        int sizeIntegerDigits  = getInt(rootConfig, "size-integer-digits",  1);

        Set<String> instruments = root.keySet();

        instruments.remove("price-integer-digits");
        instruments.remove("size-integer-digits");

        Instrument[] values = instruments.stream()
                .map(instrument -> Instrument.fromConfig(rootConfig, instrument))
                .toArray(Instrument[]::new);

        int maxPriceFractionDigits = max(values, Instrument::getPriceFractionDigits);
        int maxSizeFractionDigits  = max(values, Instrument::getSizeFractionDigits);

        for (Instrument value : values) {
            value.setPriceFormat(priceIntegerDigits, maxPriceFractionDigits);
            value.setSizeFormat(sizeIntegerDigits, maxSizeFractionDigits);
        }

        return new Instruments(values, priceIntegerDigits, maxPriceFractionDigits,
                sizeIntegerDigits, maxSizeFractionDigits);
    }

    private static int getInt(Config config, String path, int defaultValue) {
        return config.hasPath(path) ? config.getInt(path) : defaultValue;
    }

    private static <T> int max(T[] collection, ToIntFunction<? super T> mapper) {
        return Stream.of(collection).mapToInt(mapper).max().orElse(0);
    }

    private static String placeholder(int integerDigits, int maxFractionDigits) {
        if (maxFractionDigits == 0)
            return repeat(' ', integerDigits - 1) + '-';

        return repeat(' ', integerDigits) + '-' + repeat(' ', maxFractionDigits);
    }

}
