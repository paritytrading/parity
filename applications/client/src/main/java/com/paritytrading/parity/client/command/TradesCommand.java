package com.paritytrading.parity.client.command;

import com.paritytrading.parity.client.TerminalClient;
import com.paritytrading.parity.client.event.Trade;
import com.paritytrading.parity.client.event.Trades;
import java.util.Scanner;

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
