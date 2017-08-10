# Parity Terminal Client

Parity Terminal Client is a simple console application for entering orders
into the trading system.

## Download

Download the [latest release][] from GitHub.

  [latest release]: https://github.com/paritytrading/parity/releases/latest

## Usage

Run Parity Terminal Client with Java:

```
java -jar parity-client.jar <configuration-file>
```

The command line arguments are as follows:

- `<configuration-file>`: A configuration file. The configuration file
  specifies how to connect to the trading system.

Once started, the application displays a command prompt:

```
Type 'help' for help.
>
```

You can interact with the application by entering commands into the command
prompt. For example, to enter a buy order, use the `buy` command:

```
> buy 100 AAPL 150.00
```

The command enters a buy order of 100 units of the instrument AAPL at the
limit price of 150.00 into the trading system. Assuming that AAPL can be
traded on the trading system and that there is no matching sell order in
the order book, the order will remain open. To list the open orders, use
the `orders` command:

```
> orders
```

Use the `help` command to see a list of all available commands.

## Configuration

Parity Terminal Client uses a configuration file to specify how to connect to
the trading system.

The following configuration parameters are required:

```
order-entry {

    # The IP address of the trading system.
    address = 127.0.0.1

    # The TCP port for order entry at the trading system.
    port = 4000

    # The order entry username.
    username = parity

    # The order entry password.
    password = parity

}

instruments {

    # The number of digits in the integer part of a price.
    price-integer-digits = 4

    # The number of digits in the integer part of a size.
    size-integer-digits = 7

    AAPL {

        # The number of digits in the fractional part of a price.
        price-fraction-digits = 2

        # The number of digits in the fractional part of a size.
        size-fraction-digits = 0

    }

}
```

See the `etc` directory for an example configuration file.

## License

Released under the Apache License, Version 2.0.
