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
package com.paritytrading.parity.ticker;

import static java.util.Arrays.*;
import static org.jvirtanen.util.Applications.*;

import com.paritytrading.nassau.MessageListener;
import com.paritytrading.nassau.util.BinaryFILE;
import com.paritytrading.nassau.util.MoldUDP64;
import com.paritytrading.nassau.util.SoupBinTCP;
import com.paritytrading.parity.book.Market;
import com.paritytrading.parity.net.pmd.PMDParser;
import com.paritytrading.parity.util.Instrument;
import com.paritytrading.parity.util.Instruments;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import org.jvirtanen.config.Configs;

class StockTicker {

    public static void main(String[] args) {
        if (args.length < 1)
            usage();

        boolean taq = args[0].equals("-t");

        try {
            main(taq, taq ? copyOfRange(args, 1, args.length) : args);
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private static void main(boolean taq, String[] args) throws IOException {
        switch (args.length) {
        case 1:
            listen(taq, config(args[0]));
            return;
        case 2:
            read(taq, config(args[0]), new File(args[1]));
            return;
        default:
            usage();
            return;
        }
    }

    private static void listen(boolean taq, Config config) throws IOException {
        Instruments instruments = Instruments.fromConfig(config, "instruments");

        MarketDataListener listener = taq ? new TAQFormat(instruments) : new DisplayFormat(instruments);

        Market market = new Market(listener);

        for (Instrument instrument : instruments)
            market.open(instrument.asLong());

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

        listen(config, new PMDParser(processor));
    }

    private static void listen(Config config, MessageListener listener) throws IOException {
        if (config.hasPath("market-data.multicast-interface")) {
            NetworkInterface multicastInterface = Configs.getNetworkInterface(config, "market-data.multicast-interface");
            InetAddress      multicastGroup     = Configs.getInetAddress(config, "market-data.multicast-group");
            int              multicastPort      = Configs.getPort(config, "market-data.multicast-port");
            InetAddress      requestAddress     = Configs.getInetAddress(config, "market-data.request-address");
            int              requestPort        = Configs.getPort(config, "market-data.request-port");

            MoldUDP64.receive(multicastInterface, new InetSocketAddress(multicastGroup, multicastPort),
                    new InetSocketAddress(requestAddress, requestPort), listener);
        } else {
            InetAddress address  = Configs.getInetAddress(config, "market-data.address");
            int         port     = Configs.getPort(config, "market-data.port");
            String      username = config.getString("market-data.username");
            String      password = config.getString("market-data.password");

            SoupBinTCP.receive(new InetSocketAddress(address, port), username, password, listener);
        }
    }

    private static void read(boolean taq, Config config, File file) throws IOException {
        Instruments instruments = Instruments.fromConfig(config, "instruments");

        MarketDataListener listener = taq ? new TAQFormat(instruments) : new DisplayFormat(instruments);

        Market market = new Market(listener);

        for (Instrument instrument : instruments)
            market.open(instrument.asLong());

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

        BinaryFILE.read(file, new PMDParser(processor));
    }

    private static void usage() {
        System.err.println("Usage: parity-ticker [-t] <configuration-file> [<input-file>]");
        System.exit(2);
    }

}
