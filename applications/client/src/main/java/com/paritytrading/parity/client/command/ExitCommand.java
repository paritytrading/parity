package com.paritytrading.parity.client.command;

import com.paritytrading.parity.client.TerminalClient;
import java.util.Scanner;

class ExitCommand implements Command {

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws CommandException {
        if (arguments.hasNext())
            throw new CommandException();

        client.close();
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Exit the client";
    }

    @Override
    public String getUsage() {
        return "exit";
    }

}
