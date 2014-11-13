package org.jvirtanen.parity.client.util;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class OrderIDGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("HH:mm:ss");

    private String prefix;

    private int count;

    public OrderIDGenerator() {
        this(LocalTime.now());
    }

    public OrderIDGenerator(LocalTime time) {
        prefix = FORMATTER.print(time);
        count  = 1;
    }

    public String next() {
        return String.format("%s-%07d", prefix, count++);
    }

}
