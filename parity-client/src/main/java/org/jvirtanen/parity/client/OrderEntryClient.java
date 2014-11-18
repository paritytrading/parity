package org.jvirtanen.parity.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCP;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPClient;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPClientStatusListener;
import org.jvirtanen.parity.net.poe.POE;
import org.jvirtanen.parity.net.poe.POEClientListener;
import org.jvirtanen.parity.net.poe.POEClientParser;

public class OrderEntryClient implements Closeable {

    private ByteBuffer buffer;

    private Selector selector;

    private SoupBinTCPClient transport;

    private volatile boolean closed;

    private OrderEntryClient(Selector selector, SocketChannel channel, POEClientListener listener) {
        this.buffer = ByteBuffer.allocate(POE.MAX_INBOUND_MESSAGE_LENGTH);

        this.selector = selector;

        this.transport = new SoupBinTCPClient(channel, POE.MAX_OUTBOUND_MESSAGE_LENGTH,
                new POEClientParser(listener), new StatusListener());

        this.closed = false;

        new Thread(new Receiver()).start();
    }

    public static OrderEntryClient open(InetSocketAddress address, POEClientListener listener) throws IOException {
        SocketChannel channel = SocketChannel.open();

        channel.connect(address);
        channel.configureBlocking(false);

        Selector selector = Selector.open();

        channel.register(selector, SelectionKey.OP_READ);

        return new OrderEntryClient(selector, channel, listener);
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

        transport.send(buffer);
    }

    private class StatusListener implements SoupBinTCPClientStatusListener {

        @Override
        public void heartbeatTimeout() {
            close();
        }

        @Override
        public void loginAccepted(SoupBinTCP.LoginAccepted payload) {
        }

        @Override
        public void loginRejected(SoupBinTCP.LoginRejected payload) {
            close();
        }

        @Override
        public void endOfSession() {
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

                    transport.keepAlive();
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
