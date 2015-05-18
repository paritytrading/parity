package org.jvirtanen.parity.reporter;

import static org.jvirtanen.lang.Strings.*;

import org.jvirtanen.parity.net.pmr.PMR;
import org.jvirtanen.parity.util.Timestamps;

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
                decodeLong(message.instrument).trim(),
                message.quantity,
                message.price / PRICE_FACTOR,
                decodeLong(message.buyer).trim(),
                message.buyOrderNumber,
                decodeLong(message.seller).trim(),
                message.sellOrderNumber);
    }

}
