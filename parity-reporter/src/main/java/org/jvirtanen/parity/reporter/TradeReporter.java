package org.jvirtanen.parity.reporter;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.nassau.MessageListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import org.jvirtanen.config.Configs;
import org.jvirtanen.parity.net.pmr.PMRParser;
import org.jvirtanen.parity.util.MoldUDP64;
import org.jvirtanen.parity.util.SoupBinTCP;

class TradeReporter {

    private static final String USAGE = "parity-reporter [-t] <configuration-file>";

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 2)
            usage(USAGE);

        boolean tsv = false;

        if (args.length == 2) {
            if (!args[0].equals("-t"))
                usage(USAGE);

            tsv = true;
        }

        try {
            main(config(args[tsv ? 1 : 0]), tsv);
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private static void main(Config config, boolean tsv) throws IOException {
        MessageListener listener = new PMRParser(tsv ? new TSVFormat() : new DisplayFormat());

        if (config.hasPath("trade-report.multicast-interface")) {
            NetworkInterface multicastInterface = Configs.getNetworkInterface(config, "trade-report.multicast-interface");
            InetAddress      multicastGroup     = Configs.getInetAddress(config, "trade-report.multicast-group");
            int              multicastPort      = Configs.getPort(config, "trade-report.multicast-port");
            InetAddress      requestAddress     = Configs.getInetAddress(config, "trade-report.request-address");
            int              requestPort        = Configs.getPort(config, "trade-report.request-port");

            MoldUDP64.receive(multicastInterface, new InetSocketAddress(multicastGroup, multicastPort),
                    new InetSocketAddress(requestAddress, requestPort), listener);
        } else {
            InetAddress address  = Configs.getInetAddress(config, "trade-report.address");
            int         port     = Configs.getPort(config, "trade-report.port");
            String      username = config.getString("trade-report.username");
            String      password = config.getString("trade-report.password");

            SoupBinTCP.receive(new InetSocketAddress(address, port), username, password, listener);
        }
    }

}
