package org.jvirtanen.parity.reporter;

import static org.jvirtanen.parity.util.Applications.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.jvirtanen.config.Configs;

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
        InetAddress tradeReportMulticastInterface = Configs.getInetAddress(config, "trade-report.multicast-interface");
        InetAddress tradeReportMulticastGroup     = Configs.getInetAddress(config, "trade-report.multicast-group");
        int         tradeReportMulticastPort      = Configs.getPort(config, "trade-report.multicast-port");
        InetAddress tradeReportRequestAddress     = Configs.getInetAddress(config, "trade-report.request-address");
        int         tradeReportRequestPort        = Configs.getPort(config, "trade-report.request-port");

        TradeReportClient client = TradeReportClient.open(tradeReportMulticastInterface,
                new InetSocketAddress(tradeReportMulticastGroup, tradeReportMulticastPort),
                new InetSocketAddress(tradeReportRequestAddress, tradeReportRequestPort),
                new Display());

        while (true)
            client.receive();
    }

}
