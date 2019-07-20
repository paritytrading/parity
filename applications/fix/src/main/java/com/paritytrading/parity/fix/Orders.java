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
package com.paritytrading.parity.fix;

import java.util.ArrayList;
import java.util.List;

class Orders {

    private final List<Order> orders;

    Orders() {
        orders = new ArrayList<>();
    }

    void add(Order order) {
        orders.add(order);
    }

    Order findByClOrdID(String clOrdId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (clOrdId.equals(order.getClOrdID()))
                return order;
        }

        return null;
    }

    Order findByOrderEntryID(long orderEntryId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (orderEntryId == order.getOrderEntryID())
                return order;
        }

        return null;
    }

    void removeByOrderEntryID(long orderEntryId) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            if (orderEntryId == order.getOrderEntryID()) {
                orders.remove(i);
                break;
            }
        }
    }

}
