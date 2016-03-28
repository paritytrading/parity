package org.jvirtanen.parity.net.pmd;

import static org.jvirtanen.parity.net.pmd.PMD.*;

import com.paritytrading.nassau.MessageListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A parser for inbound messages.
 */
public class PMDParser implements MessageListener {

    private Version       version;
    private Seconds       seconds;
    private OrderAdded    orderAdded;
    private OrderExecuted orderExecuted;
    private OrderCanceled orderCanceled;
    private OrderDeleted  orderDeleted;
    private BrokenTrade   brokenTrade;

    private PMDListener listener;

    /**
     * Create a parser for inbound messages.
     *
     * @param listener the message listener
     */
    public PMDParser(PMDListener listener) {
        this.version       = new Version();
        this.seconds       = new Seconds();
        this.orderAdded    = new OrderAdded();
        this.orderExecuted = new OrderExecuted();
        this.orderCanceled = new OrderCanceled();
        this.orderDeleted  = new OrderDeleted();
        this.brokenTrade   = new BrokenTrade();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_VERSION:
            version(buffer);
            break;
        case MESSAGE_TYPE_SECONDS:
            seconds(buffer);
            break;
        case MESSAGE_TYPE_ORDER_ADDED:
            orderAdded(buffer);
            break;
        case MESSAGE_TYPE_ORDER_EXECUTED:
            orderExecuted(buffer);
            break;
        case MESSAGE_TYPE_ORDER_CANCELED:
            orderCanceled(buffer);
            break;
        case MESSAGE_TYPE_ORDER_DELETED:
            orderDeleted(buffer);
            break;
        case MESSAGE_TYPE_BROKEN_TRADE:
            brokenTrade(buffer);
            break;
        default:
            throw new PMDException("Unknown message type: " + (char)messageType);
        }
    }

    private void version(ByteBuffer buffer) throws IOException {
        version.get(buffer);

        listener.version(version);
    }

    private void seconds(ByteBuffer buffer) throws IOException {
        seconds.get(buffer);

        listener.seconds(seconds);
    }

    private void orderAdded(ByteBuffer buffer) throws IOException {
        orderAdded.get(buffer);

        listener.orderAdded(orderAdded);
    }

    private void orderExecuted(ByteBuffer buffer) throws IOException {
        orderExecuted.get(buffer);

        listener.orderExecuted(orderExecuted);
    }

    private void orderCanceled(ByteBuffer buffer) throws IOException {
        orderCanceled.get(buffer);

        listener.orderCanceled(orderCanceled);
    }

    private void orderDeleted(ByteBuffer buffer) throws IOException {
        orderDeleted.get(buffer);

        listener.orderDeleted(orderDeleted);
    }

    private void brokenTrade(ByteBuffer buffer) throws IOException {
        brokenTrade.get(buffer);

        listener.brokenTrade(brokenTrade);
    }

}
