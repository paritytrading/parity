package org.jvirtanen.parity.net.poe;

import static org.junit.Assert.*;
import static org.jvirtanen.parity.net.poe.ByteBuffers.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.junit.Test;

public class ByteBuffersTest {

    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    @Test
    public void gettingString() {
        ByteBuffer buffer = wrap("foo");

        assertEquals("foo", getString(buffer, 3));
    }

    @Test
    public void puttingString() {
        ByteBuffer buffer = ByteBuffer.allocate(3);

        putString(buffer, "foo", 3);
        buffer.flip();

        assertEquals("foo", remaining(buffer));
    }

    @Test
    public void puttingTooLongString() {
        ByteBuffer buffer = ByteBuffer.allocate(2);

        putString(buffer, "foo", 2);
        buffer.flip();

        assertEquals("fo", remaining(buffer));
    }

    @Test
    public void puttingTooShortString() {
        ByteBuffer buffer = ByteBuffer.allocate(4);

        putString(buffer, "foo", 4);
        buffer.flip();

        assertEquals("foo ", remaining(buffer));
    }

    private static String remaining(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];

        buffer.get(bytes);

        return new String(bytes, US_ASCII);
    }

    private static ByteBuffer wrap(String string) {
        return ByteBuffer.wrap(string.getBytes(US_ASCII));
    }

}
