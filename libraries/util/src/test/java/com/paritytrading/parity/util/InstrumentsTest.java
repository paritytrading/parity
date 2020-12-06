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
package com.paritytrading.parity.util;

import static org.junit.jupiter.api.Assertions.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

class InstrumentsTest {

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
    void maxPriceFractionDigits() {
        assertEquals(6, FRACTIONS.getMaxPriceFractionDigits());
    }

    @Test
    void maxSizeFractionDigits() {
        assertEquals(8, FRACTIONS.getMaxSizeFractionDigits());
    }

    @Test
    void pricePlaceholder() {
        assertEquals("    -      ", FRACTIONS.getPricePlaceholder());
    }

    @Test
    void sizePlaceholder() {
        assertEquals("        -        ", FRACTIONS.getSizePlaceholder());
    }

    @Test
    void priceWidth() {
        assertEquals(11, FRACTIONS.getPriceWidth());
    }

    @Test
    void sizeWidth() {
        assertEquals(17, FRACTIONS.getSizeWidth());
    }

    @Test
    void priceFractionDigits() {
        assertEquals(2, FRACTIONS.get("FOO").getPriceFractionDigits());
    }

    @Test
    void sizeFractionDigitsWithIntegers() {
        assertEquals(0, FRACTIONS.get("FOO").getSizeFractionDigits());
    }

    @Test
    void sizeFractionDigitsWithFractions() {
        assertEquals(8, FRACTIONS.get("BAR").getSizeFractionDigits());
    }

    @Test
    void priceFormat() {
        assertEquals("%7.2f    ", FRACTIONS.get("FOO").getPriceFormat());
    }

    @Test
    void sizeFormatWithIntegers() {
        assertEquals("%8.0f         ", FRACTIONS.get("FOO").getSizeFormat());
    }

    @Test
    void sizeFormatWithFractions() {
        assertEquals("%17.8f", FRACTIONS.get("BAR").getSizeFormat());
    }

    @Test
    void priceFactor() {
        assertEquals(100.0, FRACTIONS.get("FOO").getPriceFactor(), 0.0);
    }

    @Test
    void sizeFactorWithIntegers() {
        assertEquals(1.0, FRACTIONS.get("FOO").getSizeFactor(), 0.0);
    }

    @Test
    void sizeFactorWithFractions() {
        assertEquals(100000000.0, FRACTIONS.get("BAR").getSizeFactor(), 0.0);
    }

    @Test
    void sizeWidthWithIntegersOnly() {
        assertEquals(8, INTEGERS.getSizeWidth());
    }

    @Test
    void sizePlaceholderWithIntegersOnly() {
        assertEquals("       -", INTEGERS.getSizePlaceholder());
    }

    @Test
    void sizeFormatWithIntegersOnly() {
        assertEquals("%8.0f", INTEGERS.get("FOO").getSizeFormat());
    }

    private static Instruments fromString(String s) {
        Config config = ConfigFactory.parseString(s);

        return Instruments.fromConfig(config, "instruments");
    }

}
