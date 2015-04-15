package org.jvirtanen.parity.ticker;

import static org.jvirtanen.lang.Strings.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.List;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientState;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientStatusListener;
import org.jvirtanen.nassau.moldudp64.SingleChannelMoldUDP64Client;
import org.jvirtanen.parity.net.pmd.PMDParser;
import org.jvirtanen.parity.top.Market;

class MarketDataClient {

    private SingleChannelMoldUDP64Client transport;

    private MarketDataClient(SingleChannelMoldUDP64Client transport) {
        this.transport = transport;
    }

    public static MarketDataClient open(NetworkInterface multicastInterface,
            InetSocketAddress multicastGroup, InetSocketAddress requestAddress,
            List<String> instruments, MarketDataListener listener) throws IOException {
        Market market = new Market(listener);

        for (String instrument : instruments)
            market.open(encodeLong(instrument));

        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(new InetSocketAddress(multicastGroup.getPort()));
        channel.join(multicastGroup.getAddress(), multicastInterface);

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

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
                requestAddress, new PMDParser(processor), statusListener);

        return new MarketDataClient(transport);
    }

    public void receive() throws IOException {
        transport.receive();
    }

}
