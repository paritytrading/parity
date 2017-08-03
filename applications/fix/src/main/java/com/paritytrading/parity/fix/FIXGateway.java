package com.paritytrading.parity.fix;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.parity.util.Instruments;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.jvirtanen.config.Configs;

class FIXGateway {

    public static void main(String[] args) {
        if (args.length != 1)
            usage("parity-fix <configuration-file>");

        try {
            main(config(args[0]));
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        } catch (IOException e) {
            fatal(e);
        }
    }

    private static void main(Config config) throws IOException {
        OrderEntryFactory orderEntry = orderEntry(config);
        FIXAcceptor       fix        = fix(orderEntry, config);

        Events.process(fix);
    }

    private static OrderEntryFactory orderEntry(Config config) {
        InetAddress address = Configs.getInetAddress(config, "order-entry.address");
        int         port    = Configs.getPort(config, "order-entry.port");

        return new OrderEntryFactory(new InetSocketAddress(address, port));
    }

    private static FIXAcceptor fix(OrderEntryFactory orderEntry, Config config) throws IOException {
        InetAddress address      = Configs.getInetAddress(config, "fix.address");
        int         port         = Configs.getPort(config, "fix.port");
        String      senderCompId = config.getString("fix.sender-comp-id");

        Instruments instruments = Instruments.fromConfig(config, "instruments");

        return FIXAcceptor.open(orderEntry, new InetSocketAddress(address, port),
                senderCompId, instruments);
    }

}
