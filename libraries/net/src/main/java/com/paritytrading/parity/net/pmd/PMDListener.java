package com.paritytrading.parity.net.pmd;

import static com.paritytrading.parity.net.pmd.PMD.*;

import java.io.IOException;

/**
 * The interface for inbound messages.
 */
public interface PMDListener {

    /**
     * Receive a Version message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void version(Version message) throws IOException;

    /**
     * Receive a Seconds message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void seconds(Seconds message) throws IOException;

    /**
     * Receive an Order Added message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderAdded(OrderAdded message) throws IOException;

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
     * Receive an Order Deleted message.
     *
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
    void orderDeleted(OrderDeleted message) throws IOException;

}
