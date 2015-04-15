package org.jvirtanen.parity.reporter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientState;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientStatusListener;
import org.jvirtanen.nassau.moldudp64.SingleChannelMoldUDP64Client;
import org.jvirtanen.parity.net.ptr.PTRListener;
import org.jvirtanen.parity.net.ptr.PTRParser;

class TradeReportClient {

    private SingleChannelMoldUDP64Client transport;

    private TradeReportClient(SingleChannelMoldUDP64Client transport) {
        this.transport = transport;
    }

    public static TradeReportClient open(NetworkInterface multicastInterface, InetSocketAddress multicastGroup,
            InetSocketAddress requestAddress, PTRListener listener) throws IOException {

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

        SingleChannelMoldUDP64Client transport = new SingleChannelMoldUDP64Client(channel,
                requestAddress, new PTRParser(listener), statusListener);

        return new TradeReportClient(transport);
    }

    public void receive() throws IOException {
        transport.receive();
    }

}
