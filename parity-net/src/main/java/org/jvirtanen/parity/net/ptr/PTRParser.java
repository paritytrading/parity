package org.jvirtanen.parity.net.ptr;

import static org.jvirtanen.parity.net.ptr.PTR.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jvirtanen.nassau.MessageListener;

/**
 * A parser for inbound messages.
 */
public class PTRParser implements MessageListener {

    private Trade trade;

    private PTRListener listener;

    public PTRParser(PTRListener listener) {
        this.trade = new Trade();

        this.listener = listener;
    }

    @Override
    public void message(ByteBuffer buffer) throws IOException {
        byte messageType = buffer.get();

        switch (messageType) {
        case MESSAGE_TYPE_TRADE:
            trade(buffer);
            break;
        default:
            throw new PTRException("Unknown message type: " + (char)messageType);
        }
    }

    private void trade(ByteBuffer buffer) throws IOException {
        trade.get(buffer);

        listener.trade(trade);
    }

}
