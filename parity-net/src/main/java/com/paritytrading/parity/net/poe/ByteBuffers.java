package com.paritytrading.parity.net.poe;

import static java.nio.charset.StandardCharsets.*;

import java.nio.ByteBuffer;

class ByteBuffers {

    private static final byte SPACE = ' ';

    static String getString(ByteBuffer buffer, int length) {
        byte[] bytes = new  byte[length];

        buffer.get(bytes);

        return new String(bytes, US_ASCII);
    }

    static void putString(ByteBuffer buffer, String value, int length) {
        int i = 0;

        for (; i < Math.min(value.length(), length); i++)
            buffer.put((byte)value.charAt(i));

        for (; i < length; i++)
            buffer.put(SPACE);
    }

}
