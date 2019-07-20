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
package com.paritytrading.parity.net.poe;

import static com.paritytrading.parity.net.poe.POE.*;

import com.paritytrading.nassau.MessageListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A parser for inbound messages on the client side.
 */
public class POEClientParser implements MessageListener {

    private final OrderAccepted orderAccepted;
    private final OrderRejected orderRejected;
    private final OrderExecuted orderExecuted;
    private final OrderCanceled orderCanceled;

    private final POEClientListener listener;

    /**
     * Create a parser for inbound messages on the client side.
     *
     * @param listener the message listener
     */
    public POEClientParser(POEClientListener listener) {
        this.orderAccepted = new OrderAccepted();
        this.orderRejected = new OrderRejected();
        this.orderExecuted = new OrderExecuted();
        this.orderCanceled = new OrderCanceled();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        int length = buffer.remaining();

        if (length < 1)
            throw new POEException("Malformed message: no message type");

        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_ORDER_ACCEPTED:
            if (length < MESSAGE_LENGTH_ORDER_ACCEPTED)
                malformedMessage(messageType);

            orderAccepted.get(buffer);
            listener.orderAccepted(orderAccepted);
            break;
        case MESSAGE_TYPE_ORDER_REJECTED:
            if (length < MESSAGE_LENGTH_ORDER_REJECTED)
                malformedMessage(messageType);

            orderRejected.get(buffer);
            listener.orderRejected(orderRejected);
            break;
        case MESSAGE_TYPE_ORDER_EXECUTED:
            if (length < MESSAGE_LENGTH_ORDER_EXECUTED)
                malformedMessage(messageType);

            orderExecuted.get(buffer);
            listener.orderExecuted(orderExecuted);
            break;
        case MESSAGE_TYPE_ORDER_CANCELED:
            if (length < MESSAGE_LENGTH_ORDER_CANCELED)
                malformedMessage(messageType);

            orderCanceled.get(buffer);
            listener.orderCanceled(orderCanceled);
            break;
        default:
            throw new POEException("Unknown message type: " + (char)messageType);
        }
    }

    private void malformedMessage(byte messageType) throws IOException {
        throw new POEException("Malformed message: " + (char)messageType);
    }

}
