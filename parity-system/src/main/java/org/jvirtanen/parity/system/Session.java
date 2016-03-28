package org.jvirtanen.parity.system;

import static org.jvirtanen.lang.Strings.*;

import com.paritytrading.nassau.soupbintcp.SoupBinTCP;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPServer;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPServerStatusListener;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import org.jvirtanen.parity.net.poe.POE;
import org.jvirtanen.parity.net.poe.POEServerListener;
import org.jvirtanen.parity.net.poe.POEServerParser;

class Session implements Closeable, SoupBinTCPServerStatusListener, POEServerListener {

    private static SoupBinTCP.LoginAccepted loginAccepted = new SoupBinTCP.LoginAccepted();

    private static POE.OrderAccepted orderAccepted = new POE.OrderAccepted();
    private static POE.OrderRejected orderRejected = new POE.OrderRejected();
    private static POE.OrderExecuted orderExecuted = new POE.OrderExecuted();
    private static POE.OrderCanceled orderCanceled = new POE.OrderCanceled();

    private static ByteBuffer buffer = ByteBuffer.allocate(POE.MAX_OUTBOUND_MESSAGE_LENGTH);

    private SoupBinTCPServer transport;

    private HashMap<String, Order> orders;

    private HashSet<String> orderIds;

    private MatchingEngine engine;

    private boolean terminated;

    private long username;

    public Session(SocketChannel channel, MatchingEngine engine) {
        this.transport = new SoupBinTCPServer(channel, POE.MAX_INBOUND_MESSAGE_LENGTH,
                new POEServerParser(this), this);

        this.orders   = new HashMap<>();
        this.orderIds = new HashSet<>();

        this.engine = engine;

        this.terminated = false;
    }

    public SoupBinTCPServer getTransport() {
        return transport;
    }

    public long getUsername() {
        return username;
    }

    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void close() {
        for (Order order : orders.values())
            engine.cancel(order);

        try {
            transport.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void heartbeatTimeout(SoupBinTCPServer session) {
        terminated = true;
    }

    @Override
    public void loginRequest(SoupBinTCPServer session, SoupBinTCP.LoginRequest payload) {
        if (username != 0) {
            close();
            return;
        }

        loginAccepted.session        = payload.requestedSession;
        loginAccepted.sequenceNumber = payload.requestedSequenceNumber;

        try {
            transport.accept(loginAccepted);
        } catch (IOException e) {
            close();
        }

        username = encodeLong(payload.username);
    }

    @Override
    public void logoutRequest(SoupBinTCPServer session) {
        terminated = true;
    }

    @Override
    public void enterOrder(POE.EnterOrder message) {
        if (username == 0) {
            close();
            return;
        }

        if (orderIds.contains(message.orderId))
            return;

        engine.enterOrder(message, this);
    }

    @Override
    public void cancelOrder(POE.CancelOrder message) {
        if (username == 0) {
            close();
            return;
        }

        Order order = orders.get(message.orderId);
        if (order == null)
            return;

        engine.cancelOrder(message, order);
    }

    public void track(Order order) {
        orders.put(order.getOrderId(), order);
    }

    public void release(Order order) {
        orders.remove(order.getOrderId());
    }

    public void orderAccepted(POE.EnterOrder message, Order order) {
        orderAccepted.timestamp   = timestamp();
        orderAccepted.orderId     = message.orderId;
        orderAccepted.side        = message.side;
        orderAccepted.instrument  = message.instrument;
        orderAccepted.quantity    = message.quantity;
        orderAccepted.price       = message.price;
        orderAccepted.orderNumber = order.getOrderNumber();

        send(orderAccepted);

        orderIds.add(message.orderId);
    }

    public void orderRejected(POE.EnterOrder message, byte reason) {
        orderRejected.timestamp = timestamp();
        orderRejected.orderId   = message.orderId;
        orderRejected.reason    = reason;

        send(orderRejected);
    }

    public void orderExecuted(long price, long quantity, byte liquidityFlag,
            long matchNumber, Order order) {
        orderExecuted.timestamp     = timestamp();
        orderExecuted.orderId       = order.getOrderId();
        orderExecuted.quantity      = quantity;
        orderExecuted.price         = price;
        orderExecuted.liquidityFlag = liquidityFlag;
        orderExecuted.matchNumber   = matchNumber;

        send(orderExecuted);
    }

    public void orderCanceled(long canceledQuantity, byte reason, Order order) {
        orderCanceled.timestamp        = timestamp();
        orderCanceled.orderId          = order.getOrderId();
        orderCanceled.canceledQuantity = canceledQuantity;
        orderCanceled.reason           = reason;

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
