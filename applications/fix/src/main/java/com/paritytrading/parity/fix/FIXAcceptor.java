package com.paritytrading.parity.fix;

import java.io.IOException;

import com.paritytrading.parity.util.Instruments;
import com.paritytrading.philadelphia.FIXConfig;
import com.paritytrading.philadelphia.FIXVersion;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

class FIXAcceptor {

    private OrderEntryFactory orderEntry;

    private ServerSocketChannel serverChannel;

    private FIXConfig config;

    private Instruments instruments;

    private FIXAcceptor(OrderEntryFactory orderEntry,
            ServerSocketChannel serverChannel, String senderCompId,
            Instruments instruments) {
        this.orderEntry = orderEntry;

        this.serverChannel = serverChannel;

        this.config = new FIXConfig.Builder()
            .setVersion(FIXVersion.FIX_4_4)
            .setSenderCompID(senderCompId)
            .build();

        this.instruments = instruments;
    }

    public static FIXAcceptor open(OrderEntryFactory orderEntry,
            InetSocketAddress address, String senderCompId,
            Instruments instruments) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.bind(address);
        serverChannel.configureBlocking(false);

        return new FIXAcceptor(orderEntry, serverChannel, senderCompId, instruments);
    }

    public ServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public Session accept() {
        try {
            SocketChannel fix = serverChannel.accept();
            if (fix == null)
                return null;

            try {
                fix.setOption(StandardSocketOptions.TCP_NODELAY, true);
                fix.configureBlocking(false);

                return new Session(orderEntry, fix, config, instruments);
            } catch (IOException e1) {
                fix.close();

                return null;
            }
        } catch (IOException e2) {
            return null;
        }
    }

}
