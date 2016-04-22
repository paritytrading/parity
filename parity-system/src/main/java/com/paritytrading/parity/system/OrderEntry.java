package com.paritytrading.parity.system;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

class OrderEntry {

    private ServerSocketChannel serverChannel;

    private MatchingEngine engine;

    private OrderEntry(ServerSocketChannel serverChannel, MatchingEngine engine) {
        this.serverChannel = serverChannel;

        this.engine = engine;
    }

    public static OrderEntry open(int port, MatchingEngine engine) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);

        return new OrderEntry(serverChannel, engine);
    }

    public ServerSocketChannel getChannel() {
        return serverChannel;
    }

    public Session accept() throws IOException {
        SocketChannel channel = serverChannel.accept();
        if (channel == null)
            return null;

        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.configureBlocking(false);

        return new Session(channel, engine);
    }

}
