package com.paritytrading.parity.fix;

import static com.paritytrading.philadelphia.fix44.FIX44Enumerations.*;

import com.paritytrading.parity.net.poe.POE;

class Order {

    private String orderEntryId;
    private long   orderId;
    private String clOrdId;
    private String origClOrdId;
    private char   ordStatus;
    private String account;
    private char   side;
    private String symbol;
    private long   orderQty;
    private long   cumQty;
    private double avgPx;

    public Order(String orderEntryId, String clOrdId, String account, char side,
            String symbol, long orderQty) {
        this.orderEntryId = orderEntryId;
        this.orderId      = 0;
        this.clOrdId      = clOrdId;
        this.origClOrdId  = null;
        this.ordStatus    = OrdStatusValues.New;
        this.account      = null;
        this.side         = side;
        this.symbol       = symbol;
        this.orderQty     = orderQty;
        this.cumQty       = 0;
        this.avgPx        = 0.0;
    }

    public void orderAccepted(long orderNumber) {
        orderId = orderNumber;
    }

    public void orderExecuted(long quantity, double price) {
        avgPx = (cumQty * avgPx + quantity * price) / (cumQty + quantity);

        cumQty += quantity;

        ordStatus = getLeavesQty() == 0 ? OrdStatusValues.Filled : OrdStatusValues.PartiallyFilled;
    }

    public void orderCanceled(long canceledQuantity) {
        orderQty -= canceledQuantity;
    }

    public String getOrderEntryID() {
        return orderEntryId;
    }

    public long getOrderID() {
        return orderId;
    }

    public String getClOrdID() {
        return clOrdId;
    }

    public void setClOrdID(String clOrdId) {
        this.origClOrdId = this.clOrdId;

        this.clOrdId = clOrdId;
    }

    public String getOrigClOrdID() {
        return origClOrdId;
    }

    public char getOrdStatus() {
        return ordStatus;
    }

    public String getAccount() {
        return account;
    }

    public char getSide() {
        return side;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getOrderQty() {
        return orderQty;
    }

    public long getCumQty() {
        return cumQty;
    }

    public long getLeavesQty() {
        return orderQty - cumQty;
    }

    public double getAvgPx() {
        return avgPx;
    }

}
