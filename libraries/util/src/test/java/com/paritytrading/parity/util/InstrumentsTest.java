package com.paritytrading.parity.util;

import static org.junit.Assert.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

public class InstrumentsTest {

    private static final Instruments INSTRUMENTS = fromString("" +
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

    @Test
    public void maxPriceFractionDigits() {
        assertEquals(6, INSTRUMENTS.getMaxPriceFractionDigits());
    }

    @Test
    public void maxSizeFractionDigits() {
        assertEquals(8, INSTRUMENTS.getMaxSizeFractionDigits());
    }

    @Test
    public void pricePlaceholder() {
        assertEquals("    -      ", INSTRUMENTS.getPricePlaceholder());
    }

    @Test
    public void sizePlaceholder() {
        assertEquals("        -        ", INSTRUMENTS.getSizePlaceholder());
    }

    @Test
    public void priceWidth() {
        assertEquals(11, INSTRUMENTS.getPriceWidth());
    }

    @Test
    public void sizeWidth() {
        assertEquals(17, INSTRUMENTS.getSizeWidth());
    }

    @Test
    public void priceFractionDigits() {
        assertEquals(2, INSTRUMENTS.get("FOO").getPriceFractionDigits());
    }

    @Test
    public void sizeFractionDigits() {
        assertEquals(0, INSTRUMENTS.get("FOO").getSizeFractionDigits());
    }

    @Test
    public void priceFormat() {
        assertEquals("%7.2f    ", INSTRUMENTS.get("FOO").getPriceFormat());
    }

    @Test
    public void sizeFormat() {
        assertEquals("%8.0f         ", INSTRUMENTS.get("FOO").getSizeFormat());
    }

    @Test
    public void priceFactor() {
        assertEquals(100.0, INSTRUMENTS.get("FOO").getPriceFactor(), 0.0);
    }

    @Test
    public void sizeFactor() {
        assertEquals(1.0, INSTRUMENTS.get("FOO").getSizeFactor(), 0.0);
    }

    private static Instruments fromString(String s) {
        Config config = ConfigFactory.parseString(s);

        return Instruments.fromConfig(config, "instruments");
    }

}
