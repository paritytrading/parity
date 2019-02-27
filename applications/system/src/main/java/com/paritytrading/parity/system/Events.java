package com.paritytrading.parity.system;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Events implements Runnable {

    private static final int TIMEOUT_MILLIS = 1000;

    private MarketData      marketData;
    private MarketReporting marketReporting;
    private OrderEntry      orderEntry;

    private List<Session> toKeepAlive;
    private List<Session> toCleanUp;

    private Selector selector;

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
