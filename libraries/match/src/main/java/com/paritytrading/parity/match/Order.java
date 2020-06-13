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

class Order {

    private final long number;

    private final long id;

    private final Side side;

    private final long price;

    private long remainingQuantity;

    Order(long number, long id, Side side, long price, long size) {
        this.number = number;

        this.id = id;

        this.side = side;

        this.price = price;

        this.remainingQuantity = size;
    }

    long getNumber() {
        return number;
    }

    long getId() {
        return id;
    }

    Side getSide() {
        return side;
    }

    long getPrice() {
        return price;
    }

    long getRemainingQuantity() {
        return remainingQuantity;
    }

    void reduce(long quantity) {
        remainingQuantity -= quantity;
    }

    void resize(long size) {
        remainingQuantity = size;
    }

}
