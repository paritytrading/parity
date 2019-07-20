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
package com.paritytrading.parity.system;

import com.paritytrading.parity.match.OrderBook;

class Order {

    private final byte[]    orderId;
    private final long      orderNumber;
    private final Session   session;
    private final OrderBook book;

    Order(byte[] orderId, long orderNumber, Session session, OrderBook book) {
        this.orderId     = orderId.clone();
        this.orderNumber = orderNumber;
        this.session     = session;
        this.book        = book;
    }

    byte[] getOrderId() {
        return orderId;
    }

    long getOrderNumber() {
        return orderNumber;
    }

    Session getSession() {
        return session;
    }

    OrderBook getBook() {
        return book;
    }

}
