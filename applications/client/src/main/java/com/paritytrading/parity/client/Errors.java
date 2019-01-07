package com.paritytrading.parity.client;

import java.util.ArrayList;
import java.util.List;

class Errors extends DefaultEventVisitor {

    private List<Error> errors;

    private Errors() {
        errors = new ArrayList<>();
    }

    static List<Error> collect(Events events) {
        Errors visitor = new Errors();

        events.accept(visitor);

        return visitor.getEvents();
    }

    private List<Error> getEvents() {
        return errors;
    }

    @Override
    public void visit(Event.OrderRejected event) {
        errors.add(new Error(event));
    }

}
