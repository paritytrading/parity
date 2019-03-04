package com.paritytrading.parity.ticker;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.parity.book.Market;
import com.paritytrading.parity.book.Side;
import com.paritytrading.parity.net.pmd.PMD;
import com.paritytrading.parity.net.pmd.PMDListener;

class MarketDataProcessor implements PMDListener {

    private Market market;

    private MarketDataListener listener;

    MarketDataProcessor(Market market, MarketDataListener listener) {
        this.market   = market;
        this.listener = listener;
    }

    @Override
    public void version(PMD.Version message) {
        if (message.version != PMD.VERSION)
            error("Unsupported protocol version");
    }

    @Override
    public void orderAdded(PMD.OrderAdded message) {
        listener.timestamp(message.timestamp);

        market.add(message.instrument, message.orderNumber, side(message.side), message.price, message.quantity);
    }

    @Override
    public void orderExecuted(PMD.OrderExecuted message) {
        listener.timestamp(message.timestamp);

        market.execute(message.orderNumber, message.quantity);
    }

    @Override
    public void orderCanceled(PMD.OrderCanceled message) {
        listener.timestamp(message.timestamp);

        market.cancel(message.orderNumber, message.canceledQuantity);
    }

    private Side side(byte side) {
        return side == PMD.BUY ? Side.BUY : Side.SELL;
    }

}
