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
package com.paritytrading.parity.client;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.net.poe.POE;

interface Event {

    void accept(EventVisitor visitor);

    class OrderAccepted implements Event {
        public final long   timestamp;
        public final String orderId;
        public final byte   side;
        public final long   instrument;
        public final long   quantity;
        public final long   price;
        public final long   orderNumber;

        public OrderAccepted(POE.OrderAccepted message) {
            this.timestamp   = message.timestamp;
            this.orderId     = ASCII.get(message.orderId);
            this.side        = message.side;
            this.instrument  = message.instrument;
            this.quantity    = message.quantity;
            this.price       = message.price;
            this.orderNumber = message.orderNumber;
        }

        @Override
        public void accept(EventVisitor visitor) {
            visitor.visit(this);
        }
    }

    class OrderRejected implements Event {
        public final long   timestamp;
        public final String orderId;
        public final byte   reason;

        public OrderRejected(POE.OrderRejected message) {
            this.timestamp = message.timestamp;
            this.orderId   = ASCII.get(message.orderId);
            this.reason    = message.reason;
        }

        @Override
        public void accept(EventVisitor visitor) {
            visitor.visit(this);
        }
    }

    class OrderExecuted implements Event {
        public final long   timestamp;
        public final String orderId;
        public final long   quantity;
        public final long   price;
        public final byte   liquidityFlag;
        public final long   matchNumber;

        public OrderExecuted(POE.OrderExecuted message) {
            this.timestamp     = message.timestamp;
            this.orderId       = ASCII.get(message.orderId);
            this.quantity      = message.quantity;
            this.price         = message.price;
            this.liquidityFlag = message.liquidityFlag;
            this.matchNumber   = message.matchNumber;
        }

        @Override
        public void accept(EventVisitor visitor) {
            visitor.visit(this);
        }
    }

    class OrderCanceled implements Event {
        public final long   timestamp;
        public final String orderId;
        public final long   canceledQuantity;
        public final byte   reason;

        public OrderCanceled(POE.OrderCanceled message) {
            this.timestamp        = message.timestamp;
            this.orderId          = ASCII.get(message.orderId);
            this.canceledQuantity = message.canceledQuantity;
            this.reason           = message.reason;
        }

        @Override
        public void accept(EventVisitor visitor) {
            visitor.visit(this);
        }
    }

}
