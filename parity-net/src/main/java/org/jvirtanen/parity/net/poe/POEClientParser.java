package org.jvirtanen.parity.net.poe;

import static org.jvirtanen.parity.net.poe.POE.*;

import com.paritytrading.nassau.MessageListener;
import java.io.IOException;
import java.nio.ByteBuffer;

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

    /**
     * Create a parser for inbound messages on the client side.
     *
     * @param listener the message listener
     */
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
            orderAccepted.get(buffer);
            listener.orderAccepted(orderAccepted);
            break;
        case MESSAGE_TYPE_ORDER_REJECTED:
            orderRejected.get(buffer);
            listener.orderRejected(orderRejected);
            break;
        case MESSAGE_TYPE_ORDER_EXECUTED:
            orderExecuted.get(buffer);
            listener.orderExecuted(orderExecuted);
            break;
        case MESSAGE_TYPE_ORDER_CANCELED:
            orderCanceled.get(buffer);
            listener.orderCanceled(orderCanceled);
            break;
        case MESSAGE_TYPE_BROKEN_TRADE:
            brokenTrade.get(buffer);
            listener.brokenTrade(brokenTrade);
            break;
        default:
            throw new POEException("Unknown message type: " + (char)messageType);
        }
    }

}
