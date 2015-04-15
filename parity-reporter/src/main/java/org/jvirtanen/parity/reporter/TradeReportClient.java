package org.jvirtanen.parity.reporter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import org.jvirtanen.parity.net.ptr.PTRListener;
import org.jvirtanen.parity.net.ptr.PTRParser;
import org.jvirtanen.parity.util.MoldUDP64Client;

class TradeReportClient {

    private MoldUDP64Client transport;

    private TradeReportClient(MoldUDP64Client transport) {
        this.transport = transport;
    }

    public static TradeReportClient open(NetworkInterface multicastInterface,
            InetSocketAddress multicastGroup, InetSocketAddress requestAddress,
            PTRListener listener) throws IOException {
        MoldUDP64Client transport = MoldUDP64Client.open(multicastInterface,
                multicastGroup, requestAddress, new PTRParser(listener));

        return new TradeReportClient(transport);
    }

    public void receive() throws IOException {
        transport.receive();
    }

}
