package org.jvirtanen.parity.net.pmr;

import static org.jvirtanen.parity.net.pmr.PMR.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jvirtanen.nassau.MessageListener;

/**
 * A parser for inbound messages.
 */
public class PMRParser implements MessageListener {

    private Order order;
    private Trade trade;

    private PMRListener listener;

    public PMRParser(PMRListener listener) {
        this.order = new Order();
        this.trade = new Trade();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_ORDER:
            order(buffer);
            break;
        case MESSAGE_TYPE_TRADE:
            trade(buffer);
            break;
        default:
            throw new PMRException("Unknown message type: " + (char)messageType);
        }
    }

    private void order(ByteBuffer buffer) throws IOException {
        order.get(buffer);

        listener.order(order);
    }

    private void trade(ByteBuffer buffer) throws IOException {
        trade.get(buffer);

        listener.trade(trade);
    }

}
