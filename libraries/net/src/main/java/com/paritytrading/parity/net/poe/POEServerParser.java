package com.paritytrading.parity.net.poe;

import static com.paritytrading.parity.net.poe.POE.*;

import com.paritytrading.nassau.MessageListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A parser for inbound messages on the server side.
 */
public class POEServerParser implements MessageListener {

    private final EnterOrder  enterOrder;
    private final CancelOrder cancelOrder;

    private final POEServerListener listener;

    /**
     * Create a parser for inbound messages on the server side.
     *
     * @param listener the message listener
     */
    public POEServerParser(POEServerListener listener) {
        this.enterOrder  = new EnterOrder();
        this.cancelOrder = new CancelOrder();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        int length = buffer.remaining();

        if (length < 1)
            throw new POEException("Malformed message: no message type");

        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_ENTER_ORDER:
            if (length < MESSAGE_LENGTH_ENTER_ORDER)
                malformedMessage(messageType);

            enterOrder.get(buffer);
            listener.enterOrder(enterOrder);
            break;
        case MESSAGE_TYPE_CANCEL_ORDER:
            if (length < MESSAGE_LENGTH_CANCEL_ORDER)
                malformedMessage(messageType);

            cancelOrder.get(buffer);
            listener.cancelOrder(cancelOrder);
            break;
        default:
            throw new POEException("Unknown message type: " + (char)messageType);
        }
    }

    private void malformedMessage(byte messageType) throws IOException {
        throw new POEException("Malformed message: " + (char)messageType);
    }

}
