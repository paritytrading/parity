package org.jvirtanen.parity.net.ptr;

import static org.jvirtanen.parity.net.ptr.PTR.*;

import java.io.IOException;

/**
 * The interface for inbound messages.
 */
public interface PTRListener {

    /**
     * Receive a Trade message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void trade(Trade message) throws IOException;

}
