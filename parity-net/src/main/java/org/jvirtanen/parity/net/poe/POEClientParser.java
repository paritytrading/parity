package org.jvirtanen.parity.net.poe;

import static org.jvirtanen.parity.net.poe.POE.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jvirtanen.nassau.MessageListener;

/**
 * A parser for inbound messages on the client side.
 */
public class POEClientParser implements MessageListener {

    private OrderAccepted orderAccepted;
    private OrderRejected orderRejected;
    private OrderExecuted orderExecuted;
    private OrderCanceled orderCanceled;
    private BrokenTrade   brokenTrade;

    private POEClientListener listener;

    public POEClientParser(POEClientListener listener) {
        this.orderAccepted = new OrderAccepted();
        this.orderRejected = new OrderRejected();
        this.orderExecuted = new OrderExecuted();
        this.orderCanceled = new OrderCanceled();
        this.brokenTrade   = new BrokenTrade();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_ORDER_ACCEPTED:
            orderAccepted(buffer);
            break;
        case MESSAGE_TYPE_ORDER_REJECTED:
            orderRejected(buffer);
            break;
        case MESSAGE_TYPE_ORDER_EXECUTED:
            orderExecuted(buffer);
            break;
        case MESSAGE_TYPE_ORDER_CANCELED:
            orderCanceled(buffer);
            break;
        case MESSAGE_TYPE_BROKEN_TRADE:
            brokenTrade(buffer);
            break;
        default:
            throw new POEException("Unknown message type: " + (char)messageType);
        }
    }

    private void orderAccepted(ByteBuffer buffer) throws IOException {
        orderAccepted.get(buffer);

        listener.orderAccepted(orderAccepted);
    }

    private void orderRejected(ByteBuffer buffer) throws IOException {
        orderRejected.get(buffer);

        listener.orderRejected(orderRejected);
    }

    private void orderExecuted(ByteBuffer buffer) throws IOException {
        orderExecuted.get(buffer);

        listener.orderExecuted(orderExecuted);
    }

    private void orderCanceled(ByteBuffer buffer) throws IOException {
        orderCanceled.get(buffer);

        listener.orderCanceled(orderCanceled);
    }

    private void brokenTrade(ByteBuffer buffer) throws IOException {
        brokenTrade.get(buffer);

        listener.brokenTrade(brokenTrade);
    }

}
