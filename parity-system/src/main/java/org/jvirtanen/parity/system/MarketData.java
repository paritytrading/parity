package org.jvirtanen.parity.system;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.jvirtanen.nassau.moldudp64.MoldUDP64DownstreamPacket;
import org.jvirtanen.nassau.moldudp64.MoldUDP64Server;
import org.jvirtanen.parity.net.pmd.PMD;

class MarketData {

    private MoldUDP64Server server;

    private MoldUDP64DownstreamPacket packet;

    private ByteBuffer buffer;

    private MarketData(MoldUDP64Server server) {
        this.server = server;
        this.packet = new MoldUDP64DownstreamPacket();
        this.buffer = ByteBuffer.allocate(1024);
    }

    public static MarketData create(String session, InetSocketAddress address) throws IOException {
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.connect(address);

        MoldUDP64Server server = new MoldUDP64Server(channel, session);

        return new MarketData(server);
    }

    public void send(PMD.Message message) throws IOException {
        buffer.clear();
        message.put(buffer);
        buffer.flip();

        packet.clear();
        packet.put(buffer);

        server.send(packet);
    }

}
