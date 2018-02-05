package com.paritytrading.parity.net.pmr;

import static com.paritytrading.foundation.ByteBuffers.*;

import com.paritytrading.parity.net.ProtocolMessage;
import java.nio.ByteBuffer;

/**
 * Common definitions.
 */
public class PMR {

    private PMR() {
    }

    /**
     * The protocol version.
     */
    public static final long VERSION = 2;

    static final byte MESSAGE_TYPE_VERSION        = 'V';
    static final byte MESSAGE_TYPE_ORDER_ENTERED  = 'E';
    static final byte MESSAGE_TYPE_ORDER_ADDED    = 'A';
    static final byte MESSAGE_TYPE_ORDER_CANCELED = 'X';
    static final byte MESSAGE_TYPE_TRADE          = 'T';

    static final int MESSAGE_LENGTH_VERSION        =  5;
    static final int MESSAGE_LENGTH_ORDER_ENTERED  = 50;
    static final int MESSAGE_LENGTH_ORDER_ADDED    = 17;
    static final int MESSAGE_LENGTH_ORDER_CANCELED = 25;
    static final int MESSAGE_LENGTH_TRADE          = 37;

    public static final byte BUY  = 'B';
    public static final byte SELL = 'S';

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
     * An Order Entered message.
     */
    public static class OrderEntered implements Message {
        public long timestamp;
        public long username;
        public long orderNumber;
        public byte side;
        public long instrument;
        public long quantity;
        public long price;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp   = buffer.getLong();
            username    = buffer.getLong();
            orderNumber = buffer.getLong();
            side        = buffer.get();
            instrument  = buffer.getLong();
            quantity    = buffer.getLong();
            price       = buffer.getLong();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_ENTERED);
            buffer.putLong(timestamp);
            buffer.putLong(username);
            buffer.putLong(orderNumber);
            buffer.put(side);
            buffer.putLong(instrument);
            buffer.putLong(quantity);
            buffer.putLong(price);
        }
    }

    /**
     * An Order Added message.
     */
    public static class OrderAdded implements Message {
        public long timestamp;
        public long orderNumber;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp   = buffer.getLong();
            orderNumber = buffer.getLong();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_ADDED);
            buffer.putLong(timestamp);
            buffer.putLong(orderNumber);
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
            canceledQuantity = buffer.getLong();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_CANCELED);
            buffer.putLong(timestamp);
            buffer.putLong(orderNumber);
            buffer.putLong(canceledQuantity);
        }
    }

    /**
     * A Trade message.
     */
    public static class Trade implements Message {
        public long timestamp;
        public long restingOrderNumber;
        public long incomingOrderNumber;
        public long quantity;
        public long matchNumber;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp           = buffer.getLong();
            restingOrderNumber  = buffer.getLong();
            incomingOrderNumber = buffer.getLong();
            quantity            = buffer.getLong();
            matchNumber         = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_TRADE);
            buffer.putLong(timestamp);
            buffer.putLong(restingOrderNumber);
            buffer.putLong(incomingOrderNumber);
            buffer.putLong(quantity);
            putUnsignedInt(buffer, matchNumber);
        }
    }

}
