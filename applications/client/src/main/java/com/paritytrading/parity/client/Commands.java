package com.paritytrading.parity.client;

import com.paritytrading.parity.net.poe.POE;
import java.util.stream.Stream;

class Commands {

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

    static Command[] all() {
        return COMMANDS;
    }

    static Command find(final String name) {
        return Stream.of(COMMANDS)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    static String[] names() {
        return Stream.of(COMMANDS).map(Command::getName).toArray(String[]::new);
    }

}
