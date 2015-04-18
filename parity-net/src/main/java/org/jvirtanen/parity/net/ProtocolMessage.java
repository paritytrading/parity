package org.jvirtanen.parity.net;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * A protocol message.
 */
public interface ProtocolMessage {

    /**
     * Read this message from a buffer.
     *
     * @param buffer a buffer
     * @throws BufferUnderflowException if there are fewer bytes remaining
     *   in the buffer than what this message consists of
     */
    void get(ByteBuffer buffer);

    /**
     * Write this message to a buffer.
     *
     * @param buffer a buffer
     * @throws BufferOverflowException if there are fewer bytes remaining
     *   in the buffer than what this message consists of
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    void put(ByteBuffer buffer);

}
