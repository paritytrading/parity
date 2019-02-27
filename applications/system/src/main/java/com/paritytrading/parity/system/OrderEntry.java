package com.paritytrading.parity.system;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

class OrderEntry {

    private ServerSocketChannel serverChannel;

    private OrderBooks books;

    private OrderEntry(ServerSocketChannel serverChannel, OrderBooks books) {
        this.serverChannel = serverChannel;

        this.books = books;
    }

    static OrderEntry open(InetSocketAddress address, OrderBooks books) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.bind(address);
        serverChannel.configureBlocking(false);

        return new OrderEntry(serverChannel, books);
    }

    ServerSocketChannel getChannel() {
        return serverChannel;
    }

    Session accept() throws IOException {
        SocketChannel channel = serverChannel.accept();
        if (channel == null)
            return null;

        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.configureBlocking(false);

        return new Session(channel, books);
    }

}
