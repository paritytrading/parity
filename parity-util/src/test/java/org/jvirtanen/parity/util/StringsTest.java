package org.jvirtanen.parity.util;

import static org.jvirtanen.parity.util.Strings.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class StringsTest {

    @Test
    public void decodingLong() {
        assertEquals("FOOBAR  ", decodeLong(0x464F4F4241522020L));
    }

    @Test(expected=IllegalArgumentException.class)
    public void decodingLongWithInvalidCharacter() {
        decodeLong(0x464FD34241522020L);
    }

    @Test
    public void encodingLong() {
        assertEquals(0x464F4F4241524241L, encodeLong("FOOBARBA"));
    }

    @Test
    public void encodingTooLongLong() {
        assertEquals(0x464F4F4241524241L, encodeLong("FOOBARBAZ"));
    }

    @Test
    public void encodingTooShortLong() {
        assertEquals(0x464F4F4241522020L, encodeLong("FOOBAR"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void encodingLongWithInvalidCharacter() {
        encodeLong("FÓOBARBA");
    }

}
