package org.jvirtanen.parity.ticker;

import static org.jvirtanen.parity.util.Applications.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import org.jvirtanen.config.Configs;

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
        InetAddress marketDataMulticastInterface = Configs.getInetAddress(config, "market-data.multicast-interface");
        InetAddress marketDataMulticastGroup     = Configs.getInetAddress(config, "market-data.multicast-group");
        int         marketDataMulticastPort      = Configs.getPort(config, "market-data.multicast-port");
        InetAddress marketDataRequestAddress     = Configs.getInetAddress(config, "market-data.request-address");
        int         marketDataRequestPort        = Configs.getPort(config, "market-data.request-port");

        List<String> instruments = config.getStringList("instruments");

        MarketDataListener listener = taq ? new TAQFormat() : new DisplayFormat(instruments);

        MarketDataClient client = MarketDataClient.open(marketDataMulticastInterface,
                new InetSocketAddress(marketDataMulticastGroup, marketDataMulticastPort),
                new InetSocketAddress(marketDataRequestAddress, marketDataRequestPort),
                instruments, listener);

        while (true)
            client.receive();
    }

}
