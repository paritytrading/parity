package org.jvirtanen.parity.reporter;

import com.paritytrading.foundation.ASCII;
import org.jvirtanen.parity.net.pmr.PMR;
import org.jvirtanen.parity.util.Timestamps;

class DisplayFormat extends MarketReportListener {

    private static final String HEADER = "" +
        "Timestamp    Inst     Quantity   Price     Buyer    Seller\n" +
        "------------ -------- ---------- --------- -------- --------";

    public DisplayFormat() {
        printf("\n%s\n", HEADER);
    }

    @Override
    public void trade(PMR.Trade message) {
        printf("%12s %8s %10d %9.2f %8s %8s\n", Timestamps.format(message.timestamp / NANOS_PER_MILLI),
                ASCII.unpackLong(message.instrument), message.quantity, message.price / PRICE_FACTOR,
                ASCII.unpackLong(message.buyer), ASCII.unpackLong(message.seller));
    }

}
