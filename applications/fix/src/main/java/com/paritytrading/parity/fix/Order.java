package com.paritytrading.parity.fix;

import static com.paritytrading.philadelphia.fix44.FIX44Enumerations.*;

class Order {

    private long   orderEntryId;
    private long   orderId;
    private String nextClOrdId;
    private String clOrdId;
    private String origClOrdId;
    private char   ordStatus;
    private String account;
    private char   side;
    private String symbol;
    private double orderQty;
    private double cumQty;
    private double avgPx;
    private char   cxlRejResponseTo;

    public Order(long orderEntryId, String clOrdId, String account, char side,
            String symbol, double orderQty) {
        this.orderEntryId     = orderEntryId;
        this.orderId          = 0;
        this.nextClOrdId      = null;
        this.clOrdId          = clOrdId;
        this.origClOrdId      = null;
        this.ordStatus        = OrdStatusValues.New;
        this.account          = null;
        this.side             = side;
        this.symbol           = symbol;
        this.orderQty         = orderQty;
        this.cumQty           = 0;
        this.avgPx            = 0.0;
        this.cxlRejResponseTo = CxlRejResponseToValues.OrderCancelRequest;
    }

    public void orderAccepted(long orderNumber) {
        orderId = orderNumber;
    }

    public void orderExecuted(double quantity, double price) {
        avgPx = (cumQty * avgPx + quantity * price) / (cumQty + quantity);

        cumQty += quantity;

        ordStatus = getLeavesQty() == 0 ? OrdStatusValues.Filled : OrdStatusValues.PartiallyFilled;
    }

    public void orderCanceled(double canceledQuantity) {
        orderQty -= canceledQuantity;

        origClOrdId = clOrdId;

        clOrdId = nextClOrdId;

        nextClOrdId = null;
    }

    public long getOrderEntryID() {
        return orderEntryId;
    }

    public long getOrderID() {
        return orderId;
    }

    public String getClOrdID() {
        return clOrdId;
    }

    public String getOrigClOrdID() {
        return origClOrdId;
    }

    public void setNextClOrdID(String nextClOrdId) {
        this.nextClOrdId = nextClOrdId;
    }

    public String getNextClOrdID() {
        return nextClOrdId;
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

    public double getOrderQty() {
        return orderQty;
    }

    public double getCumQty() {
        return cumQty;
    }

    public double getLeavesQty() {
        return orderQty - cumQty;
    }

    public double getAvgPx() {
        return avgPx;
    }

    public void setCxlRejResponseTo(char cxlRejResponseTo) {
        this.cxlRejResponseTo = cxlRejResponseTo;
    }

    public char getCxlRejResponseTo() {
        return cxlRejResponseTo;
    }

    public boolean isInPendingStatus() {
        return nextClOrdId != null;
    }

}
