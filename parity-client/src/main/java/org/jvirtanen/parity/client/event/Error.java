package org.jvirtanen.parity.client.event;

import org.jvirtanen.parity.net.poe.POE;

public class Error {

    public static final String HEADER = "" +
        "Order ID         Reason\n" +
        "---------------- ------------------";

    private String orderId;
    private byte   reason;

    public Error(Event.OrderRejected event) {
        orderId = event.orderId;
        reason  = event.reason;
    }

    private String describe(byte reason) {
        switch (reason) {
        case POE.ORDER_REJECT_REASON_UNKNOWN_INSTRUMENT:
            return "Unknown instrument";
        default:
            return "<unknown>";
        }
    }

    public String format() {
        return String.format("%16s %-18s", orderId, describe(reason));
    }

}
