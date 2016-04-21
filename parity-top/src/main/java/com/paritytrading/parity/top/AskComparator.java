package com.paritytrading.parity.top;

import it.unimi.dsi.fastutil.longs.AbstractLongComparator;

class AskComparator extends AbstractLongComparator {

    public static final AskComparator INSTANCE = new AskComparator();

    @Override
    public int compare(long left, long right) {
        return Long.signum(left - right);
    }

}
