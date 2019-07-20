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

class DefaultEventVisitor implements EventVisitor {

    @Override
    public void visit(Event.OrderAccepted event) {
    }

    @Override
    public void visit(Event.OrderRejected event) {
    }

    @Override
    public void visit(Event.OrderExecuted event) {
    }

    @Override
    public void visit(Event.OrderCanceled event) {
    }

}
