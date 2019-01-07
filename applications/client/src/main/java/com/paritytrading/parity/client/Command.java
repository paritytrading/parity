package com.paritytrading.parity.client;

import java.io.IOException;
import java.util.Scanner;

interface Command {

    void execute(TerminalClient client, Scanner arguments) throws IOException;

    String getName();

    String getDescription();

    String getUsage();

}
