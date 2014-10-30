package org.jvirtanen.parity.system;

import static org.jvirtanen.parity.util.Applications.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.joda.time.LocalDate;
import org.jvirtanen.config.Configs;
import org.jvirtanen.parity.net.pmd.PMD;

class TradingSystem {

    public static final long EPOCH_MILLIS = new LocalDate().toDateTimeAtStartOfDay().getMillis();

    private MarketData marketData;

    private Events events;

    private TradingSystem(MarketData marketData, Events events) {
        this.marketData = marketData;

        this.events = events;
    }

    public void run() throws IOException {
        PMD.Version message = new PMD.Version();
        message.version = PMD.VERSION;

        marketData.send(message);

        events.run();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            usage("parity-system <configuration-file>");

        try {
            main(config(args[0]));
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        }
    }

    private static void main(Config config) throws IOException {
        String      marketDataSession        = config.getString("market-data.session");
        InetAddress marketDataMulticastGroup = Configs.getInetAddress(config, "market-data.multicast-group");
        int         marketDataMulticastPort  = Configs.getPort(config, "market-data.multicast-port");

        MarketData marketData = MarketData.create(marketDataSession,
                new InetSocketAddress(marketDataMulticastGroup, marketDataMulticastPort));

        int orderEntryPort = Configs.getPort(config, "order-entry.port");

        OrderEntryServer orderEntry = OrderEntryServer.create(orderEntryPort);

        Events events = new Events(orderEntry);

        new TradingSystem(marketData, events).run();
    }

}
