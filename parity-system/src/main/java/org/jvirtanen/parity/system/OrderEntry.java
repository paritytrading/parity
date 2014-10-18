package org.jvirtanen.parity.system;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class OrderEntry implements Runnable {

    private static final int TIMEOUT_MILLIS = 1000;

    private OrderEntryServer server;

    private List<Session> toKeepAlive;
    private List<Session> toCleanUp;

    private Selector selector;

    private OrderEntry(OrderEntryServer server) throws IOException {
        this.server = server;

        this.toKeepAlive = new ArrayList<>();
        this.toCleanUp   = new ArrayList<>();

        this.selector = Selector.open();

        this.server.getChannel().register(this.selector, SelectionKey.OP_ACCEPT, null);
    }

    public static OrderEntry create(int port) throws IOException {
        return new OrderEntry(OrderEntryServer.create(port));
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

                    if (key.isReadable())
                        receive((Session)key.attachment());

                    keys.remove();
                }
            }

            keepAlive();

            cleanUp();
        }
    }

    private void accept() {
        try {
            Session session = server.accept();
            if (session != null) {
                toKeepAlive.add(session);

                session.getTransport().getChannel().register(selector, SelectionKey.OP_READ, session);
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
        for (int i = 0; i < toKeepAlive.size(); i++) {
            Session session = toKeepAlive.get(i);

            try {
                session.getTransport().keepAlive();

                if (session.hasHeartbeatTimeout())
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

            try {
                session.getTransport().close();
            } catch (IOException e) {
            }
        }

        if (!toCleanUp.isEmpty())
            toCleanUp.clear();
    }

}
