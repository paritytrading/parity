package com.paritytrading.parity.client;

import static org.jvirtanen.util.Applications.*;

import com.paritytrading.foundation.ASCII;
import com.paritytrading.nassau.soupbintcp.SoupBinTCP;
import com.paritytrading.parity.net.poe.POE;
import com.paritytrading.parity.util.Instruments;
import com.paritytrading.parity.util.OrderIDGenerator;
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
import java.util.stream.Stream;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jvirtanen.config.Configs;

class TerminalClient implements Closeable {

    static final Command[] COMMANDS = new Command[] {
        new EnterCommand(POE.BUY),
        new EnterCommand(POE.SELL),
        new CancelCommand(),
        new OrdersCommand(),
        new TradesCommand(),
        new ErrorsCommand(),
        new HelpCommand(),
        new ExitCommand(),
    };

    static final String[] COMMAND_NAMES = Stream.of(COMMANDS)
            .map(Command::getName)
            .toArray(String[]::new);

    static final Locale LOCALE = Locale.US;

    static final long NANOS_PER_MILLI = 1_000_000;

    private final Events events;

    private final OrderEntry orderEntry;

    private final Instruments instruments;

    private final OrderIDGenerator orderIdGenerator;

    private boolean closed;

    private TerminalClient(Events events, OrderEntry orderEntry, Instruments instruments) {
        this.events      = events;
        this.orderEntry  = orderEntry;
        this.instruments = instruments;

        this.orderIdGenerator = new OrderIDGenerator();
    }

    static TerminalClient open(InetSocketAddress address, String username,
            String password, Instruments instruments) throws IOException {
        Events events = new Events();

        OrderEntry orderEntry = OrderEntry.open(address, events);

        SoupBinTCP.LoginRequest loginRequest = new SoupBinTCP.LoginRequest();

        ASCII.putLeft(loginRequest.username, username);
        ASCII.putLeft(loginRequest.password, password);
        ASCII.putRight(loginRequest.requestedSession, "");
        ASCII.putLongRight(loginRequest.requestedSequenceNumber, 0);

        orderEntry.getTransport().login(loginRequest);

        return new TerminalClient(events, orderEntry, instruments);
    }

    OrderEntry getOrderEntry() {
        return orderEntry;
    }

    Instruments getInstruments() {
        return instruments;
    }

    OrderIDGenerator getOrderIdGenerator() {
        return orderIdGenerator;
    }

    Events getEvents() {
        return events;
    }

    void run() throws IOException {
        LineReader reader = LineReaderBuilder.builder()
            .completer(new StringsCompleter(COMMAND_NAMES))
            .build();

        printf("Type 'help' for help.\n");

        while (!closed) {
            String line = reader.readLine("> ");
            if (line == null)
                break;

            Scanner scanner = scan(line);

            if (!scanner.hasNext())
                continue;

            Command command = findCommand(scanner.next());
            if (command == null) {
                printf("error: Unknown command\n");
                continue;
            }

            try {
                command.execute(this, scanner);
            } catch (IllegalArgumentException e) {
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

    static Command findCommand(String name) {
        for (Command command : COMMANDS) {
            if (name.equals(command.getName()))
                return command;
        }

        return null;
    }

    static void printf(String format, Object... args) {
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

        Instruments instruments = Instruments.fromConfig(config, "instruments");

        TerminalClient.open(new InetSocketAddress(orderEntryAddress, orderEntryPort),
                orderEntryUsername, orderEntryPassword, instruments).run();
    }

}
