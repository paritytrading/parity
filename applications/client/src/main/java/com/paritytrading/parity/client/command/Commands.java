package com.paritytrading.parity.client.command;

import com.paritytrading.parity.net.poe.POE;
import java.util.stream.Stream;

public class Commands {

    private static final Command[] COMMANDS = new Command[] {
        new EnterCommand(POE.BUY),
        new EnterCommand(POE.SELL),
        new CancelCommand(),
        new OrdersCommand(),
        new TradesCommand(),
        new ErrorsCommand(),
        new HelpCommand(),
        new ExitCommand(),
    };

    private Commands() {
    }

    public static Command[] all() {
        return COMMANDS;
    }

    public static Command find(final String name) {
        return Stream.of(COMMANDS)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static String[] names() {
        return Stream.of(COMMANDS).map(Command::getName).toArray(String[]::new);
    }

}
