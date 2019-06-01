package com.paritytrading.parity.net.pmr;

import static com.paritytrading.parity.net.pmr.PMR.*;

import com.paritytrading.nassau.MessageListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A parser for inbound messages.
 */
public class PMRParser implements MessageListener {

    private final Version       version;
    private final OrderEntered  orderEntered;
    private final OrderAdded    orderAdded;
    private final OrderCanceled orderCanceled;
    private final Trade         trade;

    private final PMRListener listener;

    /**
     * Create a parser for inbound messages.
     *
     * @param listener the message listener
     */
    public PMRParser(PMRListener listener) {
        this.version       = new Version();
        this.orderEntered  = new OrderEntered();
        this.orderAdded    = new OrderAdded();
        this.orderCanceled = new OrderCanceled();
        this.trade         = new Trade();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        int length = buffer.remaining();

        if (length < 1)
            throw new PMRException("Malformed message: no message type");

        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_VERSION:
            if (length < MESSAGE_LENGTH_VERSION)
                malformedMessage(messageType);

            version.get(buffer);
            listener.version(version);
            break;
        case MESSAGE_TYPE_ORDER_ENTERED:
            if (length < MESSAGE_LENGTH_ORDER_ENTERED)
                malformedMessage(messageType);

            orderEntered.get(buffer);
            listener.orderEntered(orderEntered);
            break;
        case MESSAGE_TYPE_ORDER_ADDED:
            if (length < MESSAGE_LENGTH_ORDER_ADDED)
                malformedMessage(messageType);

            orderAdded.get(buffer);
            listener.orderAdded(orderAdded);
            break;
        case MESSAGE_TYPE_ORDER_CANCELED:
            if (length < MESSAGE_LENGTH_ORDER_CANCELED)
                malformedMessage(messageType);

            orderCanceled.get(buffer);
            listener.orderCanceled(orderCanceled);
            break;
        case MESSAGE_TYPE_TRADE:
            if (length < MESSAGE_LENGTH_TRADE)
                malformedMessage(messageType);

            trade.get(buffer);
            listener.trade(trade);
            break;
        default:
            throw new PMRException("Unknown message type: " + (char)messageType);
        }
    }

    private void malformedMessage(byte messageType) throws IOException {
        throw new PMRException("Malformed message: " + (char)messageType);
    }

}
