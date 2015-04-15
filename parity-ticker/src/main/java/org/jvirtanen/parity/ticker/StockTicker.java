package org.jvirtanen.parity.ticker;

import static org.jvirtanen.lang.Strings.*;
import static org.jvirtanen.parity.util.Applications.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.List;
import org.jvirtanen.config.Configs;
import org.jvirtanen.parity.net.pmd.PMDParser;
import org.jvirtanen.parity.top.Market;
import org.jvirtanen.parity.util.MoldUDP64Client;

class StockTicker {

    private static final String USAGE = "parity-ticker [-t] <configuration-file>";

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 2)
            usage(USAGE);

        boolean taq = false;

        if (args.length == 2) {
            if (!args[0].equals("-t"))
                usage(USAGE);

            taq = true;
        }

        try {
            main(config(args[taq ? 1 : 0]), taq);
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private static void main(Config config, boolean taq) throws IOException {
        NetworkInterface multicastInterface = Configs.getNetworkInterface(config, "market-data.multicast-interface");
        InetAddress      multicastGroup     = Configs.getInetAddress(config, "market-data.multicast-group");
        int              multicastPort      = Configs.getPort(config, "market-data.multicast-port");
        InetAddress      requestAddress     = Configs.getInetAddress(config, "market-data.request-address");
        int              requestPort        = Configs.getPort(config, "market-data.request-port");

        List<String> instruments = config.getStringList("instruments");

        MarketDataListener listener = taq ? new TAQFormat() : new DisplayFormat(instruments);

        Market market = new Market(listener);

        for (String instrument : instruments)
            market.open(encodeLong(instrument));

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

        MoldUDP64Client transport = MoldUDP64Client.open(multicastInterface,
                new InetSocketAddress(multicastGroup, multicastPort),
                new InetSocketAddress(requestAddress, requestPort),
                new PMDParser(processor));

        while (true)
            transport.receive();
    }

}
