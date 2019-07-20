/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritytrading.parity.ticker;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.parity.book.Market;
import com.paritytrading.parity.book.Side;
import com.paritytrading.parity.net.pmd.PMD;
import com.paritytrading.parity.net.pmd.PMDListener;

class MarketDataProcessor implements PMDListener {

    private final Market market;

    private final MarketDataListener listener;

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
