package com.paritytrading.parity.reporter;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.net.pmr.PMR;
import com.paritytrading.parity.net.pmr.PMRListener;
import com.paritytrading.parity.util.Timestamps;
import java.util.HashMap;
import java.util.Map;

class TradeProcessor implements PMRListener {

    private Map<Long, Order> orders;

    private Trade trade;

    private TradeListener listener;

    TradeProcessor(TradeListener listener) {
        this.orders = new HashMap<>();

        this.trade = new Trade();

        this.listener = listener;
    }

    @Override
    public void version(PMR.Version message) {
        if (message.version != PMR.VERSION)
            error("Unsupported protocol version");
    }

    @Override
    public void orderEntered(PMR.OrderEntered message) {
        orders.put(message.orderNumber, new Order(message));
    }

    @Override
    public void orderAdded(PMR.OrderAdded message) {
    }

    @Override
    public void orderCanceled(PMR.OrderCanceled message) {
        Order order = orders.get(message.orderNumber);

        order.remainingQuantity -= message.canceledQuantity;

        if (order.remainingQuantity == 0)
            orders.remove(message.orderNumber);
    }

    @Override
    public void trade(PMR.Trade message) {
        Order resting  = orders.get(message.restingOrderNumber);
        Order incoming = orders.get(message.incomingOrderNumber);

        Order buy  = resting.side == PMR.BUY  ? resting : incoming;
        Order sell = resting.side == PMR.SELL ? resting : incoming;

        long buyOrderNumber = resting.side == PMR.BUY ?
                message.restingOrderNumber : message.incomingOrderNumber;

        long sellOrderNumber = resting.side == PMR.SELL ?
                message.restingOrderNumber : message.incomingOrderNumber;

        trade.timestamp       = Timestamps.format(message.timestamp / 1_000_000);
        trade.matchNumber     = message.matchNumber;
        trade.instrument      = ASCII.unpackLong(resting.instrument).trim();
        trade.quantity        = message.quantity;
        trade.price           = resting.price;
        trade.buyer           = ASCII.unpackLong(buy.username).trim();
        trade.buyOrderNumber  = buyOrderNumber;
        trade.seller          = ASCII.unpackLong(sell.username).trim();
        trade.sellOrderNumber = sellOrderNumber;

        listener.trade(trade);

        resting.remainingQuantity  -= message.quantity;
        incoming.remainingQuantity -= message.quantity;

        if (resting.remainingQuantity == 0)
            orders.remove(message.restingOrderNumber);

        if (incoming.remainingQuantity == 0)
            orders.remove(message.incomingOrderNumber);
    }

    private static class Order {
        long username;
        byte side;
        long instrument;
        long price;
        long remainingQuantity;

        Order(PMR.OrderEntered message) {
            this.username          = message.username;
            this.side              = message.side;
            this.instrument        = message.instrument;
            this.price             = message.price;
            this.remainingQuantity = message.quantity;
        }
    }

}
