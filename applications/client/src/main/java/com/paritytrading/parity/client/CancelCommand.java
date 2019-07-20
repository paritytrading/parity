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

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.net.poe.POE;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

class CancelCommand implements Command {

    private final POE.CancelOrder message;

    CancelCommand() {
        message = new POE.CancelOrder();
    }

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws IOException {
        try {
            String orderId = arguments.next();

            if (arguments.hasNext())
                throw new IllegalArgumentException();

            execute(client, orderId);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException();
        }
    }

    private void execute(TerminalClient client, String orderId) throws IOException {
        ASCII.putLeft(message.orderId, orderId);
        message.quantity = 0;

        client.getOrderEntry().send(message);
    }

    @Override
    public String getName() {
        return "cancel";
    }

    @Override
    public String getDescription() {
        return "Cancel an order";
    }

    @Override
    public String getUsage() {
        return "cancel <order-id>";
    }

}
