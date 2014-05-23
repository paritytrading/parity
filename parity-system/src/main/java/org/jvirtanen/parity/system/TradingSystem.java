package org.jvirtanen.parity.system;

import static org.jvirtanen.parity.util.Applications.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.joda.time.LocalDate;
import org.jvirtanen.config.Configs;

class TradingSystem {

    public static final long EPOCH_MILLIS = new LocalDate().toDateTimeAtStartOfDay().getMillis();

    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            usage("parity-system <configuration-file>");

        try {
            main(config(args[0]));
        } catch (ConfigException e) {
            error(e);
        } catch (FileNotFoundException e) {
            error(e);
        }
    }

    private static void main(Config config) throws IOException {
        int orderEntryPort = Configs.getPort(config, "order-entry.port");

        OrderEntry orderEntry = OrderEntry.create(orderEntryPort);

        orderEntry.run();
    }

}
