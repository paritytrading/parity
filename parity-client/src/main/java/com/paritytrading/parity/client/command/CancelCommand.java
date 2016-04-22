package com.paritytrading.parity.client.command;

import com.paritytrading.parity.client.TerminalClient;
import com.paritytrading.parity.net.poe.POE;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

class CancelCommand implements Command {

    private POE.CancelOrder message;

    public CancelCommand() {
        message = new POE.CancelOrder();
    }

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws CommandException, IOException {
        try {
            String orderId = arguments.next();

            if (arguments.hasNext())
                throw new CommandException();

            execute(client, orderId);
        } catch (NoSuchElementException e) {
            throw new CommandException();
        }
    }

    private void execute(TerminalClient client, String orderId) throws IOException {
        message.orderId  = orderId;
        message.quantity = 0;

        client.getOrderEntry().send(message);
    }

    @Override
    public String getName() {
        return "cancel";
    }

    @Override
    public String getDescription() {
        return "Cancel an order";
    }

    @Override
    public String getUsage() {
        return "cancel <order-id>";
    }

}
