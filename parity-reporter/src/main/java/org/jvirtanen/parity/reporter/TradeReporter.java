package org.jvirtanen.parity.reporter;

import static org.jvirtanen.parity.util.Applications.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import org.jvirtanen.config.Configs;
import org.jvirtanen.parity.net.pmr.PMRParser;
import org.jvirtanen.parity.util.MoldUDP64Client;

class TradeReporter {

    public static void main(String[] args) {
        if (args.length != 1)
            usage("parity-reporter <configuration-file>");

        try {
            main(config(args[0]));
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private static void main(Config config) throws IOException {
        NetworkInterface multicastInterface = Configs.getNetworkInterface(config, "trade-report.multicast-interface");
        InetAddress      multicastGroup     = Configs.getInetAddress(config, "trade-report.multicast-group");
        int              multicastPort      = Configs.getPort(config, "trade-report.multicast-port");
        InetAddress      requestAddress     = Configs.getInetAddress(config, "trade-report.request-address");
        int              requestPort        = Configs.getPort(config, "trade-report.request-port");

        MoldUDP64Client transport = MoldUDP64Client.open(multicastInterface,
                new InetSocketAddress(multicastGroup, multicastPort),
                new InetSocketAddress(requestAddress, requestPort),
                new PMRParser(new Display()));

        while (true)
            transport.receive();
    }

}
