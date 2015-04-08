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
     * @param timestampMillis a timestamp in milliseconds
     * @return the timestamp as a string
     */
    public static String format(long timestampMillis) {
        return FORMATTER.print(LocalTime.fromMillisOfDay(timestampMillis));
    }

}
