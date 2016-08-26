package com.paritytrading.parity.system;

import com.paritytrading.parity.match.OrderBook;

class Order {

    private String    orderId;
    private long      orderNumber;
    private Session   session;
    private OrderBook book;

    public Order(String orderId, long orderNumber, Session session, OrderBook book) {
        this.orderId     = orderId;
        this.orderNumber = orderNumber;
        this.session     = session;
        this.book        = book;
    }

    public String getOrderId() {
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
