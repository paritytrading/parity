package com.paritytrading.parity.system;

import com.paritytrading.parity.match.OrderBook;

class Order {

    private final byte[]    orderId;
    private final long      orderNumber;
    private final Session   session;
    private final OrderBook book;

    Order(byte[] orderId, long orderNumber, Session session, OrderBook book) {
        this.orderId     = orderId.clone();
        this.orderNumber = orderNumber;
        this.session     = session;
        this.book        = book;
    }

    byte[] getOrderId() {
        return orderId;
    }

    long getOrderNumber() {
        return orderNumber;
    }

    Session getSession() {
        return session;
    }

    OrderBook getBook() {
        return book;
    }

}
