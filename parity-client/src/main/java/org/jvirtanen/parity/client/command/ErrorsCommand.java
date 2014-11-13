package org.jvirtanen.parity.client.command;

import java.util.Scanner;
import org.jvirtanen.parity.client.TerminalClient;
import org.jvirtanen.parity.client.event.Error;
import org.jvirtanen.parity.client.event.Errors;

class ErrorsCommand implements Command {

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws CommandException {
        if (arguments.hasNext())
            throw new CommandException();

        client.printf("\n%s\n", Error.HEADER);
        for (Error error : Errors.collect(client.getEvents()))
            client.printf("%s\n", error.format());
        client.printf("\n");
    }

    @Override
    public String getName() {
        return "errors";
    }

    @Override
    public String getDescription() {
        return "Display occurred errors";
    }

    @Override
    public String getUsage() {
        return "errors";
    }

}
