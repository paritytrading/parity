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

import java.io.IOException;

/**
 * The interface for inbound messages on the client side.
 */
public interface POEClientListener {

    /**
     * Receive an Order Accepted message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderAccepted(OrderAccepted message) throws IOException;

    /**
     * Receive an Order Rejected message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderRejected(OrderRejected message) throws IOException;

    /**
     * Receive an Order Executed message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderExecuted(OrderExecuted message) throws IOException;

    /**
     * Receive an Order Canceled message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderCanceled(OrderCanceled message) throws IOException;

}
