package org.jvirtanen.parity.system;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCP;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPServer;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCPServerStatusListener;
import org.jvirtanen.parity.net.poe.POE;
import org.jvirtanen.parity.net.poe.POEServerListener;
import org.jvirtanen.parity.net.poe.POEServerParser;

class Session implements Closeable, SoupBinTCPServerStatusListener, POEServerListener {

    private static SoupBinTCP.LoginAccepted loginAccepted = new SoupBinTCP.LoginAccepted();

    private static POE.OrderAccepted orderAccepted = new POE.OrderAccepted();
    private static POE.OrderCanceled orderCanceled = new POE.OrderCanceled();

    private static ByteBuffer buffer = ByteBuffer.allocate(128);

    private SoupBinTCPServer transport;

    private boolean terminated;

    public Session(SocketChannel channel) {
        this.transport = new SoupBinTCPServer(channel, new POEServerParser(this), this);

        this.terminated = false;
    }

    public SoupBinTCPServer getTransport() {
        return transport;
    }

    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void close() {
        try {
            transport.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void heartbeatTimeout() {
        terminated = true;
    }

    @Override
    public void loginRequest(SoupBinTCP.LoginRequest payload) {
        loginAccepted.session        = payload.requestedSession;
        loginAccepted.sequenceNumber = payload.requestedSequenceNumber;

        try {
            transport.accept(loginAccepted);
        } catch (IOException e) {
            close();
        }
    }

    @Override
    public void logoutRequest() {
        terminated = true;
    }

    @Override
    public void enterOrder(POE.EnterOrder message) {
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
    public void cancelOrder(POE.CancelOrder message) {
        orderCanceled.timestamp        = timestamp();
        orderCanceled.orderId          = message.orderId;
        orderCanceled.canceledQuantity = 0;
        orderCanceled.reason           = POE.ORDER_CANCEL_REASON_REQUEST;

        send(orderCanceled);
    }

    private void send(POE.OutboundMessage message) {
        buffer.clear();
        message.put(buffer);
        buffer.flip();

        try {
            transport.send(buffer);
        } catch (IOException e) {
            close();
        }
    }

    private long timestamp() {
        return (System.currentTimeMillis() - TradingSystem.EPOCH_MILLIS) * 1000 * 1000;
    }

}
