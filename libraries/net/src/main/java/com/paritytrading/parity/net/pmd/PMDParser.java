package com.paritytrading.parity.net.pmd;

import static com.paritytrading.parity.net.pmd.PMD.*;

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

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_VERSION:
            version.get(buffer);
            listener.version(version);
            break;
        case MESSAGE_TYPE_SECONDS:
            seconds.get(buffer);
            listener.seconds(seconds);
            break;
        case MESSAGE_TYPE_ORDER_ADDED:
            orderAdded.get(buffer);
            listener.orderAdded(orderAdded);
            break;
        case MESSAGE_TYPE_ORDER_EXECUTED:
            orderExecuted.get(buffer);
            listener.orderExecuted(orderExecuted);
            break;
        case MESSAGE_TYPE_ORDER_CANCELED:
            orderCanceled.get(buffer);
            listener.orderCanceled(orderCanceled);
            break;
        case MESSAGE_TYPE_ORDER_DELETED:
            orderDeleted.get(buffer);
            listener.orderDeleted(orderDeleted);
            break;
        default:
            throw new PMDException("Unknown message type: " + (char)messageType);
        }
    }

}
