package org.jvirtanen.parity.reporter;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

class Timestamps {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("HH:mm:ss.SSS");

    private Timestamps() {
    }

    public static String format(long timestamp) {
        return FORMATTER.print(LocalTime.fromMillisOfDay(timestamp / (1000 * 1000)));
    }

}
