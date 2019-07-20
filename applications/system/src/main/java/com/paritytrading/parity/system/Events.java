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
package com.paritytrading.parity.system;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Events implements Runnable {

    private static final int TIMEOUT_MILLIS = 1000;

    private final MarketData      marketData;
    private final MarketReporting marketReporting;
    private final OrderEntry      orderEntry;

    private final List<Session> toKeepAlive;
    private final List<Session> toCleanUp;

    private final Selector selector;

    Events(MarketData marketData, MarketReporting marketReporting,
            OrderEntry orderEntry) throws IOException {
        this.marketData      = marketData;
        this.marketReporting = marketReporting;
        this.orderEntry      = orderEntry;

        this.toKeepAlive = new ArrayList<>();
        this.toCleanUp   = new ArrayList<>();

        this.selector = Selector.open();

        this.marketData.getRequestTransport().getChannel().register(this.selector,
                SelectionKey.OP_READ, this.marketData);
        this.marketReporting.getRequestTransport().getChannel().register(this.selector,
                SelectionKey.OP_READ, this.marketReporting);
        this.orderEntry.getChannel().register(this.selector, SelectionKey.OP_ACCEPT, null);
    }

    @Override
    public void run() {
        int numKeys;

        while (true) {
            try {
                numKeys = selector.select(TIMEOUT_MILLIS);
            } catch (IOException e) {
                break;
            }

            if (numKeys > 0) {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();

                    if (key.isAcceptable())
                        accept();

                    if (key.isReadable()) {
                        Object attachment = key.attachment();
                        if (attachment == marketData)
                            marketData.serve();
                        else if (attachment == marketReporting)
                            marketReporting.serve();
                        else
                            receive((Session)attachment);
                    }

                    keys.remove();
                }
            }

            keepAlive();

            cleanUp();
        }
    }

    private void accept() {
        try {
            Session session = orderEntry.accept();
            if (session != null) {
                session.getTransport().getChannel().register(selector, SelectionKey.OP_READ, session);

                toKeepAlive.add(session);
            }
        } catch (IOException e) {
        }
    }

    private void receive(Session session) {
        try {
            if (session.getTransport().receive() < 0)
                toCleanUp.add(session);
        } catch (IOException e) {
            toCleanUp.add(session);
        }
    }

    private void keepAlive() {
        try {
            marketData.getTransport().keepAlive();
        } catch (IOException e) {
        }

        try {
            marketReporting.getTransport().keepAlive();
        } catch (IOException e) {
        }

        for (int i = 0; i < toKeepAlive.size(); i++) {
            Session session = toKeepAlive.get(i);

            try {
                session.getTransport().keepAlive();

                if (session.isTerminated())
                    toCleanUp.add(session);
            } catch (IOException e) {
                toCleanUp.add(session);
            }
        }
    }

    private void cleanUp() {
        for (int i = 0; i < toCleanUp.size(); i++) {
            Session session = toCleanUp.get(i);

            toKeepAlive.remove(session);

            session.close();
        }

        if (!toCleanUp.isEmpty())
            toCleanUp.clear();
    }

}
