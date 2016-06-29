package com.paritytrading.parity.ticker;

import static java.util.Arrays.*;
import static org.jvirtanen.util.Applications.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StockTicker {

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("listen", new ListenCommand());
    }

    public static void main(String[] args) {
        if (args.length < 1)
            usage();

        Command command = COMMANDS.get(args[0]);
        if (command == null)
            error("Unknown command: " + command);

        List<String> arguments = asList(args).subList(1, args.length);

        try {
            command.execute(arguments);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private static void usage() {
        System.err.printf("Usage: parity-ticker <command>\n\n");
        System.err.printf("Commands:\n");

        for (Command command : COMMANDS.values())
            System.err.printf("  %s  %s\n", command.getName(), command.getDescription());

        System.err.printf("\n");
        System.exit(2);
    }

}
