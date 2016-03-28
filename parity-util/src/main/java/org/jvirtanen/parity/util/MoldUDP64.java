package org.jvirtanen.parity.util;

import com.paritytrading.nassau.MessageListener;
import com.paritytrading.nassau.moldudp64.MoldUDP64Client;
import com.paritytrading.nassau.moldudp64.MoldUDP64ClientState;
import com.paritytrading.nassau.moldudp64.MoldUDP64ClientStatusListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * This class contains utility methods for MoldUDP64.
 */
public class MoldUDP64 {

    private MoldUDP64() {
    }

    /**
     * Receive messages.
     *
     * @param multicastInterface the multicast interface
     * @param multicastGroup the multicast group
     * @param requestAddress the request address
     * @param listener the message listener
     * @throws IOException if an I/O error occurs
     */
    public static void receive(NetworkInterface multicastInterface,
            InetSocketAddress multicastGroup, InetSocketAddress requestAddress,
            MessageListener listener) throws IOException {
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(new InetSocketAddress(multicastGroup.getPort()));
        channel.join(multicastGroup.getAddress(), multicastInterface);
        channel.configureBlocking(false);

        DatagramChannel requestChannel = DatagramChannel.open(StandardProtocolFamily.INET);

        requestChannel.configureBlocking(false);

        MoldUDP64ClientStatusListener statusListener = new MoldUDP64ClientStatusListener() {

            @Override
            public void state(MoldUDP64Client session, MoldUDP64ClientState next) {
            }

            @Override
            public void downstream(MoldUDP64Client session) {
            }

            @Override
            public void request(MoldUDP64Client session, long sequenceNumber, int requestedMessageCount) {
            }

            @Override
            public void endOfSession(MoldUDP64Client session) {
            }

        };

        receive(new MoldUDP64Client(channel, requestChannel, requestAddress, listener,
                    statusListener));
    }

    private static void receive(MoldUDP64Client client) throws IOException {
        Selector selector = Selector.open();

        SelectionKey channelKey = client.getChannel().register(selector, SelectionKey.OP_READ);

        SelectionKey requestChannelKey = client.getRequestChannel().register(selector, SelectionKey.OP_READ);

        while (true) {
            while (selector.select() == 0);

            if (selector.selectedKeys().contains(channelKey))
                client.receive();

            if (selector.selectedKeys().contains(requestChannelKey))
                client.receiveResponse();

            selector.selectedKeys().clear();
        }
    }

}
