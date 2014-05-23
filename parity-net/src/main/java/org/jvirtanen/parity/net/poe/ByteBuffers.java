package org.jvirtanen.parity.net.poe;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

class ByteBuffers {

    private static final byte SPACE = ' ';

    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    static String getString(ByteBuffer buffer, int length) {
        byte[] bytes = new  byte[length];

        buffer.get(bytes);

        return new String(bytes, US_ASCII);
    }

    static void putString(ByteBuffer buffer, String value, int length) {
        byte[] bytes = value.getBytes(US_ASCII);

        int i = 0;

        for (; i < Math.min(bytes.length, length); i++)
            buffer.put(bytes[i]);

        for (; i < length; i++)
            buffer.put(SPACE);
    }

}
