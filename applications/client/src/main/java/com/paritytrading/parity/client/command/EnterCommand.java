package com.paritytrading.parity.client.command;

import static com.paritytrading.parity.client.TerminalClient.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.parity.client.TerminalClient;
import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.util.Instrument;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

class EnterCommand implements Command {

    private POE.EnterOrder message;

    public EnterCommand(byte side) {
        this.message = new POE.EnterOrder();

        this.message.side = side;
    }

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws CommandException, IOException {
        try {
            double quantity   = arguments.nextDouble();
            long   instrument = ASCII.packLong(arguments.next());
            double price      = arguments.nextDouble();

            if (arguments.hasNext())
                throw new CommandException();

            Instrument config = client.getInstruments().get(instrument);
            if (config == null)
                throw new CommandException();

            execute(client, (long)(quantity * config.getSizeFactor()), instrument, (long)(price * config.getPriceFactor()));
        } catch (NoSuchElementException e) {
            throw new CommandException();
        }
    }

    private void execute(TerminalClient client, long quantity, long instrument, long price) throws IOException {
        String orderId = client.getOrderIdGenerator().next();

        ASCII.putLeft(message.orderId, orderId);
        message.quantity   = quantity;
        message.instrument = instrument;
        message.price      = price;

        client.getOrderEntry().send(message);

        client.printf("\nOrder ID\n----------------\n%s\n\n", orderId);
    }

    @Override
    public String getName() {
        return message.side == POE.BUY ? "buy" : "sell";
    }

    @Override
    public String getDescription() {
        return "Enter a " + getName() + " order";
    }

    @Override
    public String getUsage() {
        return getName() + " <quantity> <instrument> <price>";
    }

}
