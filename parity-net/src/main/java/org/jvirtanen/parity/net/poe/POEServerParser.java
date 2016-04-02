package org.jvirtanen.parity.net.poe;

import static org.jvirtanen.parity.net.poe.POE.*;

import com.paritytrading.nassau.MessageListener;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A parser for inbound messages on the server side.
 */
public class POEServerParser implements MessageListener {

    private EnterOrder  enterOrder;
    private CancelOrder cancelOrder;

    private POEServerListener listener;

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
        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_ENTER_ORDER:
            enterOrder.get(buffer);
            listener.enterOrder(enterOrder);
            break;
        case MESSAGE_TYPE_CANCEL_ORDER:
            cancelOrder.get(buffer);
            listener.cancelOrder(cancelOrder);
            break;
        default:
            throw new POEException("Unknown message type: " + (char)messageType);
        }
    }

}
