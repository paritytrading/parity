package com.paritytrading.parity.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * This class contains utility methods for working with timestamps.
 */
public class Timestamps {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private Timestamps() {
    }

    /**
     * Format a timestamp as a string.
     *
     * @param timestampMillis a timestamp in milliseconds
     * @return the timestamp as a string
     */
    public static String format(long timestampMillis) {
        return FORMATTER.format(LocalTime.ofNanoOfDay(timestampMillis % (24 * 60 * 60 * 1000) * 1000 * 1000));
    }

}
