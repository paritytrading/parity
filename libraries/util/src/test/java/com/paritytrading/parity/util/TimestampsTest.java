package com.paritytrading.parity.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimestampsTest {

    @Test
    public void testFirstMidnight() {
        long timestampMillis = 0L;

        assertEquals("00:00:00.000", Timestamps.format(timestampMillis));
    }

    @Test
    public void testFirstMidday() {
        long timestampMillis = 12L * 60 * 60 * 1000;

        assertEquals("12:00:00.000", Timestamps.format(timestampMillis));
    }

    @Test
    public void testSecondMidnight() {
        long timestampMillis = 24L * 60 * 60 * 1000;

        assertEquals("00:00:00.000", Timestamps.format(timestampMillis));
    }

}
