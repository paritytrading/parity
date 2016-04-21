package com.paritytrading.parity.client.event;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public class Errors extends DefaultEventVisitor {

    private MutableList<Error> errors;

    private Errors() {
        errors = Lists.mutable.with();
    }

    public static ImmutableList<Error> collect(Events events) {
        Errors visitor = new Errors();

        events.accept(visitor);

        return visitor.getEvents();
    }

    private ImmutableList<Error> getEvents() {
        return errors.toImmutable();
    }

    @Override
    public void visit(Event.OrderRejected event) {
        errors.add(new Error(event));
    }

}
