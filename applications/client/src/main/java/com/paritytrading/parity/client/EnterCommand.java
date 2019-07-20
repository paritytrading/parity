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

import static com.paritytrading.parity.client.TerminalClient.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.util.Instrument;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

class EnterCommand implements Command {

    private final POE.EnterOrder message;

    EnterCommand(byte side) {
        this.message = new POE.EnterOrder();

        this.message.side = side;
    }

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws IOException {
        try {
            double quantity   = arguments.nextDouble();
            long   instrument = ASCII.packLong(arguments.next());
            double price      = arguments.nextDouble();

            if (arguments.hasNext())
                throw new IllegalArgumentException();

            Instrument config = client.getInstruments().get(instrument);
            if (config == null)
                throw new IllegalArgumentException();

            execute(client, Math.round(quantity * config.getSizeFactor()), instrument, Math.round(price * config.getPriceFactor()));
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException();
        }
    }

    private void execute(TerminalClient client, long quantity, long instrument, long price) throws IOException {
        String orderId = client.getOrderIdGenerator().next();

        ASCII.putLeft(message.orderId, orderId);
        message.quantity   = quantity;
        message.instrument = instrument;
        message.price      = price;

        client.getOrderEntry().send(message);

        printf("\nOrder ID\n----------------\n%s\n\n", orderId);
    }

    @Override
    public String getName() {
        return message.side == POE.BUY ? "buy" : "sell";
    }

    @Override
    public String getDescription() {
        return "Enter a " + getName() + " order";
    }

    @Override
    public String getUsage() {
        return getName() + " <quantity> <instrument> <price>";
    }

}
