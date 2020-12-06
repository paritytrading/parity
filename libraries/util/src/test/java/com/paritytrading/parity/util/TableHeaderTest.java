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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TableHeaderTest {

    @Test
    void foo() {
        TableHeader actual = new TableHeader();

        actual.add("Foo",  5);
        actual.add("Bar",  5);
        actual.add("Quux", 3);

        String expected = ""
            + "Foo   Bar   Quu\n"
            + "----- ----- ---\n";

        assertEquals(expected, actual.format());
    }

}
