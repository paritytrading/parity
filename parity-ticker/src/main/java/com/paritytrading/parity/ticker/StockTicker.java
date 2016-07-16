package com.paritytrading.parity.ticker;

import static java.util.Arrays.*;
import static java.util.Comparator.*;
import static org.jvirtanen.util.Applications.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StockTicker {

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("listen", new ListenCommand());
        COMMANDS.put("read",   new ReadCommand());
    }

    public static void main(String[] args) {
        if (args.length < 1)
            usage();

        Command command = COMMANDS.get(args[0]);
        if (command == null)
            error("Unknown command: " + args[0]);

        List<String> arguments = asList(args).subList(1, args.length);

        try {
            command.execute(arguments);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private static void usage() {
        List<Command> commands = new ArrayList<>(COMMANDS.values());

        commands.sort(comparing(c -> c.getName()));

        int maxCommandNameLength = commands.stream().mapToInt(c -> c.getName().length()).max().orElse(0);

        System.err.printf("Usage: parity-ticker <command>\n\n");
        System.err.printf("Commands:\n");

        for (Command command : commands)
            System.err.printf("  %-" + maxCommandNameLength + "s  %s\n", command.getName(), command.getDescription());

        System.err.printf("\n");
        System.exit(2);
    }

}
