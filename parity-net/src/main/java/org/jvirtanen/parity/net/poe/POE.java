package org.jvirtanen.parity.net.poe;

import static com.paritytrading.foundation.ByteBuffers.*;
import static org.jvirtanen.parity.net.poe.ByteBuffers.*;

import java.nio.ByteBuffer;
import org.jvirtanen.parity.net.ProtocolMessage;

/**
 * Common definitions.
 */
public class POE {

    private POE() {
    }

    public static final int MAX_INBOUND_MESSAGE_LENGTH  = 34;
    public static final int MAX_OUTBOUND_MESSAGE_LENGTH = 50;

    public static final byte BUY  = 'B';
    public static final byte SELL = 'S';

    public static final byte ORDER_REJECT_REASON_UNKNOWN_INSTRUMENT = 'I';

    public static final byte LIQUIDITY_FLAG_ADDED_LIQUIDITY   = 'A';
    public static final byte LIQUIDITY_FLAG_REMOVED_LIQUIDITY = 'R';

    public static final byte ORDER_CANCEL_REASON_REQUEST     = 'R';
    public static final byte ORDER_CANCEL_REASON_SUPERVISORY = 'S';

    public static final byte BROKEN_TRADE_REASON_CONSENT     = 'C';
    public static final byte BROKEN_TRADE_REASON_SUPERVISORY = 'S';

    private static final byte ORDER_ID_LENGTH = 16;

    /**
     * A message.
     */
    public interface Message extends ProtocolMessage {
    }

    /**
     * A message from a client application to the trading system.
     */
    public interface InboundMessage extends Message {
    }

    static final byte MESSAGE_TYPE_ENTER_ORDER  = 'E';
    static final byte MESSAGE_TYPE_CANCEL_ORDER = 'X';

    /**
     * An Enter Order message.
     */
    public static class EnterOrder implements InboundMessage {
        public String orderId;
        public byte   side;
        public long   instrument;
        public long   quantity;
        public long   price;

        @Override
        public void get(ByteBuffer buffer) {
            orderId    = getString(buffer, ORDER_ID_LENGTH);
            side       = buffer.get();
            instrument = buffer.getLong();
            quantity   = getUnsignedInt(buffer);
            price      = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ENTER_ORDER);
            putString(buffer, orderId, ORDER_ID_LENGTH);
            buffer.put(side);
            buffer.putLong(instrument);
            putUnsignedInt(buffer, quantity);
            putUnsignedInt(buffer, price);
        }
    }

    /**
     * A Cancel Order message.
     */
    public static class CancelOrder implements InboundMessage {
        public String orderId;
        public long   quantity;

        @Override
        public void get(ByteBuffer buffer) {
            orderId  = getString(buffer, ORDER_ID_LENGTH);
            quantity = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_CANCEL_ORDER);
            putString(buffer, orderId, ORDER_ID_LENGTH);
            putUnsignedInt(buffer, quantity);
        }
    }

    /**
     * A message from the trading system to a client application.
     */
    public interface OutboundMessage extends Message {
    }

    static final byte MESSAGE_TYPE_ORDER_ACCEPTED = 'A';
    static final byte MESSAGE_TYPE_ORDER_REJECTED = 'R';
    static final byte MESSAGE_TYPE_ORDER_EXECUTED = 'E';
    static final byte MESSAGE_TYPE_ORDER_CANCELED = 'X';
    static final byte MESSAGE_TYPE_BROKEN_TRADE   = 'B';

    /**
     * An Order Accepted message.
     */
    public static class OrderAccepted implements OutboundMessage {
        public long   timestamp;
        public String orderId;
        public byte   side;
        public long   instrument;
        public long   quantity;
        public long   price;
        public long   orderNumber;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp   = buffer.getLong();
            orderId     = getString(buffer, ORDER_ID_LENGTH);
            side        = buffer.get();
            instrument  = buffer.getLong();
            quantity    = getUnsignedInt(buffer);
            price       = getUnsignedInt(buffer);
            orderNumber = buffer.getLong();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_ACCEPTED);
            buffer.putLong(timestamp);
            putString(buffer, orderId, ORDER_ID_LENGTH);
            buffer.put(side);
            buffer.putLong(instrument);
            putUnsignedInt(buffer, quantity);
            putUnsignedInt(buffer, price);
            buffer.putLong(orderNumber);
        }
    }

    /**
     * An Order Rejected message.
     */
    public static class OrderRejected implements OutboundMessage {
        public long   timestamp;
        public String orderId;
        public byte   reason;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp = buffer.getLong();
            orderId   = getString(buffer, ORDER_ID_LENGTH);
            reason    = buffer.get();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_REJECTED);
            buffer.putLong(timestamp);
            putString(buffer, orderId, ORDER_ID_LENGTH);
            buffer.put(reason);
        }
    }

    /**
     * An Order Executed message.
     */
    public static class OrderExecuted implements OutboundMessage {
        public long   timestamp;
        public String orderId;
        public long   quantity;
        public long   price;
        public byte   liquidityFlag;
        public long   matchNumber;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp     = buffer.getLong();
            orderId       = getString(buffer, ORDER_ID_LENGTH);
            quantity      = getUnsignedInt(buffer);
            price         = getUnsignedInt(buffer);
            liquidityFlag = buffer.get();
            matchNumber   = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_EXECUTED);
            buffer.putLong(timestamp);
            putString(buffer, orderId, ORDER_ID_LENGTH);
            putUnsignedInt(buffer, quantity);
            putUnsignedInt(buffer, price);
            buffer.put(liquidityFlag);
            putUnsignedInt(buffer, matchNumber);
        }
    }

    /**
     * An Order Canceled message.
     */
    public static class OrderCanceled implements OutboundMessage {
        public long   timestamp;
        public String orderId;
        public long   canceledQuantity;
        public byte   reason;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp        = buffer.getLong();
            orderId          = getString(buffer, ORDER_ID_LENGTH);
            canceledQuantity = getUnsignedInt(buffer);
            reason           = buffer.get();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_CANCELED);
            buffer.putLong(timestamp);
            putString(buffer, orderId, ORDER_ID_LENGTH);
            putUnsignedInt(buffer, canceledQuantity);
            buffer.put(reason);
        }
    }

    /**
     * A Broken Trade message.
     */
    public static class BrokenTrade implements OutboundMessage {
        public long   timestamp;
        public String orderId;
        public long   matchNumber;
        public byte   reason;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp   = buffer.getLong();
            orderId     = getString(buffer, ORDER_ID_LENGTH);
            matchNumber = getUnsignedInt(buffer);
            reason      = buffer.get();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_BROKEN_TRADE);
            buffer.putLong(timestamp);
            putString(buffer, orderId, ORDER_ID_LENGTH);
            putUnsignedInt(buffer, matchNumber);
            buffer.put(reason);
        }
    }

}
