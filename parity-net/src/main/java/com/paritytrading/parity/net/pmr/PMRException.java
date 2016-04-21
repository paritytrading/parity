package com.paritytrading.parity.net.pmr;

import com.paritytrading.parity.net.ProtocolException;

/**
 * Indicates a protocol error while handling the PMR protocol.
 */
public class PMRException extends ProtocolException {

    /**
     * Construct an instance with the specified detail message.
     *
     * @param message the detail message
     */
    public PMRException(String message) {
        super(message);
    }

}
