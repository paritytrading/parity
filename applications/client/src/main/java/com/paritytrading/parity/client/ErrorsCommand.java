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
