package com.paritytrading.parity.fix;

import com.paritytrading.nassau.soupbintcp.SoupBinTCPClient;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClientStatusListener;
import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.net.poe.POEClientListener;
import com.paritytrading.parity.net.poe.POEClientParser;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

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
