package com.paritytrading.parity.client.event;

import com.paritytrading.parity.net.poe.POE;

public interface Event {

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
            this.orderId     = message.orderId;
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
            this.orderId   = message.orderId;
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
            this.orderId       = message.orderId;
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
            this.orderId          = message.orderId;
            this.canceledQuantity = message.canceledQuantity;
            this.reason           = message.reason;
        }

        @Override
        public void accept(EventVisitor visitor) {
            visitor.visit(this);
        }
    }

    class BrokenTrade implements Event {
        public final long   timestamp;
        public final String orderId;
        public final long   matchNumber;
        public final byte   reason;

        public BrokenTrade(POE.BrokenTrade message) {
            this.timestamp   = message.timestamp;
            this.orderId     = message.orderId;
            this.matchNumber = message.matchNumber;
            this.reason      = message.reason;
        }

        @Override
        public void accept(EventVisitor visitor) {
            visitor.visit(this);
        }
    }

}
