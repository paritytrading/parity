package com.paritytrading.parity.reporter;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.net.pmr.PMR;
import com.paritytrading.parity.util.Timestamps;

class TSVFormat extends MarketReportListener {

    private static final String HEADER = "" +
        "Timestamp\t" +
        "Match Number\t" +
        "Instrument\t" +
        "Quantity\t" +
        "Price\t" +
        "Buyer\t" +
        "Buy Order Number\t" +
        "Seller\t" +
        "Sell Order Number\n";

    public TSVFormat() {
        printf(HEADER);
    }

    @Override
    public void trade(PMR.Trade message) {
        printf("%s\t%d\t%s\t%d\t%.2f\t%s\t%d\t%s\t%d\n",
                Timestamps.format(message.timestamp / NANOS_PER_MILLI),
                message.matchNumber,
                ASCII.unpackLong(message.instrument).trim(),
                message.quantity,
                message.price / PRICE_FACTOR,
                ASCII.unpackLong(message.buyer).trim(),
                message.buyOrderNumber,
                ASCII.unpackLong(message.seller).trim(),
                message.sellOrderNumber);
    }

}
