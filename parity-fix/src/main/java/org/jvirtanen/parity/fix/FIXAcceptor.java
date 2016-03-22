package org.jvirtanen.parity.fix;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.jvirtanen.philadelphia.FIXConfig;
import org.jvirtanen.philadelphia.FIXVersion;

class FIXAcceptor {

    private OrderEntryFactory orderEntry;

    private ServerSocketChannel serverChannel;

    private FIXConfig config;

    private FIXAcceptor(OrderEntryFactory orderEntry, ServerSocketChannel serverChannel,
            String senderCompId) {
        this.orderEntry = orderEntry;

        this.serverChannel = serverChannel;

        this.config = new FIXConfig.Builder()
            .setVersion(FIXVersion.FIX_4_4)
            .setSenderCompID(senderCompId)
            .build();
    }

    public static FIXAcceptor open(OrderEntryFactory orderEntry, int port,
            String senderCompId) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);

        return new FIXAcceptor(orderEntry, serverChannel, senderCompId);
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

                return new Session(orderEntry, fix, config);
            } catch (IOException e1) {
                fix.close();

                return null;
            }
        } catch (IOException e2) {
            return null;
        }
    }

}
