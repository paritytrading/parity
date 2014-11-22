package org.jvirtanen.parity.ticker;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

class Timestamps {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("HH:mm:ss.SSS");

    private Timestamps() {
    }

    public static String format(long second, long timestamp) {
        return FORMATTER.print(LocalTime.fromMillisOfDay(second * 1000 + timestamp / (1000 * 1000)));
    }

}
