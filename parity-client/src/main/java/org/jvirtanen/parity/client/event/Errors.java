package org.jvirtanen.parity.client.event;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.factory.Lists;

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
