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

/**
 * The interface for outbound events from an order book.
 */
public interface OrderBookListener {

    /**
     * Match an incoming order to a resting order in the order book. The match
     * occurs at the price of the order in the order book.
     *
     * @param restingOrderId the order identifier of the resting order
     * @param incomingOrderId the order identifier of the incoming order
     * @param incomingSide the side of the incoming order
     * @param price the execution price
     * @param executedQuantity the executed quantity
     * @param remainingQuantity the remaining quantity of the resting order
     */
    void match(long restingOrderId, long incomingOrderId, Side incomingSide,
            long price, long executedQuantity, long remainingQuantity);

    /**
     * Add an order to the order book.
     *
     * @param orderId the order identifier
     * @param side the side
     * @param price the limit price
     * @param size the size
     */
    void add(long orderId, Side side, long price, long size);

    /**
     * Cancel a quantity of an order.
     *
     * @param orderId the order identifier
     * @param canceledQuantity the canceled quantity
     * @param remainingQuantity the remaining quantity
     */
    void cancel(long orderId, long canceledQuantity, long remainingQuantity);

}
