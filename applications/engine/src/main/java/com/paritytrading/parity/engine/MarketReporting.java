package com.paritytrading.parity.engine;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.nassau.moldudp64.MoldUDP64DefaultMessageStore;
import com.paritytrading.nassau.moldudp64.MoldUDP64DownstreamPacket;
import com.paritytrading.nassau.moldudp64.MoldUDP64RequestServer;
import com.paritytrading.nassau.moldudp64.MoldUDP64Server;
import com.paritytrading.parity.net.pmr.PMR;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

class MarketReporting {

    private PMR.Version       version;
    private PMR.OrderEntered  orderEntered;
    private PMR.OrderAdded    orderAdded;
    private PMR.OrderCanceled orderCanceled;
    private PMR.Trade         trade;

    private MoldUDP64Server transport;

    private MoldUDP64RequestServer requestTransport;

    private MoldUDP64DefaultMessageStore messages;

    private MoldUDP64DownstreamPacket packet;

    private ByteBuffer buffer;

    private MarketReporting(MoldUDP64Server transport, MoldUDP64RequestServer requestTransport) {
        this.version       = new PMR.Version();
        this.orderEntered  = new PMR.OrderEntered();
        this.orderAdded    = new PMR.OrderAdded();
        this.orderCanceled = new PMR.OrderCanceled();
        this.trade         = new PMR.Trade();

        this.transport = transport;

        this.requestTransport = requestTransport;

        this.messages = new MoldUDP64DefaultMessageStore();

        this.packet = new MoldUDP64DownstreamPacket();
        this.buffer = ByteBuffer.allocateDirect(1024);
    }

    public static MarketReporting open(String session, NetworkInterface multicastInterface,
            InetSocketAddress multicastGroup,
            InetSocketAddress requestAddress) throws IOException {
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, multicastInterface);
        channel.connect(multicastGroup);

        MoldUDP64Server transport = new MoldUDP64Server(channel, session);

        DatagramChannel requestChannel = DatagramChannel.open();

        requestChannel.bind(requestAddress);
        requestChannel.configureBlocking(false);

        MoldUDP64RequestServer requestTransport = new MoldUDP64RequestServer(requestChannel);

        return new MarketReporting(transport, requestTransport);
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
        version.version = PMR.VERSION;

        send(version);
    }

    public void orderEntered(long username, long orderNumber, byte side, long instrument, long quantity, long price) {
        orderEntered.timestamp   = timestamp();
        orderEntered.username    = username;
        orderEntered.orderNumber = orderNumber;
        orderEntered.side        = side;
        orderEntered.instrument  = instrument;
        orderEntered.quantity    = quantity;
        orderEntered.price       = price;

        send(orderEntered);
    }

    public void orderAdded(long orderNumber) {
        orderAdded.timestamp   = timestamp();
        orderAdded.orderNumber = orderNumber;

        send(orderAdded);
    }

    public void orderCanceled(long orderNumber, long canceledQuantity) {
        orderCanceled.timestamp        = timestamp();
        orderCanceled.orderNumber      = orderNumber;
        orderCanceled.canceledQuantity = canceledQuantity;

        send(orderCanceled);
    }

    public void trade(long restingOrderNumber, long incomingOrderNumber,
            long quantity, long matchNumber) {
        trade.timestamp           = timestamp();
        trade.restingOrderNumber  = restingOrderNumber;
        trade.incomingOrderNumber = incomingOrderNumber;
        trade.quantity            = quantity;
        trade.matchNumber         = matchNumber;

        send(trade);
    }

    private void send(PMR.Message message) {
        buffer.clear();
        message.put(buffer);
        buffer.flip();

        try {
            packet.put(buffer);

            transport.send(packet);

            packet.payload().flip();

            messages.put(packet);

            packet.clear();
        } catch (IOException e) {
            fatal(e);
        }
    }

    private long timestamp() {
        return (System.currentTimeMillis() - MatchingEngine.EPOCH_MILLIS) * 1_000_000;
    }

}
