package com.paritytrading.parity.ticker;

import static java.util.Arrays.*;
import static java.util.Comparator.*;
import static org.jvirtanen.util.Applications.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.nassau.MessageListener;
import com.paritytrading.nassau.util.BinaryFILE;
import com.paritytrading.nassau.util.MoldUDP64;
import com.paritytrading.nassau.util.SoupBinTCP;
import com.paritytrading.parity.book.Market;
import com.paritytrading.parity.net.pmd.PMDParser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.List;
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
        if (args.length < 1)
            usage();

        try {
            listen(taq, config(args[0]));
        } catch (ConfigException.Parse e) {
            read(taq, new File(args[0]), asList(copyOfRange(args, 1, args.length)));
        }
    }

    private static void listen(boolean taq, Config config) throws IOException {
        List<String> instruments = config.getStringList("instruments");

        MarketDataListener listener = taq ? new TAQFormat() : new DisplayFormat(instruments);

        Market market = new Market(listener);

        for (String instrument : instruments)
            market.open(ASCII.packLong(instrument));

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

    private static void read(boolean taq, File file, List<String> instruments) throws IOException {
        MarketDataListener listener = taq ? new TAQFormat() : new DisplayFormat(instruments);

        Market market = new Market(listener);

        for (String instrument : instruments)
            market.open(ASCII.packLong(instrument));

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

        BinaryFILE.read(file, new PMDParser(processor));
    }

    private static void usage() {
        System.err.println("Usage: parity-ticker [-t] <configuration-file>");
        System.err.println("       parity-ticker [-t] <input-file> [<instrument> ...]");
        System.exit(2);
    }

}
