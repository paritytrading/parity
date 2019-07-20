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
        return FORMATTER.format(LocalTime.ofNanoOfDay(timestampMillis % (24 * 60 * 60 * 1000) * 1_000_000));
    }

}
