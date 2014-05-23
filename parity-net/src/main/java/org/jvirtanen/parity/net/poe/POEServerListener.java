package org.jvirtanen.parity.net.poe;

import org.jvirtanen.parity.net.poe.POE.*;

import java.io.IOException;

/**
 * The interface for inbound messages on the server side.
 */
public interface POEServerListener {

    /**
     * Receive an Enter Order message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void enterOrder(EnterOrder message) throws IOException;

    /**
     * Receive a Cancel Order message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void cancelOrder(CancelOrder message) throws IOException;

}
