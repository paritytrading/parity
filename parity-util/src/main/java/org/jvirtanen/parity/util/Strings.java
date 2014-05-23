package org.jvirtanen.parity.util;

/**
 * This class contains methods for manipulating strings.
 */
public class Strings {

    private Strings() {
    }

    /**
     * Decode an eight-character ASCII string from a long.
     *
     * @param l a long
     * @return a string
     * @throws IllegalArgumentException if the long does not contain an
     *   ASCII string
     */
    public static String decodeLong(long l) {
        StringBuilder b = new StringBuilder(8);

        for (int i = 7; i >= 0; i--) {
            char c = (char)((l >> (8 * i)) & 0xFF);
            if (c > 127)
                throw invalidCharacter(c);

            b.append(c);
        }

        return b.toString();
    }

    /**
     * Encode an eight-character ASCII string to a long.
     *
     * <p>If the length of the string is less than eight characters, it will
     * be left-justified and padded on the right with the space character.</p>
     *
     * <p>If the length of the string is more than eight characters, only the
     * first eight characters are encoded.</p>
     *
     * @param s a string
     * @return a long
     * @throws IllegalArgumentException if the string contains other than
     *   ASCII characters
     */
    public static long encodeLong(String s) {
        long l = 0;
        int  i = 0;

        for (; i < Math.min(s.length(), 8); i++) {
            char c = s.charAt(i);
            if (c > 127)
                throw invalidCharacter(c);

            l = (l << 8) | c;
        }

        for (; i < 8; i++)
            l = (l << 8) | ' ';

        return l;
    }

    private static IllegalArgumentException invalidCharacter(int c) {
        return new IllegalArgumentException(String.format("Invalid character: \\u%04x", c));
    }

}
