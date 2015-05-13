package org.jvirtanen.parity.net.poe;

import org.jvirtanen.parity.net.ProtocolException;

/**
 * Indicates a protocol error while handling the POE protocol.
 */
public class POEException extends ProtocolException {

    /**
     * Construct an instance with the specified detail message.
     *
     * @param message the detail message
     */
    public POEException(String message) {
        super(message);
    }

}
