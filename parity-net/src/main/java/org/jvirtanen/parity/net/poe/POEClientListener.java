package org.jvirtanen.parity.net.poe;

import static org.jvirtanen.parity.net.poe.POE.*;

import java.io.IOException;

/**
 * The interface for inbound messages on the client side.
 */
public interface POEClientListener {

    /**
     * Receive an Order Accepted message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderAccepted(OrderAccepted message) throws IOException;

    /**
     * Receive an Order Rejected message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderRejected(OrderRejected message) throws IOException;

    /**
     * Receive an Order Executed message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderExecuted(OrderExecuted message) throws IOException;

    /**
     * Receive an Order Canceled message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderCanceled(OrderCanceled message) throws IOException;

    /**
     * Receive a Broken Trade message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void brokenTrade(BrokenTrade message) throws IOException;

}
