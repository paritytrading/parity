package org.jvirtanen.parity.net.pmr;

import static org.jvirtanen.parity.net.pmr.PMR.*;

import java.io.IOException;

/**
 * The interface for inbound messages.
 */
public interface PMRListener {

    /**
     * Receive a Trade message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void trade(Trade message) throws IOException;

}
