package com.paritytrading.parity.net.pmd;

import static com.paritytrading.foundation.ByteBuffers.*;

import com.paritytrading.parity.net.ProtocolMessage;
import java.nio.ByteBuffer;

/**
 * Common definitions.
 */
public class PMD {

    private PMD() {
    }

    public static final long VERSION = 1;

    public static final byte BUY  = 'B';
    public static final byte SELL = 'S';

    static final byte MESSAGE_TYPE_VERSION        = 'V';
    static final byte MESSAGE_TYPE_ORDER_ADDED    = 'A';
    static final byte MESSAGE_TYPE_ORDER_EXECUTED = 'E';
    static final byte MESSAGE_TYPE_ORDER_CANCELED = 'X';
    static final byte MESSAGE_TYPE_ORDER_DELETED  = 'D';

    /**
     * A message.
     */
    public interface Message extends ProtocolMessage {
    }

    /**
     * A Version message.
     */
    public static class Version implements Message {
        public long version;

        @Override
        public void get(ByteBuffer buffer) {
            version = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_VERSION);
            putUnsignedInt(buffer, version);
        }
    }

    /**
     * An Order Added message.
     */
    public static class OrderAdded implements Message {
        public long timestamp;
        public long orderNumber;
        public byte side;
        public long instrument;
        public long quantity;
        public long price;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp   = buffer.getLong();
            orderNumber = buffer.getLong();
            side        = buffer.get();
            instrument  = buffer.getLong();
            quantity    = getUnsignedInt(buffer);
            price       = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_ADDED);
            buffer.putLong(timestamp);
            buffer.putLong(orderNumber);
            buffer.put(side);
            buffer.putLong(instrument);
            putUnsignedInt(buffer, quantity);
            putUnsignedInt(buffer, price);
        }
    }

    /**
     * An Order Executed message.
     */
    public static class OrderExecuted implements Message {
        public long timestamp;
        public long orderNumber;
        public long quantity;
        public long matchNumber;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp   = buffer.getLong();
            orderNumber = buffer.getLong();
            quantity    = getUnsignedInt(buffer);
            matchNumber = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_EXECUTED);
            buffer.putLong(timestamp);
            buffer.putLong(orderNumber);
            putUnsignedInt(buffer, quantity);
            putUnsignedInt(buffer, matchNumber);
        }
    }

    /**
     * An Order Canceled message.
     */
    public static class OrderCanceled implements Message {
        public long timestamp;
        public long orderNumber;
        public long canceledQuantity;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp        = buffer.getLong();
            orderNumber      = buffer.getLong();
            canceledQuantity = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_CANCELED);
            buffer.putLong(timestamp);
            buffer.putLong(orderNumber);
            putUnsignedInt(buffer, canceledQuantity);
        }
    }

    /**
     * An Order Deleted message.
     */
    public static class OrderDeleted implements Message {
        public long timestamp;
        public long orderNumber;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp   = buffer.getLong();
            orderNumber = buffer.getLong();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_DELETED);
            buffer.putLong(timestamp);
            buffer.putLong(orderNumber);
        }
    }

}
