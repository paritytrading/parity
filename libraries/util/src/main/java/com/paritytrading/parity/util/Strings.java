package com.paritytrading.parity.util;

import java.util.Arrays;

class Strings {

    static String repeat(char c, int count) {
        char[] a = new char[count];

        Arrays.fill(a, c);

        return new String(a);
    }

}
