package org.jvirtanen.parity.client.command;

import static org.jvirtanen.parity.client.TerminalClient.*;

import com.paritytrading.foundation.ASCII;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jvirtanen.parity.client.TerminalClient;
import org.jvirtanen.parity.net.poe.POE;

class EnterCommand implements Command {

    private POE.EnterOrder message;

    public EnterCommand(byte side) {
        this.message = new POE.EnterOrder();

        this.message.side = side;
    }

    @Override
    public void execute(TerminalClient client, Scanner arguments) throws CommandException, IOException {
        try {
            long quantity   = arguments.nextInt();
            long instrument = ASCII.packLong(arguments.next());
            long price      = (int)(arguments.nextDouble() * PRICE_FACTOR);

            if (arguments.hasNext())
                throw new CommandException();

            execute(client, quantity, instrument, price);
        } catch (NoSuchElementException e) {
            throw new CommandException();
        }
    }

    private void execute(TerminalClient client, long quantity, long instrument, long price) throws IOException {
        message.orderId    = client.getOrderIdGenerator().next();
        message.quantity   = quantity;
        message.instrument = instrument;
        message.price      = price;

        client.getOrderEntry().send(message);

        client.printf("\nOrder ID\n----------------\n%s\n\n", message.orderId);
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
