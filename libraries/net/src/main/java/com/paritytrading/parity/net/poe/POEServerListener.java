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

import com.paritytrading.parity.net.poe.POE.*;

import java.io.IOException;

/**
 * The interface for inbound messages on the server side.
 */
public interface POEServerListener {

    /**
     * Receive an Enter Order message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void enterOrder(EnterOrder message) throws IOException;

    /**
     * Receive a Cancel Order message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void cancelOrder(CancelOrder message) throws IOException;

}
