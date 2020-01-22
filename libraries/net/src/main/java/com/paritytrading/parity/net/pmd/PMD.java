/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritytrading.parity.net.pmd;

import com.paritytrading.parity.net.ProtocolMessage;
import java.nio.ByteBuffer;

/**
 * Common definitions.
 */
public class PMD {

    private PMD() {
    }

    public static final long VERSION = 2;

    public static final byte BUY  = 'B';
    public static final byte SELL = 'S';

    static final byte MESSAGE_TYPE_VERSION        = 'V';
    static final byte MESSAGE_TYPE_ORDER_ADDED    = 'A';
    static final byte MESSAGE_TYPE_ORDER_EXECUTED = 'E';
    static final byte MESSAGE_TYPE_ORDER_CANCELED = 'X';

    static final int MESSAGE_LENGTH_VERSION        =  5;
    static final int MESSAGE_LENGTH_ORDER_ADDED    = 42;
    static final int MESSAGE_LENGTH_ORDER_EXECUTED = 29;
    static final int MESSAGE_LENGTH_ORDER_CANCELED = 25;

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
            quantity    = buffer.getLong();
            price       = buffer.getLong();
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_ADDED);
            buffer.putLong(timestamp);
            buffer.putLong(orderNumber);
            buffer.put(side);
            buffer.putLong(instrument);
            buffer.putLong(quantity);
            buffer.putLong(price);
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
            quantity    = buffer.getLong();
            matchNumber = getUnsignedInt(buffer);
        }

        @Override
        public void put(ByteBuffer buffer) {
            buffer.put(MESSAGE_TYPE_ORDER_EXECUTED);
            buffer.putLong(timestamp);
            buffer.putLong(orderNumber);
            buffer.putLong(quantity);
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

    private static long getUnsignedInt(ByteBuffer buffer) {
        return buffer.getInt() & 0xffffffffL;
    }

    private static void putUnsignedInt(ByteBuffer buffer, long value) {
        buffer.putInt((int)value);
    }

}
