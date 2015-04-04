package org.jvirtanen.parity.util;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This class contains utility methods for working with timestamps.
 */
public class Timestamps {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("HH:mm:ss.SSS");

    private Timestamps() {
    }

    /**
     * Format a timestamp as a string.
     *
     * @param timestamp a timestamp in nanoseconds
     * @return the timestamp as a string
     */
    public static String format(long timestamp) {
        return FORMATTER.print(LocalTime.fromMillisOfDay(timestamp / (1000 * 1000)));
    }

}
