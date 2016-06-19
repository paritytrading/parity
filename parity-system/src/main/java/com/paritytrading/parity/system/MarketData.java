package com.paritytrading.parity.system;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.nassau.moldudp64.MoldUDP64DefaultMessageStore;
import com.paritytrading.nassau.moldudp64.MoldUDP64DownstreamPacket;
import com.paritytrading.nassau.moldudp64.MoldUDP64RequestServer;
import com.paritytrading.nassau.moldudp64.MoldUDP64Server;
import com.paritytrading.parity.net.pmd.PMD;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

class MarketData {

    private PMD.Version       version;
    private PMD.Seconds       seconds;
    private PMD.OrderAdded    orderAdded;
    private PMD.OrderExecuted orderExecuted;
    private PMD.OrderCanceled orderCanceled;
    private PMD.OrderDeleted  orderDeleted;

    private MoldUDP64Server transport;

    private MoldUDP64RequestServer requestTransport;

    private MoldUDP64DefaultMessageStore messages;

    private MoldUDP64DownstreamPacket packet;

    private ByteBuffer buffer;

    private long previousSecond;

    private long timestamp;

    private MarketData(MoldUDP64Server transport, MoldUDP64RequestServer requestTransport) {
        this.version       = new PMD.Version();
        this.seconds       = new PMD.Seconds();
        this.orderAdded    = new PMD.OrderAdded();
        this.orderExecuted = new PMD.OrderExecuted();
        this.orderCanceled = new PMD.OrderCanceled();
        this.orderDeleted  = new PMD.OrderDeleted();

        this.transport = transport;

        this.requestTransport = requestTransport;

        this.messages = new MoldUDP64DefaultMessageStore();

        this.packet = new MoldUDP64DownstreamPacket();
        this.buffer = ByteBuffer.allocate(1024);
    }

    public static MarketData open(String session, NetworkInterface multicastInterface,
            InetSocketAddress multicastGroup, int requestPort) throws IOException {
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, multicastInterface);
        channel.connect(multicastGroup);

        MoldUDP64Server transport = new MoldUDP64Server(channel, session);

        DatagramChannel requestChannel = DatagramChannel.open();

        requestChannel.bind(new InetSocketAddress(requestPort));
        requestChannel.configureBlocking(false);

        MoldUDP64RequestServer requestTransport = new MoldUDP64RequestServer(requestChannel);

        return new MarketData(transport, requestTransport);
    }

    public MoldUDP64Server getTransport() {
        return transport;
    }

    public MoldUDP64RequestServer getRequestTransport() {
        return requestTransport;
    }

    public void serve() {
        try {
            requestTransport.serve(messages);
        } catch (IOException e) {
            fatal(e);
        }
    }

    public void version() {
        version.version = PMD.VERSION;

        enqueue(version);

        transmit();
    }

    public void orderAdded(long orderNumber, byte side, long instrument, long quantity, long price) {
        timestamp();

        orderAdded.timestamp   = timestamp;
        orderAdded.orderNumber = orderNumber;
        orderAdded.side        = side;
        orderAdded.instrument  = instrument;
        orderAdded.quantity    = quantity;
        orderAdded.price       = price;

        enqueue(orderAdded);

        transmit();
    }

    public void orderExecuted(long orderNumber, long quantity, long matchNumber) {
        timestamp();

        orderExecuted.timestamp   = timestamp;
        orderExecuted.orderNumber = orderNumber;
        orderExecuted.quantity    = quantity;
        orderExecuted.matchNumber = matchNumber;

        enqueue(orderExecuted);

        transmit();
    }

    public void orderCanceled(long orderNumber, long canceledQuantity) {
        timestamp();

        orderCanceled.timestamp        = timestamp;
        orderCanceled.orderNumber      = orderNumber;
        orderCanceled.canceledQuantity = canceledQuantity;

        enqueue(orderCanceled);

        transmit();
    }

    public void orderDeleted(long orderNumber) {
        timestamp();

        orderDeleted.timestamp   = timestamp;
        orderDeleted.orderNumber = orderNumber;

        enqueue(orderDeleted);

        transmit();
    }

    private void timestamp() {
        long currentTimeMillis = System.currentTimeMillis() - TradingSystem.EPOCH_MILLIS;

        long currentSecond = currentTimeMillis / 1000;

        if (currentSecond != previousSecond) {
            seconds.second = currentSecond;

            enqueue(seconds);
        }

        previousSecond = currentSecond;

        timestamp = (currentTimeMillis - currentSecond * 1000) * 1_000_000;
    }

    private void enqueue(PMD.Message message) {
        buffer.clear();
        message.put(buffer);
        buffer.flip();

        try {
            packet.put(buffer);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private void transmit() {
        try {
            transport.send(packet);

            packet.payload().flip();

            messages.put(packet);

            packet.clear();
        } catch (IOException e) {
            fatal(e);
        }
    }

}
