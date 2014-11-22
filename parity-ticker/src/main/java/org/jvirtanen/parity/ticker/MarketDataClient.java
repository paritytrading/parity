package org.jvirtanen.parity.ticker;

import static org.jvirtanen.parity.util.Strings.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.List;
import org.jvirtanen.nassau.moldudp64.MoldUDP64Client;
import org.jvirtanen.nassau.moldudp64.MoldUDP64ClientStatusListener;
import org.jvirtanen.parity.net.pmd.PMDParser;
import org.jvirtanen.parity.top.Market;

class MarketDataClient {

    private MoldUDP64Client transport;

    private MarketDataClient(MoldUDP64Client transport) {
        this.transport = transport;
    }

    public static MarketDataClient open(InetAddress multicastInterface, InetSocketAddress multicastGroup,
            InetSocketAddress requestAddress, List<String> instruments, MarketDataListener listener) throws IOException {
        Market market = new Market(listener);

        for (String instrument : instruments)
            market.open(encodeLong(instrument));

        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(new InetSocketAddress(multicastGroup.getPort()));
        channel.join(multicastGroup.getAddress(), NetworkInterface.getByInetAddress(multicastInterface));

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

        MoldUDP64ClientStatusListener statusListener = new MoldUDP64ClientStatusListener() {

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

        MoldUDP64Client transport = new MoldUDP64Client(channel, requestAddress, new PMDParser(processor), statusListener);

        return new MarketDataClient(transport);
    }

    public void receive() throws IOException {
        transport.receive();
    }

}
