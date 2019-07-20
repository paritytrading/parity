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
package com.paritytrading.parity.book;

/**
 * The interface for outbound events from a market.
 */
public interface MarketListener {

    /**
     * An event indicating that an order book has changed.
     *
     * @param book the order book
     * @param bbo true if the best bid and offer (BBO) has changed, otherwise false
     */
    void update(OrderBook book, boolean bbo);

    /**
     * An event indicating that a trade has taken place.
     *
     * @param book the order book
     * @param side the side of the incoming order
     * @param price the trade price
     * @param size the trade size
     */
    void trade(OrderBook book, Side side, long price, long size);

}
