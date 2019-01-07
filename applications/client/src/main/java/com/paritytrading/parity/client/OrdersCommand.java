package com.paritytrading.parity.client;

import com.paritytrading.parity.client.event.Order;
import com.paritytrading.parity.client.event.Orders;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.TableHeader;
import java.util.Scanner;

class OrdersCommand implements Command {

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws CommandException {
        if (arguments.hasNext())
            throw new CommandException();

        Instruments instruments = client.getInstruments();

        int priceWidth = instruments.getPriceWidth();
        int sizeWidth  = instruments.getSizeWidth();

        TableHeader header = new TableHeader();

        header.add("Timestamp",       12);
        header.add("Order ID",        16);
        header.add("S",                1);
        header.add("Inst",             8);
        header.add("Quantity", sizeWidth);
        header.add("Price",   priceWidth);

        client.printf("\n");
        client.printf(header.format());

        for (Order order : Orders.collect(client.getEvents()))
            client.printf("%s\n", order.format(client.getInstruments()));
        client.printf("\n");
    }

    @Override
    public String getName() {
        return "orders";
    }

    @Override
    public String getDescription() {
        return "Display open orders";
    }

    @Override
    public String getUsage() {
        return "orders";
    }

}
