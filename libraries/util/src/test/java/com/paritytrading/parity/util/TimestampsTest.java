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

import static org.junit.Assert.*;

import org.junit.Test;

public class TimestampsTest {

    @Test
    public void testFirstMidnight() {
        long timestampMillis = 0L;

        assertEquals("00:00:00.000", Timestamps.format(timestampMillis));
    }

    @Test
    public void testFirstMidday() {
        long timestampMillis = 12L * 60 * 60 * 1000;

        assertEquals("12:00:00.000", Timestamps.format(timestampMillis));
    }

    @Test
    public void testSecondMidnight() {
        long timestampMillis = 24L * 60 * 60 * 1000;

        assertEquals("00:00:00.000", Timestamps.format(timestampMillis));
    }

}
