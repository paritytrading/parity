package com.paritytrading.parity.client.command;

import com.paritytrading.parity.client.TerminalClient;
import java.io.IOException;
import java.util.Scanner;

public interface Command {

    void execute(TerminalClient client, Scanner arguments) throws CommandException, IOException;

    String getName();

    String getDescription();

    String getUsage();

}
