package org.jvirtanen.parity.net.pmd;

import org.jvirtanen.parity.net.ProtocolException;

/**
 * Indicates a protocol error while handling the PMD protocol.
 */
public class PMDException extends ProtocolException {

    /**
     * Construct an instance with the specified detail message.
     *
     * @param message the detail message
     */
    public PMDException(String message) {
        super(message);
    }

}
