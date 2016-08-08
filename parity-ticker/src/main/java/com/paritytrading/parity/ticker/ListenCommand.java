package com.paritytrading.parity.ticker;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.nassau.MessageListener;
import com.paritytrading.nassau.util.MoldUDP64;
import com.paritytrading.nassau.util.SoupBinTCP;
import com.paritytrading.parity.book.Market;
import com.paritytrading.parity.net.pmd.PMDParser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.List;
import org.jvirtanen.config.Configs;

class ListenCommand implements Command {

    private static final String USAGE = "parity-ticker listen [-t] <configuration-file>";

    @Override
    public void execute(List<String> arguments) throws IOException {
        if (arguments.size() == 0 || arguments.size() > 2)
            usage(USAGE);

        boolean taq = false;

        if (arguments.size() == 2) {
            if (!arguments.get(0).equals("-t"))
                usage(USAGE);
            taq = true;
        }

        try {
            execute(config(arguments.get(taq ? 1 : 0)), taq);
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        }
    }

    private void execute(Config config, boolean taq) throws IOException {
        List<String> instruments = config.getStringList("instruments");

        MarketDataListener listener = taq ? new TAQFormat() : new DisplayFormat(instruments);

        Market market = new Market(listener);

        for (String instrument : instruments)
            market.open(ASCII.packLong(instrument));

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

        execute(config, new PMDParser(processor));
    }

    private void execute(Config config, MessageListener listener) throws IOException {
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

    @Override
    public String getName() {
        return "listen";
    }

    @Override
    public String getDescription() {
        return "Listen to a live market data feed";
    }

}
