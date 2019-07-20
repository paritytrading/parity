/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritytrading.parity.client;

import com.paritytrading.parity.net.poe.POE;

class Error {

    static final String HEADER = "" +
        "Order ID         Reason\n" +
        "---------------- ------------------";

    private final String orderId;
    private final byte   reason;

    Error(Event.OrderRejected event) {
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

    String format() {
        return String.format("%16s %-18s", orderId, describe(reason));
    }

}
