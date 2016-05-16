package com.paritytrading.parity.client;

import com.paritytrading.nassau.soupbintcp.SoupBinTCP;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClient;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClientStatusListener;
import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.net.poe.POEClientListener;
import com.paritytrading.parity.net.poe.POEClientParser;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class OrderEntry implements Closeable {

    private ByteBuffer buffer;

    private Selector selector;

    private SoupBinTCPClient transport;

    private volatile boolean closed;

    private Object txLock;

    private OrderEntry(Selector selector, SocketChannel channel, POEClientListener listener) {
        this.buffer = ByteBuffer.allocate(POE.MAX_INBOUND_MESSAGE_LENGTH);

        this.selector = selector;

        this.transport = new SoupBinTCPClient(channel, POE.MAX_OUTBOUND_MESSAGE_LENGTH,
                new POEClientParser(listener), new StatusListener());

        this.closed = false;

        this.txLock = new Object();

        new Thread(new Receiver()).start();
    }

    public static OrderEntry open(InetSocketAddress address, POEClientListener listener) throws IOException {
        SocketChannel channel = SocketChannel.open();

        channel.connect(address);
        channel.configureBlocking(false);

        Selector selector = Selector.open();

        channel.register(selector, SelectionKey.OP_READ);

        return new OrderEntry(selector, channel, listener);
    }

    @Override
    public void close() {
        closed = true;
    }

    public SoupBinTCPClient getTransport() {
        return transport;
    }

    public void send(POE.InboundMessage message) throws IOException {
        buffer.clear();
        message.put(buffer);
        buffer.flip();

        synchronized (txLock) {
            transport.send(buffer);
        }
    }

    private class StatusListener implements SoupBinTCPClientStatusListener {

        @Override
        public void heartbeatTimeout(SoupBinTCPClient session) {
            close();
        }

        @Override
        public void loginAccepted(SoupBinTCPClient session, SoupBinTCP.LoginAccepted payload) {
        }

        @Override
        public void loginRejected(SoupBinTCPClient session, SoupBinTCP.LoginRejected payload) {
            close();
        }

        @Override
        public void endOfSession(SoupBinTCPClient session) {
        }

    }

    private class Receiver implements Runnable {

        private static final long TIMEOUT_MILLIS = 100;

        @Override
        public void run() {
            try {
                while (!closed) {
                    int numKeys = selector.select(TIMEOUT_MILLIS);
                    if (numKeys > 0) {
                        if (transport.receive() < 0)
                            break;

                        selector.selectedKeys().clear();
                    }

                    synchronized (txLock) {
                        transport.keepAlive();
                    }
                }
            } catch (IOException e) {
            }

            try {
                transport.close();
            } catch (IOException e) {
            }

            try {
                selector.close();
            } catch (IOException e) {
            }
        }

    }

}
