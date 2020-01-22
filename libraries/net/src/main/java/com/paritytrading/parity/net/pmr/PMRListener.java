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
package com.paritytrading.parity.net.pmr;

import static com.paritytrading.parity.net.pmr.PMR.*;

import java.io.IOException;

/**
 * The interface for inbound messages.
 */
public interface PMRListener {

    /**
     * Receive a Version message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void version(Version message) throws IOException;

    /**
     * Receive an Order Entered message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderEntered(OrderEntered message) throws IOException;

    /**
     * Receive an Order Added message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderAdded(OrderAdded message) throws IOException;

    /**
     * Receive an Order Canceled message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderCanceled(OrderCanceled message) throws IOException;

    /**
     * Receive a Trade message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void trade(Trade message) throws IOException;

}
