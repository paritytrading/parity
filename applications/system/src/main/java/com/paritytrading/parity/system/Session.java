package com.paritytrading.parity.system;

import static it.unimi.dsi.fastutil.bytes.ByteArrays.HASH_STRATEGY;

import com.paritytrading.foundation.ByteArrays;
import com.paritytrading.nassau.soupbintcp.SoupBinTCP;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPServer;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPServerStatusListener;
import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.net.poe.POEServerListener;
import com.paritytrading.parity.net.poe.POEServerParser;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class Session implements Closeable, SoupBinTCPServerStatusListener, POEServerListener {

    private static final byte SPACE = ' ';

    private static final SoupBinTCP.LoginAccepted loginAccepted;

    private static final POE.OrderAccepted orderAccepted;
    private static final POE.OrderRejected orderRejected;
    private static final POE.OrderExecuted orderExecuted;
    private static final POE.OrderCanceled orderCanceled;

    private static final ByteBuffer buffer;

    static {
        loginAccepted = new SoupBinTCP.LoginAccepted();

        orderAccepted = new POE.OrderAccepted();
        orderRejected = new POE.OrderRejected();
        orderExecuted = new POE.OrderExecuted();
        orderCanceled = new POE.OrderCanceled();

        buffer = ByteBuffer.allocateDirect(POE.MAX_OUTBOUND_MESSAGE_LENGTH);
    }

    private final SoupBinTCPServer transport;

    private final Object2ObjectOpenCustomHashMap<byte[], Order> orders;

    private final ObjectOpenCustomHashSet<byte[]> orderIds;

    private final OrderBooks books;

    private boolean terminated;

    private long username;

    Session(SocketChannel channel, OrderBooks books) {
        this.transport = new SoupBinTCPServer(channel, POE.MAX_INBOUND_MESSAGE_LENGTH,
                new POEServerParser(this), this);

        this.orders   = new Object2ObjectOpenCustomHashMap<>(HASH_STRATEGY);
        this.orderIds = new ObjectOpenCustomHashSet<>(HASH_STRATEGY);

        this.books = books;

        this.terminated = false;
    }

    SoupBinTCPServer getTransport() {
        return transport;
    }

    long getUsername() {
        return username;
    }

    boolean isTerminated() {
        return terminated;
    }

    @Override
    public void close() {
        for (Order order : orders.values())
            books.cancel(order);

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

        username = ByteArrays.packLong(payload.username, SPACE);
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

        books.enterOrder(message, this);
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

        books.cancelOrder(message, order);
    }

    void track(Order order) {
        orders.put(order.getOrderId().clone(), order);
    }

    void release(Order order) {
        orders.remove(order.getOrderId());
    }

    void orderAccepted(POE.EnterOrder message, Order order) {
        orderAccepted.timestamp   = timestamp();
        System.arraycopy(message.orderId, 0, orderAccepted.orderId, 0, orderAccepted.orderId.length);
        orderAccepted.side        = message.side;
        orderAccepted.instrument  = message.instrument;
        orderAccepted.quantity    = message.quantity;
        orderAccepted.price       = message.price;
        orderAccepted.orderNumber = order.getOrderNumber();

        send(orderAccepted);

        orderIds.add(message.orderId.clone());
    }

    void orderRejected(POE.EnterOrder message, byte reason) {
        orderRejected.timestamp = timestamp();
        System.arraycopy(message.orderId, 0, orderRejected.orderId, 0, orderRejected.orderId.length);
        orderRejected.reason    = reason;

        send(orderRejected);
    }

    void orderExecuted(long price, long quantity, byte liquidityFlag,
            long matchNumber, Order order) {
        orderExecuted.timestamp     = timestamp();
        System.arraycopy(order.getOrderId(), 0, orderExecuted.orderId, 0, orderExecuted.orderId.length);
        orderExecuted.quantity      = quantity;
        orderExecuted.price         = price;
        orderExecuted.liquidityFlag = liquidityFlag;
        orderExecuted.matchNumber   = matchNumber;

        send(orderExecuted);
    }

    void orderCanceled(long canceledQuantity, byte reason, Order order) {
        orderCanceled.timestamp        = timestamp();
        System.arraycopy(order.getOrderId(), 0, orderCanceled.orderId, 0, orderCanceled.orderId.length);
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
        return (System.currentTimeMillis() - TradingSystem.EPOCH_MILLIS) * 1_000_000;
    }

}
