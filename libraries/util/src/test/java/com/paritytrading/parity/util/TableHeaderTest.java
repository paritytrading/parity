package com.paritytrading.parity.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TableHeaderTest {

    @Test
    public void foo() {
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
