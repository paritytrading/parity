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
package com.paritytrading.parity.fix;

import com.paritytrading.parity.util.Instruments;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.jvirtanen.config.Configs;

class FIXGateway {

    public static void main(String[] args) {
        if (args.length != 1)
            usage();

        try {
            main(config(args[0]));
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private static void main(Config config) throws IOException {
        OrderEntryFactory orderEntry = orderEntry(config);
        FIXAcceptor       fix        = fix(orderEntry, config);

        Events.process(fix);
    }

    private static OrderEntryFactory orderEntry(Config config) {
        InetAddress address = Configs.getInetAddress(config, "order-entry.address");
        int         port    = Configs.getPort(config, "order-entry.port");

        return new OrderEntryFactory(new InetSocketAddress(address, port));
    }

    private static FIXAcceptor fix(OrderEntryFactory orderEntry, Config config) throws IOException {
        InetAddress address      = Configs.getInetAddress(config, "fix.address");
        int         port         = Configs.getPort(config, "fix.port");
        String      senderCompId = config.getString("fix.sender-comp-id");

        Instruments instruments = Instruments.fromConfig(config, "instruments");

        return FIXAcceptor.open(orderEntry, new InetSocketAddress(address, port),
                senderCompId, instruments);
    }

    private static Config config(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists() || !file.isFile())
            throw new FileNotFoundException(filename + ": No such file");

        return ConfigFactory.parseFile(file);
    }

    private static void usage() {
        System.err.println("Usage: parity-fix <configuration-file>");
        System.exit(2);
    }


    private static void error(Throwable throwable) {
        System.err.println("error: " + throwable.getMessage());
        System.exit(1);
    }

    private static void fatal(Throwable throwable) {
        System.err.println("error: " + throwable.getMessage());
        System.err.println();
        throwable.printStackTrace(System.err);
        System.err.println();
        System.exit(1);
    }

}
