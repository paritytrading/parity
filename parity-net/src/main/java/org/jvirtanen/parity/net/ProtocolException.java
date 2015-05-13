package org.jvirtanen.parity.net;

import java.io.IOException;

/**
 * Indicates a protocol error.
 */
public class ProtocolException extends IOException {

    /**
     * Construct an instance with the specified detail message.
     *
     * @param message the detail message
     */
    public ProtocolException(String message) {
        super(message);
    }

    /**
     * Construct an instance with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct an instance with the specified cause.
     *
     * @param cause the cause
     */
    public ProtocolException(Throwable cause) {
        super(cause);
    }

}
