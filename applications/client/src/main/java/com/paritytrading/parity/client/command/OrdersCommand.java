package com.paritytrading.parity.client.command;

import com.paritytrading.parity.client.TerminalClient;
import com.paritytrading.parity.client.event.Order;
import com.paritytrading.parity.client.event.Orders;
import java.util.Scanner;

class OrdersCommand implements Command {

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws CommandException {
        if (arguments.hasNext())
            throw new CommandException();

        client.printf("\n%s\n", Order.HEADER);
        for (Order order : Orders.collect(client.getEvents()))
            client.printf("%s\n", order.format());
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
