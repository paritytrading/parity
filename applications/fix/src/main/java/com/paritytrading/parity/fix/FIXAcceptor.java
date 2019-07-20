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
package com.paritytrading.parity.fix;

import java.io.IOException;

import com.paritytrading.parity.util.Instruments;
import com.paritytrading.philadelphia.FIXConfig;
import com.paritytrading.philadelphia.FIXVersion;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

class FIXAcceptor {

    private final OrderEntryFactory orderEntry;

    private final ServerSocketChannel serverChannel;

    private final FIXConfig config;

    private final Instruments instruments;

    private FIXAcceptor(OrderEntryFactory orderEntry,
            ServerSocketChannel serverChannel, String senderCompId,
            Instruments instruments) {
        this.orderEntry = orderEntry;

        this.serverChannel = serverChannel;

        this.config = new FIXConfig.Builder()
            .setVersion(FIXVersion.FIX_4_4)
            .setSenderCompID(senderCompId)
            .build();

        this.instruments = instruments;
    }

    static FIXAcceptor open(OrderEntryFactory orderEntry,
            InetSocketAddress address, String senderCompId,
            Instruments instruments) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.bind(address);
        serverChannel.configureBlocking(false);

        return new FIXAcceptor(orderEntry, serverChannel, senderCompId, instruments);
    }

    ServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    Session accept() {
        try {
            SocketChannel fix = serverChannel.accept();
            if (fix == null)
                return null;

            try {
                fix.setOption(StandardSocketOptions.TCP_NODELAY, true);
                fix.configureBlocking(false);

                return new Session(orderEntry, fix, config, instruments);
            } catch (IOException e1) {
                fix.close();

                return null;
            }
        } catch (IOException e2) {
            return null;
        }
    }

}
