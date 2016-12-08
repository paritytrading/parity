package com.paritytrading.parity.fix;

import static com.paritytrading.philadelphia.fix44.FIX44Enumerations.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.net.poe.POE;

class Order {

    private byte[] orderEntryId;
    private long   orderId;
    private String nextClOrdId;
    private String clOrdId;
    private String origClOrdId;
    private char   ordStatus;
    private String account;
    private char   side;
    private String symbol;
    private long   orderQty;
    private long   cumQty;
    private double avgPx;
    private char   cxlRejResponseTo;

    public Order(String orderEntryId, String clOrdId, String account, char side,
            String symbol, long orderQty) {
        this.orderEntryId     = new byte[POE.ORDER_ID_LENGTH];
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

        ASCII.putLeft(this.orderEntryId, orderEntryId);
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

        origClOrdId = clOrdId;

        clOrdId = nextClOrdId;

        nextClOrdId = null;
    }

    public byte[] getOrderEntryID() {
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
