package org.jvirtanen.parity.system;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPServer;
import org.jvirtanen.parity.net.poe.POEServerParser;

class POEServer {

    private ServerSocketChannel serverChannel;

    private POEServer(ServerSocketChannel serverChannel) {
        this.serverChannel = serverChannel;
    }

    public static POEServer create(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);

        return new POEServer(serverChannel);
    }

    public ServerSocketChannel getChannel() {
        return serverChannel;
    }

    public POESession accept() throws IOException {
        SocketChannel channel = serverChannel.accept();
        if (channel == null)
            return null;

        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.configureBlocking(false);

        POESession session = new POESession();

        SoupBinTCPServer transport = new SoupBinTCPServer(channel, new POEServerParser(session), session);

        session.attach(transport);

        return session;
    }

}
