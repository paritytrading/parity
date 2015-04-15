package org.jvirtanen.parity.ticker;

import static org.jvirtanen.lang.Strings.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.List;
import org.jvirtanen.parity.net.pmd.PMDParser;
import org.jvirtanen.parity.top.Market;
import org.jvirtanen.parity.util.MoldUDP64Client;

class MarketDataClient {

    private MoldUDP64Client transport;

    private MarketDataClient(MoldUDP64Client transport) {
        this.transport = transport;
    }

    public static MarketDataClient open(NetworkInterface multicastInterface,
            InetSocketAddress multicastGroup, InetSocketAddress requestAddress,
            List<String> instruments, MarketDataListener listener) throws IOException {
        Market market = new Market(listener);

        for (String instrument : instruments)
            market.open(encodeLong(instrument));

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

        MoldUDP64Client transport = MoldUDP64Client.open(multicastInterface,
                multicastGroup, requestAddress, new PMDParser(processor));

        return new MarketDataClient(transport);
    }

    public void receive() throws IOException {
        transport.receive();
    }

}
