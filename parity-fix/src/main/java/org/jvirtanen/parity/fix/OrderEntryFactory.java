package org.jvirtanen.parity.fix;

import com.paritytrading.nassau.soupbintcp.SoupBinTCPClient;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClientStatusListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import org.jvirtanen.parity.net.poe.POE;
import org.jvirtanen.parity.net.poe.POEClientListener;
import org.jvirtanen.parity.net.poe.POEClientParser;

class OrderEntryFactory {

    private InetSocketAddress address;

    public OrderEntryFactory(InetSocketAddress address) {
        this.address = address;
    }

    public SoupBinTCPClient create(POEClientListener listener,
            SoupBinTCPClientStatusListener statusListener) throws IOException {
        SocketChannel channel = SocketChannel.open();

        channel.connect(address);

        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.configureBlocking(false);

        return new SoupBinTCPClient(channel, POE.MAX_OUTBOUND_MESSAGE_LENGTH,
                new POEClientParser(listener), statusListener);
    }

}
