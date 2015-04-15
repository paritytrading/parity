package org.jvirtanen.parity.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import org.jvirtanen.nassau.MessageListener;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientState;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientStatusListener;
import org.jvirtanen.nassau.moldudp64.SingleChannelMoldUDP64Client;

/**
 * This class implements a MoldUDP64 client.
 */
public class MoldUDP64Client implements Closeable {

    private SingleChannelMoldUDP64Client backing;

    private MoldUDP64Client(SingleChannelMoldUDP64Client backing) {
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

        SingleChannelMoldUDP64Client backing = new SingleChannelMoldUDP64Client(channel,
                requestAddress, listener, statusListener);

        return new MoldUDP64Client(backing);
    }

    /**
     * Receive a downstream packet.
     *
     * @throws IOException if an I/O error occurs
     */
    public void receive() throws IOException {
        backing.receive();
    }

    @Override
    public void close() throws IOException {
        backing.close();
    }

}
