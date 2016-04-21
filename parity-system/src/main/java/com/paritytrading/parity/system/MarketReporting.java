package com.paritytrading.parity.system;

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

    private PMR.Order  order;
    private PMR.Cancel cancel;
    private PMR.Trade  trade;

    private MoldUDP64Server transport;

    private MoldUDP64RequestServer requestTransport;

    private MoldUDP64DefaultMessageStore messages;

    private MoldUDP64DownstreamPacket packet;

    private ByteBuffer buffer;

    private MarketReporting(MoldUDP64Server transport, MoldUDP64RequestServer requestTransport) {
        this.order  = new PMR.Order();
        this.cancel = new PMR.Cancel();
        this.trade  = new PMR.Trade();

        this.transport = transport;

        this.requestTransport = requestTransport;

        this.messages = new MoldUDP64DefaultMessageStore();

        this.packet = new MoldUDP64DownstreamPacket();
        this.buffer = ByteBuffer.allocate(1024);
    }

    public static MarketReporting open(String session, NetworkInterface multicastInterface,
            InetSocketAddress multicastGroup, int requestPort) throws IOException {
        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, multicastInterface);
        channel.connect(multicastGroup);

        MoldUDP64Server transport = new MoldUDP64Server(channel, session);

        DatagramChannel requestChannel = DatagramChannel.open();

        requestChannel.bind(new InetSocketAddress(requestPort));
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

    public void order(long username, long orderNumber, byte side, long instrument, long quantity, long price) {
        order.timestamp   = timestamp();
        order.username    = username;
        order.orderNumber = orderNumber;
        order.side        = side;
        order.instrument  = instrument;
        order.quantity    = quantity;
        order.price       = price;

        send(order);
    }

    public void cancel(long username, long orderNumber, long canceledQuantity) {
        cancel.timestamp        = timestamp();
        cancel.username         = username;
        cancel.orderNumber      = orderNumber;
        cancel.canceledQuantity = canceledQuantity;

        send(cancel);
    }

    public void trade(long matchNumber, long instrument, long quantity, long price, long buyer,
            long buyOrderNumber, long seller, long sellOrderNumber) {
        trade.timestamp       = timestamp();
        trade.matchNumber     = matchNumber;
        trade.instrument      = instrument;
        trade.quantity        = quantity;
        trade.price           = price;
        trade.buyer           = buyer;
        trade.buyOrderNumber  = buyOrderNumber;
        trade.seller          = seller;
        trade.sellOrderNumber = sellOrderNumber;

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
        return (System.currentTimeMillis() - TradingSystem.EPOCH_MILLIS) * 1000 * 1000;
    }

}
