package com.paritytrading.parity.ticker;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.net.pmd.PMDParser;
import com.paritytrading.parity.top.Market;
import com.paritytrading.parity.util.BinaryFILE;
import java.io.File;
import java.io.IOException;
import java.util.List;

class ReadCommand implements Command {

    private static final String USAGE = "parity-ticker read [-t] <input-file> [<instrument> ...]";

    @Override
    public void execute(List<String> arguments) throws IOException {
        if (arguments.size() == 0)
            usage(USAGE);

        boolean taq = false;

        String filename = arguments.get(0);

        int i = 1;

        if (filename.equals("-t")) {
            if (arguments.size() == 1)
                usage(USAGE);

            taq = true;

            filename = arguments.get(1);

            i = 2;
        }

        List<String> instruments = arguments.subList(i, arguments.size());

        execute(new File(filename), instruments, taq);
    }

    private void execute(File file, List<String> instruments, boolean taq) throws IOException {
        MarketDataListener listener = taq ? new TAQFormat() : new DisplayFormat(instruments);

        Market market = new Market(listener);

        for (String instrument : instruments)
            market.open(ASCII.packLong(instrument));

        MarketDataProcessor processor = new MarketDataProcessor(market, listener);

        BinaryFILE.read(file, new PMDParser(processor));
    }

    @Override
    public String getName() {
        return "read";
    }

    @Override
    public String getDescription() {
        return "Read a historical market data file";
    }

}
