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
package com.paritytrading.parity.match;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.TreeSet;

/**
 * An order book.
 */
public class OrderBook {

    private final TreeSet<Order> bids;
    private final TreeSet<Order> asks;

    private final Long2ObjectOpenHashMap<Order> orders;

    private final OrderBookListener listener;

    private long nextOrderNumber;

    /**
     * Create an order book.
     *
     * @param listener a listener for outbound events from the order book
     */
    public OrderBook(OrderBookListener listener) {
        this.bids = new TreeSet<>(OrderBook::compareBids);
        this.asks = new TreeSet<>(OrderBook::compareAsks);

        this.orders = new Long2ObjectOpenHashMap<>();

        this.listener = listener;

        this.nextOrderNumber = 0;
    }

    /**
     * Enter an order to this order book.
     *
     * <p>The incoming order is first matched against resting orders in this
     * order book. This operation results in zero or more Match events.</p>
     *
     * <p>If the remaining quantity is not zero after the matching operation,
     * the remaining quantity is added to this order book and an Add event is
     * triggered.</p>
     *
     * <p>If the order identifier is known, do nothing.</p>
     *
     * @param orderId an order identifier
     * @param side the side
     * @param price the limit price
     * @param size the size
     */
    public void enter(long orderId, Side side, long price, long size) {
        if (orders.containsKey(orderId))
            return;

        if (side == Side.BUY)
            buy(orderId, price, size);
        else
            sell(orderId, price, size);
    }

    private void buy(long incomingId, long incomingPrice, long incomingQuantity) {
        while (!asks.isEmpty()) {
            Order resting = asks.first();

            long restingPrice = resting.getPrice();
            if (restingPrice > incomingPrice)
                break;

            long restingId = resting.getId();

            long restingQuantity = resting.getRemainingQuantity();

            if (restingQuantity > incomingQuantity) {
                resting.reduce(incomingQuantity);

                listener.match(restingId, incomingId, Side.BUY, restingPrice, incomingQuantity, resting.getRemainingQuantity());

                return;
            }

            asks.remove(resting);
            orders.remove(restingId);

            listener.match(restingId, incomingId, Side.BUY, restingPrice, restingQuantity, 0);

            incomingQuantity -= restingQuantity;
            if (incomingQuantity == 0)
                return;
        }

        add(incomingId, Side.BUY, incomingPrice, incomingQuantity, bids);
    }

    private void sell(long incomingId, long incomingPrice, long incomingQuantity) {
        while (!bids.isEmpty()) {
            Order resting = bids.first();

            long restingPrice = resting.getPrice();
            if (restingPrice < incomingPrice)
                break;

            long restingId = resting.getId();

            long restingQuantity = resting.getRemainingQuantity();
            if (restingQuantity > incomingQuantity) {
                resting.reduce(incomingQuantity);

                listener.match(restingId, incomingId, Side.SELL, restingPrice, incomingQuantity, resting.getRemainingQuantity());

                return;
            }

            bids.remove(resting);
            orders.remove(restingId);

            listener.match(restingId, incomingId, Side.SELL, restingPrice, restingQuantity, 0);

            incomingQuantity -= restingQuantity;
            if (incomingQuantity == 0)
                return;
        }

        add(incomingId, Side.SELL, incomingPrice, incomingQuantity, asks);
    }

    private void add(long orderId, Side side, long price, long size, TreeSet<Order> queue) {
        Order order = new Order(nextOrderNumber++, orderId, side, price, size);

        queue.add(order);
        orders.put(orderId, order);

        listener.add(orderId, side, price, size);
    }

    /**
     * Cancel a quantity of an order in this order book. The size refers
     * to the new order size. If the new order size is set to zero, the
     * order is deleted from this order book.
     *
     * <p>A Cancel event is triggered.</p>
     *
     * <p>If the order identifier is unknown, do nothing.</p>
     *
     * @param orderId the order identifier
     * @param size the new size
     */
    public void cancel(long orderId, long size) {
        Order order = orders.get(orderId);
        if (order == null)
            return;

        long remainingQuantity = order.getRemainingQuantity();

        if (size >= remainingQuantity)
            return;

        if (size > 0) {
            order.resize(size);
        } else {
            TreeSet<Order> queue = order.getSide() == Side.BUY ? bids : asks;

            queue.remove(order);
            orders.remove(orderId);
        }

        listener.cancel(orderId, remainingQuantity - size, size);
    }

    private static int compareBids(Order a, Order b) {
        int result = Long.compare(b.getPrice(), a.getPrice());
        if (result != 0)
            return result;

        return Long.compare(a.getNumber(), b.getNumber());
    }

    private static int compareAsks(Order a, Order b) {
        int result = Long.compare(a.getPrice(), b.getPrice());
        if (result != 0)
            return result;

        return Long.compare(a.getNumber(), b.getNumber());
    };

}
