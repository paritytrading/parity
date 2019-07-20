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
package com.paritytrading.parity.system;

import static org.jvirtanen.util.Applications.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.jvirtanen.config.Configs;

class TradingSystem {

    static final long EPOCH_MILLIS = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
        .toInstant().toEpochMilli();

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            usage("parity-system <configuration-file>");

        try {
            main(config(args[0]));
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        }
    }

    private static void main(Config config) throws IOException {
        MarketData marketData = marketData(config);

        MarketReporting marketReporting = marketReporting(config);

        List<String> instruments = config.getStringList("instruments");

        OrderBooks books = new OrderBooks(instruments, marketData, marketReporting);

        OrderEntry orderEntry = orderEntry(config, books);

        marketData.version();
        marketReporting.version();

        new Events(marketData, marketReporting, orderEntry).run();
    }

    private static MarketData marketData(Config config) throws IOException {
        String           session            = config.getString("market-data.session");
        NetworkInterface multicastInterface = Configs.getNetworkInterface(config, "market-data.multicast-interface");
        InetAddress      multicastGroup     = Configs.getInetAddress(config, "market-data.multicast-group");
        int              multicastPort      = Configs.getPort(config, "market-data.multicast-port");
        InetAddress      requestAddress     = Configs.getInetAddress(config, "market-data.request-address");
        int              requestPort        = Configs.getPort(config, "market-data.request-port");

        return MarketData.open(session, multicastInterface,
                new InetSocketAddress(multicastGroup, multicastPort),
                new InetSocketAddress(requestAddress, requestPort));
    }

    private static MarketReporting marketReporting(Config config) throws IOException {
        String           session            = config.getString("market-report.session");
        NetworkInterface multicastInterface = Configs.getNetworkInterface(config, "market-report.multicast-interface");
        InetAddress      multicastGroup     = Configs.getInetAddress(config, "market-report.multicast-group");
        int              multicastPort      = Configs.getPort(config, "market-report.multicast-port");
        InetAddress      requestAddress     = Configs.getInetAddress(config, "market-report.request-address");
        int              requestPort        = Configs.getPort(config, "market-report.request-port");

        return MarketReporting.open(session, multicastInterface,
                new InetSocketAddress(multicastGroup, multicastPort),
                new InetSocketAddress(requestAddress, requestPort));
    }

    private static OrderEntry orderEntry(Config config, OrderBooks books) throws IOException {
        InetAddress address = Configs.getInetAddress(config, "order-entry.address");
        int         port    = Configs.getPort(config, "order-entry.port");

        return OrderEntry.open(new InetSocketAddress(address, port), books);
    }

}
