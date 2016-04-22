package com.paritytrading.parity.system;

import com.paritytrading.parity.match.Market;

class Order {

    private String  orderId;
    private long    orderNumber;
    private Session session;
    private Market  market;

    public Order(String orderId, long orderNumber, Session session, Market market) {
        this.orderId     = orderId;
        this.orderNumber = orderNumber;
        this.session     = session;
        this.market      = market;
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

    public Market getMarket() {
        return market;
    }

}
