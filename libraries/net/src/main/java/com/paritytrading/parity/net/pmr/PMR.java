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
    public static final long VERSION = 1;

    static final byte MESSAGE_TYPE_VERSION = 'V';
    static final byte MESSAGE_TYPE_ORDER   = 'O';
    static final byte MESSAGE_TYPE_CANCEL  = 'X';
    static final byte MESSAGE_TYPE_TRADE   = 'T';

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
     * An Order message.
     */
    public static class Order implements Message {
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
            quantity    = getUnsignedInt(buffer);
            price       = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER);
            buffer.putLong(timestamp);
            buffer.putLong(username);
            buffer.putLong(orderNumber);
            buffer.put(side);
            buffer.putLong(instrument);
            putUnsignedInt(buffer, quantity);
            putUnsignedInt(buffer, price);
        }
    }

    /**
     * A Cancel message.
     */
    public static class Cancel implements Message {
        public long timestamp;
        public long username;
        public long orderNumber;
        public long canceledQuantity;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp        = buffer.getLong();
            username         = buffer.getLong();
            orderNumber      = buffer.getLong();
            canceledQuantity = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_CANCEL);
            buffer.putLong(timestamp);
            buffer.putLong(username);
            buffer.putLong(orderNumber);
            putUnsignedInt(buffer, canceledQuantity);
        }
    }

    /**
     * A Trade message.
     */
    public static class Trade implements Message {
        public long timestamp;
        public long matchNumber;
        public long instrument;
        public long quantity;
        public long price;
        public long buyer;
        public long buyOrderNumber;
        public long seller;
        public long sellOrderNumber;

        @Override
        public void get(ByteBuffer buffer) {
            timestamp       = buffer.getLong();
            matchNumber     = getUnsignedInt(buffer);
            instrument      = buffer.getLong();
            quantity        = getUnsignedInt(buffer);
            price           = getUnsignedInt(buffer);
            buyer           = buffer.getLong();
            buyOrderNumber  = buffer.getLong();
            seller          = buffer.getLong();
            sellOrderNumber = buffer.getLong();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_TRADE);
            buffer.putLong(timestamp);
            putUnsignedInt(buffer, matchNumber);
            buffer.putLong(instrument);
            putUnsignedInt(buffer, quantity);
            putUnsignedInt(buffer, price);
            buffer.putLong(buyer);
            buffer.putLong(buyOrderNumber);
            buffer.putLong(seller);
            buffer.putLong(sellOrderNumber);
        }
    }

}
