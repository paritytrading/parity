package com.paritytrading.parity.fix;

import static com.paritytrading.philadelphia.fix44.FIX44Enumerations.*;

class Order {

    private final long   orderEntryId;
    private       long   orderId;
    private       String nextClOrdId;
    private       String clOrdId;
    private       String origClOrdId;
    private       char   ordStatus;
    private final String account;
    private final char   side;
    private final String symbol;
    private       double orderQty;
    private       double cumQty;
    private       double avgPx;
    private       char   cxlRejResponseTo;

    Order(long orderEntryId, String clOrdId, String account, char side,
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

    void orderAccepted(long orderNumber) {
        orderId = orderNumber;
    }

    void orderExecuted(double quantity, double price) {
        avgPx = (cumQty * avgPx + quantity * price) / (cumQty + quantity);

        cumQty += quantity;

        ordStatus = getLeavesQty() == 0 ? OrdStatusValues.Filled : OrdStatusValues.PartiallyFilled;
    }

    void orderCanceled(double canceledQuantity) {
        orderQty -= canceledQuantity;

        origClOrdId = clOrdId;

        clOrdId = nextClOrdId;

        nextClOrdId = null;
    }

    long getOrderEntryID() {
        return orderEntryId;
    }

    long getOrderID() {
        return orderId;
    }

    String getClOrdID() {
        return clOrdId;
    }

    String getOrigClOrdID() {
        return origClOrdId;
    }

    void setNextClOrdID(String nextClOrdId) {
        this.nextClOrdId = nextClOrdId;
    }

    String getNextClOrdID() {
        return nextClOrdId;
    }

    char getOrdStatus() {
        return ordStatus;
    }

    String getAccount() {
        return account;
    }

    char getSide() {
        return side;
    }

    String getSymbol() {
        return symbol;
    }

    double getOrderQty() {
        return orderQty;
    }

    double getCumQty() {
        return cumQty;
    }

    double getLeavesQty() {
        return orderQty - cumQty;
    }

    double getAvgPx() {
        return avgPx;
    }

    void setCxlRejResponseTo(char cxlRejResponseTo) {
        this.cxlRejResponseTo = cxlRejResponseTo;
    }

    char getCxlRejResponseTo() {
        return cxlRejResponseTo;
    }

    boolean isInPendingStatus() {
        return nextClOrdId != null;
    }

}
