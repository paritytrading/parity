package com.paritytrading.parity.util;

import static com.paritytrading.parity.util.Strings.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigObject;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

/**
 * The instruments configuration.
 */
public class Instruments implements Iterable<Instrument> {

    private HashMap<String, Instrument> valuesByString;

    private Long2ObjectOpenHashMap<Instrument> valuesByLong;

    private int maxPriceFractionDigits;
    private int maxSizeFractionDigits;

    private int priceWidth;
    private int sizeWidth;

    private String pricePlaceholder;
    private String sizePlaceholder;

    private Instruments(List<Instrument> values, int priceIntegerDigits, int maxPriceFractionDigits,
            int sizeIntegerDigits, int maxSizeFractionDigits) {
        this.valuesByString = new HashMap<>();
        this.valuesByLong   = new Long2ObjectOpenHashMap<>();

        for (Instrument value : values) {
            valuesByString.put(value.asString(), value);
            valuesByLong.put(value.asLong(), value);
        }

        this.maxPriceFractionDigits = maxPriceFractionDigits;
        this.maxSizeFractionDigits  = maxSizeFractionDigits;

        this.priceWidth = priceIntegerDigits + 1 + maxPriceFractionDigits;
        this.sizeWidth  = sizeIntegerDigits  + 1 + maxSizeFractionDigits;

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
        return valuesByString.get(instrument);
    }

    /**
     * Get an instrument.
     *
     * @param instrument an instrument
     * @return the instrument
     */
    public Instrument get(long instrument) {
        return valuesByLong.get(instrument);
    }

    /**
     * Return an iterator over the instruments.
     *
     * @return an iterator over the instruments
     */
    public Iterator<Instrument> iterator() {
        return valuesByString.values().iterator();
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

        List<Instrument> values = new ArrayList<>();

        for (String instrument : instruments)
            values.add(Instrument.fromConfig(rootConfig, instrument));

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

    private static <T> int max(Collection<T> collection, ToIntFunction<? super T> mapper) {
        return collection.stream().mapToInt(mapper).max().orElse(0);
    }

    private static String placeholder(int integerDigits, int maxFractionDigits) {
        if (maxFractionDigits == 0)
            return repeat(' ', integerDigits - 1) + '-';

        return repeat(' ', integerDigits) + '-' + repeat(' ', maxFractionDigits);
    }

}
