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
package com.paritytrading.parity.net.pmd;

import static com.paritytrading.parity.net.pmd.PMD.*;

import com.paritytrading.nassau.MessageListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A parser for inbound messages.
 */
public class PMDParser implements MessageListener {

    private final Version       version;
    private final OrderAdded    orderAdded;
    private final OrderExecuted orderExecuted;
    private final OrderCanceled orderCanceled;

    private final PMDListener listener;

    /**
     * Create a parser for inbound messages.
     *
     * @param listener the message listener
     */
    public PMDParser(PMDListener listener) {
        this.version       = new Version();
        this.orderAdded    = new OrderAdded();
        this.orderExecuted = new OrderExecuted();
        this.orderCanceled = new OrderCanceled();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        int length = buffer.remaining();

        if (length < 1)
            throw new PMDException("Malformed message: no message type");

        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_VERSION:
            if (length < MESSAGE_LENGTH_VERSION)
                malformedMessage(messageType);

            version.get(buffer);
            listener.version(version);
            break;
        case MESSAGE_TYPE_ORDER_ADDED:
            if (length < MESSAGE_LENGTH_ORDER_ADDED)
                malformedMessage(messageType);

            orderAdded.get(buffer);
            listener.orderAdded(orderAdded);
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
            throw new PMDException("Unknown message type: " + (char)messageType);
        }
    }

    private void malformedMessage(byte messageType) throws IOException {
        throw new PMDException("Malformed message: " + (char)messageType);
    }

}
