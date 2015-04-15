package org.jvirtanen.parity.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import org.jvirtanen.nassau.MessageListener;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientState;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientStatusListener;
import org.jvirtanen.nassau.moldudp64.MultiChannelMoldUDP64Client;

/**
 * This class implements a MoldUDP64 client.
 */
public class MoldUDP64Client implements Closeable {

    private Selector selector;

    private SelectionKey requestChannelKey;

    private MultiChannelMoldUDP64Client backing;

    private MoldUDP64Client(Selector selector, SelectionKey requestChannelKey,
            MultiChannelMoldUDP64Client backing) {
        this.selector = selector;

        this.requestChannelKey = requestChannelKey;

        this.backing = backing;
    }

    /**
     * Open a MoldUDP64 client.
     *
     * @param multicastInterface the multicast interface
     * @param multicastGroup the multicast group
     * @param requestAddress the request address
     * @param listener the message listener
     * @return a MoldUDP64 client
     * @throws IOException if an I/O error occurs
     */
    public static MoldUDP64Client open(NetworkInterface multicastInterface,
            InetSocketAddress multicastGroup, InetSocketAddress requestAddress,
            MessageListener listener) throws IOException {
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(new InetSocketAddress(multicastGroup.getPort()));
        channel.join(multicastGroup.getAddress(), multicastInterface);
        channel.configureBlocking(false);

        DatagramChannel requestChannel = DatagramChannel.open(StandardProtocolFamily.INET);

        requestChannel.connect(requestAddress);
        requestChannel.configureBlocking(false);

        MoldUDP64ClientStatusListener statusListener = new MoldUDP64ClientStatusListener() {

            @Override
            public void state(MoldUDP64ClientState next) {
            }

            @Override
            public void downstream() {
            }

            @Override
            public void request(long sequenceNumber, int requestedMessageCount) {
            }

            @Override
            public void endOfSession() {
            }

        };

        Selector selector = Selector.open();

        channel.register(selector, SelectionKey.OP_READ, null);

        SelectionKey requestChannelKey = requestChannel.register(selector,
                SelectionKey.OP_READ, null);

        MultiChannelMoldUDP64Client backing = new MultiChannelMoldUDP64Client(channel,
                requestChannel, listener, statusListener);

        return new MoldUDP64Client(selector, requestChannelKey, backing);
    }

    /**
     * Receive data.
     *
     * @throws IOException if an I/O error occurs
     */
    public void receive() throws IOException {
        while (selector.select() == 0);

        if (requestChannelKey.isReadable()) {
            backing.receiveResponse();

            selector.selectedKeys().remove(requestChannelKey);
        }

        if (selector.selectedKeys().size() > 0) {
            backing.receive();

            selector.selectedKeys().clear();
        }
    }

    @Override
    public void close() throws IOException {
        selector.close();

        backing.close();
    }

}
