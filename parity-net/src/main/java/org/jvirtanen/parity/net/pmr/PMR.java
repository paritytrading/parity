package org.jvirtanen.parity.net.pmr;

import static org.jvirtanen.nio.ByteBuffers.*;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * Common definitions.
 */
public class PMR {

    private PMR() {
    }

    static final byte MESSAGE_TYPE_ORDER = 'O';
    static final byte MESSAGE_TYPE_TRADE = 'T';

    public static final byte BUY  = 'B';
    public static final byte SELL = 'S';

    /**
     * A message.
     */
    public interface Message {

        /**
         * Read this message from the buffer.
         *
         * @param buffer a buffer
         * @throws BufferUnderflowException if there are fewer bytes remaining
         *   in the buffer than what this message consists of
         */
        void get(ByteBuffer buffer);

        /**
         * Write this message to the buffer.
         *
         * @param buffer a buffer
         * @throws BufferOverflowException if there are fewer bytes remaining
         *   in the buffer than what this message consists of
         * @throws ReadOnlyBufferException if the buffer is read-only
         */
        void put(ByteBuffer buffer);

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
            quantity    = buffer.getLong();
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
            buffer.putLong(quantity);
            putUnsignedInt(buffer, price);
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