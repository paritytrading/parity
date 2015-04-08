package org.jvirtanen.parity.reporter;

import static org.jvirtanen.lang.Strings.*;

import java.util.Locale;
import org.jvirtanen.parity.net.ptr.PTR;
import org.jvirtanen.parity.net.ptr.PTRListener;
import org.jvirtanen.parity.util.Timestamps;

class Display implements PTRListener {

    private static final double PRICE_FACTOR = 10000.0;

    private static final long NANOS_PER_MILLI = 1000 * 1000;

    private static final String HEADER = "" +
        "Timestamp    Inst     Quantity   Price     Buyer    Seller\n" +
        "------------ -------- ---------- --------- -------- --------";

    public Display() {
        printf("\n%s\n", HEADER);
    }

    @Override
    public void trade(PTR.Trade message) {
        printf("%12s %8s %10d %9.2f %8s %8s\n", Timestamps.format(message.timestamp / NANOS_PER_MILLI),
                decodeLong(message.instrument), message.quantity, message.price / PRICE_FACTOR,
                decodeLong(message.buyer), decodeLong(message.seller));
    }

    private void printf(String format, Object... args) {
        System.out.printf(Locale.US, format, args);
    }

}
