package com.paritytrading.parity.util;

import java.util.Arrays;

class Strings {

    private Strings() {
    }

    public static String repeat(char c, int count) {
        char[] a = new char[count];

        Arrays.fill(a, c);

        return new String(a);
    }

}
