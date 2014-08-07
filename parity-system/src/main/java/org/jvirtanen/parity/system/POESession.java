package org.jvirtanen.parity.system;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCP;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPServer;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPServerStatusListener;
import org.jvirtanen.parity.net.poe.POE;
import org.jvirtanen.parity.net.poe.POEServerListener;
import org.jvirtanen.parity.net.poe.POEServerParser;

class POESession implements SoupBinTCPServerStatusListener, POEServerListener {

    private SoupBinTCP.LoginAccepted loginAccepted;

    private POE.OrderAccepted orderAccepted;
    private POE.OrderCanceled orderCanceled;

    private ByteBuffer buffer;

    private SoupBinTCPServer transport;

    private boolean heartbeatTimeout;

    public POESession(SocketChannel channel) {
        this.loginAccepted = new SoupBinTCP.LoginAccepted();

        this.orderAccepted = new POE.OrderAccepted();
        this.orderCanceled = new POE.OrderCanceled();

        this.buffer = ByteBuffer.allocate(128);

        this.transport = new SoupBinTCPServer(channel, new POEServerParser(this), this);

        this.heartbeatTimeout = false;
    }

    public SoupBinTCPServer getTransport() {
        return transport;
    }

    @Override
    public void heartbeatTimeout() {
        heartbeatTimeout = true;
    }

    public boolean hasHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    @Override
    public void loginRequest(SoupBinTCP.LoginRequest payload) throws IOException {
        loginAccepted.session        = payload.requestedSession;
        loginAccepted.sequenceNumber = payload.requestedSequenceNumber;

        transport.accept(loginAccepted);
    }

    @Override
    public void logoutRequest() throws IOException {
    }

    @Override
    public void enterOrder(POE.EnterOrder message) throws IOException {
        orderAccepted.timestamp   = timestamp();
        orderAccepted.orderId     = message.orderId;
        orderAccepted.side        = message.side;
        orderAccepted.instrument  = message.instrument;
        orderAccepted.quantity    = 0;
        orderAccepted.price       = message.price;
        orderAccepted.orderNumber = 0;

        send(orderAccepted);
    }

    @Override
    public void cancelOrder(POE.CancelOrder message) throws IOException {
        orderCanceled.timestamp        = timestamp();
        orderCanceled.orderId          = message.orderId;
        orderCanceled.canceledQuantity = 0;
        orderCanceled.reason           = POE.ORDER_CANCEL_REASON_REQUEST;

        send(orderCanceled);
    }

    private void send(POE.OutboundMessage message) throws IOException {
        buffer.clear();
        message.put(buffer);
        buffer.flip();

        transport.send(buffer);
    }

    private long timestamp() {
        return (System.currentTimeMillis() - TradingSystem.EPOCH_MILLIS) * 1000 * 1000;
    }

}
