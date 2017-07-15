package com.paritytrading.parity.net.pmr;

import static com.paritytrading.parity.net.pmr.PMR.*;

import java.io.IOException;

/**
 * The interface for inbound messages.
 */
public interface PMRListener {

    /**
     * Receive a Version message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void version(Version message) throws IOException;

    /**
     * Receive an Order Entered message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderEntered(OrderEntered message) throws IOException;

    /**
     * Receive an Order Added message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderAdded(OrderAdded message) throws IOException;

    /**
     * Receive a Cancel message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void cancel(Cancel message) throws IOException;

    /**
     * Receive a Trade message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void trade(Trade message) throws IOException;

}
