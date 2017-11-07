package com.paritytrading.parity.engine;

import com.paritytrading.parity.match.OrderBook;

class Order {

    private byte[]    orderId;
    private long      orderNumber;
    private Session   session;
    private OrderBook book;

    public Order(byte[] orderId, long orderNumber, Session session, OrderBook book) {
        this.orderId     = orderId.clone();
        this.orderNumber = orderNumber;
        this.session     = session;
        this.book        = book;
    }

    public byte[] getOrderId() {
        return orderId;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public Session getSession() {
        return session;
    }

    public OrderBook getBook() {
        return book;
    }

}
