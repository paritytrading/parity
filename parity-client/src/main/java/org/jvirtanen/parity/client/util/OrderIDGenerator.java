package org.jvirtanen.parity.client.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OrderIDGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private String prefix;

    private int count;

    public OrderIDGenerator() {
        this(LocalTime.now());
    }

    public OrderIDGenerator(LocalTime time) {
        prefix = FORMATTER.format(time);
        count  = 1;
    }

    public String next() {
        return String.format("%s-%07d", prefix, count++);
    }

}
