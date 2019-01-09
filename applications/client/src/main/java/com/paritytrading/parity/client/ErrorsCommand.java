package com.paritytrading.parity.client;

import static com.paritytrading.parity.client.TerminalClient.*;

import java.util.Scanner;

class ErrorsCommand implements Command {

    @Override
    public void execute(TerminalClient client, Scanner arguments) {
        if (arguments.hasNext())
            throw new IllegalArgumentException();

        printf("\n%s\n", Error.HEADER);
        for (Error error : Errors.collect(client.getEvents()))
            printf("%s\n", error.format());
        printf("\n");
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
