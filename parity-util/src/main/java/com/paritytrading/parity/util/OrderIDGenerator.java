package com.paritytrading.parity.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * This class implements a simple order identifier generator.
 */
public class OrderIDGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private String prefix;

    private int count;

    /**
     * Create an instance.
     */
    public OrderIDGenerator() {
        this(LocalTime.now());
    }

    /**
     * Create an instance.
     *
     * @param time a timestamp
     */
    public OrderIDGenerator(LocalTime time) {
        prefix = FORMATTER.format(time);
        count  = 1;
    }

    /**
     * Generate an order identifier.
     *
     * @return an order identifier
     */
    public String next() {
        return String.format("%s-%07d", prefix, count++);
    }

}
