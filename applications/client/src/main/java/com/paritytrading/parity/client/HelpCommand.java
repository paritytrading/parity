/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritytrading.parity.client;

import static com.paritytrading.parity.client.TerminalClient.*;

import java.util.Scanner;
import java.util.stream.Stream;

class HelpCommand implements Command {

    @Override
    public void execute(TerminalClient client, Scanner arguments) {
        if (arguments.hasNext()) {
            Command command = findCommand(arguments.next());

            if (arguments.hasNext())
                throw new IllegalArgumentException();

            if (command != null)
                displayCommandHelp(client, command);
            else
                displayGeneralHelp(client);
        } else {
            displayGeneralHelp(client);
        }
    }

    private void displayGeneralHelp(TerminalClient client) {
        printf("Commands:\n");

        int maxCommandNameLength = calculateMaxCommandNameLength();

        for (Command command : COMMANDS)
            printf("  %-" + maxCommandNameLength + "s  %s\n", command.getName(), command.getDescription());

        printf("\nType 'help <command>' for command specific help.\n");
    }

    private void displayCommandHelp(TerminalClient client, Command command) {
        printf("Usage: %s\n\n  %s\n\n", command.getUsage(), command.getDescription());
    }

    private int calculateMaxCommandNameLength() {
        return Stream.of(COMMAND_NAMES).mapToInt(String::length).max().orElse(0);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Display the help";
    }

    @Override
    public String getUsage() {
        return "help [command]";
    }

}
