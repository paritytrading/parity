package org.jvirtanen.parity.client.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimestampsTest {

    @Test
    public void testFirstMidnight() {
        long timestamp = 0L;

        assertEquals("00:00:00.000", Timestamps.format(timestamp));
    }

    @Test
    public void testFirstMidday() {
        long timestamp = 12L * 60 * 60 * 1000 * 1000 * 1000;

        assertEquals("12:00:00.000", Timestamps.format(timestamp));
    }

    @Test
    public void testSecondMidnight() {
        long timestamp = 24L * 60 * 60 * 1000 * 1000 * 1000;

        assertEquals("00:00:00.000", Timestamps.format(timestamp));
    }

}
