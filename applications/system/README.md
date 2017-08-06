# Parity Trading System

Parity Trading System is a server application for running a financial
exchange.

## Download

Download the [latest release][] from GitHub.

  [latest release]: https://github.com/paritytrading/parity/releases/latest

## Usage

Run Parity Trading System with Java:

```
java -jar parity-system.jar <configuration-file>
```

The command line arguments are as follows:

- `<configuration-file>`. The configuration file. The configuration file
  specifies the services that are made available to market particpants.

Once started, the application starts listening for order entry sessions
initiated by market participants and publishing market data and market
reports.

## Configuration

Parity Trading System uses a configuration file to specify the services it
makes available to market participants.

The following configuration parameters are required:

```
market-data {

    # The market data session name.
    session = parity

    # The IP address or name of the network interface for the market data session.
    multicast-interface = 127.0.0.1

    # The IP address of the multicast group for the market data session.
    multicast-group = 224.0.0.1

    # The UDP port for the market data session.
    multicast-port = 5000

    # The local IP address for the market data request server.
    request-address = 127.0.0.1

    # The local UDP port for the market data request server.
    request-port = 5001

}

market-report {

    # The market reporting session name.
    session = parity

    # The IP address or name of the network interface for the market reporting session.
    multicast-interface = 127.0.0.1

    # The IP address of the multicast group for the market reporting session.
    multicast-group = 224.0.0.1

    # The UDP port for the market reporting session.
    multicast-port = 6000

    # The local IP address for the market reporting request server.
    request-address = 127.0.0.1

    # The local UDP port for the market reporting request server.
    request-port = 6001

}

order-entry {

    # The local IP address for the order entry server.
    address = 127.0.0.1

    # The local TCP port for the order entry server.
    port = 4000

}

# A list of zero or more instruments.
instruments = [ AAPL, BAR, BAZ ]
```

See the `etc` directory for an example configuration file.

## License

Released under the Apache License, Version 2.0.
