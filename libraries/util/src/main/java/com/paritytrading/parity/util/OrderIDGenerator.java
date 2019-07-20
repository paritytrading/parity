/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritytrading.parity.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * This class implements a simple order identifier generator.
 */
public class OrderIDGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String prefix;

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
