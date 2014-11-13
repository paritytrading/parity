package org.jvirtanen.parity.client.command;

import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.impl.factory.Lists;
import org.jvirtanen.parity.net.poe.POE;

public class Commands {

    private static final ImmutableList<Command> COMMANDS = Lists.immutable.of(
            new EnterCommand(POE.BUY),
            new EnterCommand(POE.SELL),
            new CancelCommand(),
            new OrdersCommand(),
            new TradesCommand(),
            new ErrorsCommand(),
            new HelpCommand(),
            new ExitCommand()
        );

    private Commands() {
    }

    public static ImmutableList<Command> all() {
        return COMMANDS;
    }

    public static Command find(final String name) {
        return COMMANDS.select(new Predicate<Command>() {

            @Override
            public boolean accept(Command command) {
                return command.getName().equals(name);
            }

        }).getFirst();
    }

}
