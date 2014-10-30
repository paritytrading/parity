package org.jvirtanen.parity.system;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.jvirtanen.nassau.moldudp64.MoldUDP64DownstreamPacket;
import org.jvirtanen.nassau.moldudp64.MoldUDP64Server;
import org.jvirtanen.parity.net.pmd.PMD;

class MarketDataServer {

    private PMD.Version version;

    private MoldUDP64Server transport;

    private MoldUDP64DownstreamPacket packet;

    private ByteBuffer buffer;

    private MarketDataServer(MoldUDP64Server transport) {
        this.version = new PMD.Version();

        this.transport = transport;

        this.packet = new MoldUDP64DownstreamPacket();
        this.buffer = ByteBuffer.allocate(1024);
    }

    public static MarketDataServer create(String session, InetSocketAddress address) throws IOException {
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.connect(address);

        MoldUDP64Server transport = new MoldUDP64Server(channel, session);

        return new MarketDataServer(transport);
    }

    public MoldUDP64Server getTransport() {
        return transport;
    }

    public void version() throws IOException {
        version.version = PMD.VERSION;

        send(version);
    }

    private void send(PMD.Message message) throws IOException {
        buffer.clear();
        message.put(buffer);
        buffer.flip();

        packet.clear();
        packet.put(buffer);

        transport.send(packet);
    }

}
