package com.paritytrading.parity.match;

import it.unimi.dsi.fastutil.longs.AbstractLongComparator;

class BidComparator extends AbstractLongComparator {

    public static final BidComparator INSTANCE = new BidComparator();

    @Override
    public int compare(long left, long right) {
        return Long.signum(right - left);
    }

}
