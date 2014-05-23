package org.jvirtanen.parity.system;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCP;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPServer;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPServerStatusListener;
import org.jvirtanen.parity.net.poe.POE;
import org.jvirtanen.parity.net.poe.POEServerListener;

class POESession implements SoupBinTCPServerStatusListener, POEServerListener {

    private SoupBinTCP.LoginAccepted loginAccepted;

    private POE.OrderAccepted orderAccepted;
    private POE.OrderCanceled orderCanceled;

    private ByteBuffer buffer;

    private SoupBinTCPServer transport;

    public POESession() {
        this.loginAccepted = new SoupBinTCP.LoginAccepted();

        this.orderAccepted = new POE.OrderAccepted();
        this.orderCanceled = new POE.OrderCanceled();

        this.buffer = ByteBuffer.allocate(128);
    }

    public void attach(SoupBinTCPServer transport) {
        this.transport = transport;
    }

    public SoupBinTCPServer getTransport() {
        return transport;
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
