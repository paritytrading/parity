package org.jvirtanen.parity.client;

import static org.jvirtanen.parity.util.Applications.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Locale;
import java.util.Scanner;
import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
import org.jvirtanen.config.Configs;
import org.jvirtanen.nassau.soupbintcp.SoupBinTCP;
import org.jvirtanen.parity.client.command.Command;
import org.jvirtanen.parity.client.command.CommandException;
import org.jvirtanen.parity.client.command.Commands;
import org.jvirtanen.parity.client.event.Events;
import org.jvirtanen.parity.client.util.OrderIDGenerator;

public class TerminalClient implements Closeable {

    public static final Locale LOCALE = Locale.US;

    public static final long PRICE_FACTOR = 10000;

    private Events events;

    private OrderEntryClient orderEntry;

    private OrderIDGenerator orderIdGenerator;

    private boolean closed;

    private TerminalClient(Events events, OrderEntryClient orderEntry) {
        this.events     = events;
        this.orderEntry = orderEntry;

        this.orderIdGenerator = new OrderIDGenerator();
    }

    public static TerminalClient open(InetSocketAddress address, String username, String password) throws IOException {
        Events events = new Events();

        OrderEntryClient orderEntry = OrderEntryClient.open(address, events);

        SoupBinTCP.LoginRequest loginRequest = new SoupBinTCP.LoginRequest();

        loginRequest.username = username;
        loginRequest.password = password;
        loginRequest.requestedSession = "";
        loginRequest.requestedSequenceNumber = 0;

        orderEntry.getTransport().login(loginRequest);

        return new TerminalClient(events, orderEntry);
    }

    public OrderEntryClient getOrderEntry() {
        return orderEntry;
    }

    public OrderIDGenerator getOrderIdGenerator() {
        return orderIdGenerator;
    }

    public Events getEvents() {
        return events;
    }

    public void run() throws IOException {
        ConsoleReader reader = new ConsoleReader();

        reader.addCompleter(new StringsCompleter(Commands.names().castToList()));

        printf("Type 'help' for help.\n");

        while (!closed) {
            String line = reader.readLine("> ");
            if (line == null)
                break;

            Scanner scanner = scan(line);

            if (!scanner.hasNext())
                continue;

            Command command = Commands.find(scanner.next());
            if (command == null) {
                printf("error: Unknown command\n");
                continue;
            }

            try {
                command.execute(this, scanner);
            } catch (CommandException e) {
                printf("Usage: %s\n", command.getUsage());
            } catch (ClosedChannelException e) {
                printf("error: Connection closed\n");
            }
        }

        close();
    }

    @Override
    public void close() {
        orderEntry.close();

        closed = true;
    }

    public void printf(String format, Object... args) {
        System.out.printf(LOCALE, format, args);
    }

    private Scanner scan(String text) {
        Scanner scanner = new Scanner(text);
        scanner.useLocale(LOCALE);

        return scanner;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            usage("parity-client <configuration-file>");

        try {
            main(config(args[0]));
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        }
    }

    private static void main(Config config) throws IOException {
        InetAddress orderEntryAddress  = Configs.getInetAddress(config, "order-entry.address");
        int         orderEntryPort     = Configs.getPort(config, "order-entry.port");
        String      orderEntryUsername = config.getString("order-entry.username");
        String      orderEntryPassword = config.getString("order-entry.password");

        TerminalClient.open(new InetSocketAddress(orderEntryAddress, orderEntryPort),
                orderEntryUsername, orderEntryPassword).run();
    }

}
