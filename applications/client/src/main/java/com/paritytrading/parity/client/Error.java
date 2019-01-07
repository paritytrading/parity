package com.paritytrading.parity.client;

import com.paritytrading.parity.net.poe.POE;

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
        case POE.ORDER_REJECT_REASON_INVALID_PRICE:
            return "Invalid price";
        case POE.ORDER_REJECT_REASON_INVALID_QUANTITY:
            return "Invalid quantity";
        default:
            return "<unknown>";
        }
    }

    public String format() {
        return String.format("%16s %-18s", orderId, describe(reason));
    }

}
