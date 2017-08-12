package com.paritytrading.parity.util;

import static org.junit.Assert.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

public class InstrumentsTest {

    private static final Instruments FRACTIONS = fromString("" +
            "instruments = {\n" +
            "  price-integer-digits = 4\n" +
            "  size-integer-digits  = 8\n" +
            "  FOO {\n" +
            "    price-fraction-digits = 2\n" +
            "    size-fraction-digits  = 0\n" +
            "  }\n" +
            "  BAR {\n" +
            "    price-fraction-digits = 6\n" +
            "    size-fraction-digits  = 8\n" +
            "  }\n" +
            "}");

    private static final Instruments INTEGERS = fromString("" +
            "instruments = {\n" +
            "  price-integer-digits = 4\n" +
            "  size-integer-digits  = 8\n" +
            "  FOO {\n" +
            "    price-fraction-digits = 2\n" +
            "    size-fraction-digits  = 0\n" +
            "  }\n" +
            "}");

    @Test
    public void maxPriceFractionDigits() {
        assertEquals(6, FRACTIONS.getMaxPriceFractionDigits());
    }

    @Test
    public void maxSizeFractionDigits() {
        assertEquals(8, FRACTIONS.getMaxSizeFractionDigits());
    }

    @Test
    public void pricePlaceholder() {
        assertEquals("    -      ", FRACTIONS.getPricePlaceholder());
    }

    @Test
    public void sizePlaceholder() {
        assertEquals("        -        ", FRACTIONS.getSizePlaceholder());
    }

    @Test
    public void priceWidth() {
        assertEquals(11, FRACTIONS.getPriceWidth());
    }

    @Test
    public void sizeWidth() {
        assertEquals(17, FRACTIONS.getSizeWidth());
    }

    @Test
    public void priceFractionDigits() {
        assertEquals(2, FRACTIONS.get("FOO").getPriceFractionDigits());
    }

    @Test
    public void sizeFractionDigitsWithIntegers() {
        assertEquals(0, FRACTIONS.get("FOO").getSizeFractionDigits());
    }

    @Test
    public void sizeFractionDigitsWithFractions() {
        assertEquals(8, FRACTIONS.get("BAR").getSizeFractionDigits());
    }

    @Test
    public void priceFormat() {
        assertEquals("%7.2f    ", FRACTIONS.get("FOO").getPriceFormat());
    }

    @Test
    public void sizeFormatWithIntegers() {
        assertEquals("%8.0f         ", FRACTIONS.get("FOO").getSizeFormat());
    }

    @Test
    public void sizeFormatWithFractions() {
        assertEquals("%17.8f", FRACTIONS.get("BAR").getSizeFormat());
    }

    @Test
    public void priceFactor() {
        assertEquals(100.0, FRACTIONS.get("FOO").getPriceFactor(), 0.0);
    }

    @Test
    public void sizeFactorWithIntegers() {
        assertEquals(1.0, FRACTIONS.get("FOO").getSizeFactor(), 0.0);
    }

    @Test
    public void sizeFactorWithFractions() {
        assertEquals(100000000.0, FRACTIONS.get("BAR").getSizeFactor(), 0.0);
    }

    @Test
    public void sizeWidthWithIntegersOnly() {
        assertEquals(8, INTEGERS.getSizeWidth());
    }

    @Test
    public void sizePlaceholderWithIntegersOnly() {
        assertEquals("       -", INTEGERS.getSizePlaceholder());
    }

    @Test
    public void sizeFormatWithIntegersOnly() {
        assertEquals("%8.0f", INTEGERS.get("FOO").getSizeFormat());
    }

    private static Instruments fromString(String s) {
        Config config = ConfigFactory.parseString(s);

        return Instruments.fromConfig(config, "instruments");
    }

}
