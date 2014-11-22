package org.jvirtanen.parity.ticker;

import org.jvirtanen.parity.top.MarketListener;

interface MarketDataListener extends MarketListener {

    void seconds(long second);

    void timestamp(long timestamp);

}
