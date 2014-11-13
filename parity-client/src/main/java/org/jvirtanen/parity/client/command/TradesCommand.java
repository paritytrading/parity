package org.jvirtanen.parity.client.command;

import java.util.Scanner;
import org.jvirtanen.parity.client.TerminalClient;
import org.jvirtanen.parity.client.event.Trade;
import org.jvirtanen.parity.client.event.Trades;

class TradesCommand implements Command {

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws CommandException {
        if (arguments.hasNext())
            throw new CommandException();

        client.printf("\n%s\n", Trade.HEADER);
        for (Trade trade : Trades.collect(client.getEvents()))
            client.printf("%s\n", trade.format());
        client.printf("\n");
    }

    @Override
    public String getName() {
        return "trades";
    }

    @Override
    public String getDescription() {
        return "Display occurred trades";
    }

    @Override
    public String getUsage() {
        return "trades";
    }

}
