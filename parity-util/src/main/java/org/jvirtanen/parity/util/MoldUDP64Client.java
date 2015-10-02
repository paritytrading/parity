package org.jvirtanen.parity.util;

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
public class MoldUDP64Client {

    private MultiChannelMoldUDP64Client backing;

    private MoldUDP64Client(MultiChannelMoldUDP64Client backing) {
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

        MultiChannelMoldUDP64Client backing = new MultiChannelMoldUDP64Client(channel,
                requestChannel, listener, statusListener);

        return new MoldUDP64Client(backing);
    }

    /**
     * Receive data.
     *
     * @throws IOException if an I/O error occurs
     */
    public void run() throws IOException {
        Selector selector = Selector.open();

        SelectionKey channelKey = backing.getChannel().register(selector, SelectionKey.OP_READ);

        SelectionKey requestChannelKey = backing.getRequestChannel().register(selector, SelectionKey.OP_READ);

        while (true) {
            while (selector.select() == 0);

            if (selector.selectedKeys().contains(channelKey))
                backing.receive();

            if (selector.selectedKeys().contains(requestChannelKey))
                backing.receiveResponse();

            selector.selectedKeys().clear();
        }
    }

}
