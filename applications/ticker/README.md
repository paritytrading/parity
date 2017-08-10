# Parity Stock Ticker

Parity Stock Ticker is a simple console application that displays the best
prices and latest trades in the trading system.

## Download

Download the [latest release][] from GitHub.

  [latest release]: https://github.com/paritytrading/parity/releases/latest

## Usage

Run Parity Stock Ticker with Java:

```
java -jar parity-ticker.jar [-t] <configuration-file> [<input-file>]
```

The application can either listen to a live market data feed or to read a
historical market data file.

The command line options are as follows:

- `-t`: Format the output as [TAQ][].

  [TAQ]: ../../libraries/file/doc/TAQ.md

The command line arguments are as follows:

- `<configuration-file>`: The configuration file. The configuration file
  specifies how to display instruments. When listening to live market data,
  it also specifies how to connect to the trading system.

- `<input-file>`: The input file. The application reads market events from
  the input file in the NASDAQ BinaryFILE format.

When listening to a live market data feed, the application first replays
market events that have taken place so far. Then it proceeds to display
market events in real time.

## Configuration

Parity Stock Ticker uses a configuration file to specify how to display
instruments and connect to the trading system. The application supports two
transport options: NASDAQ MoldUDP64 and NASDAQ SoupBinTCP.

The following configuration parameters are required when connecting to the
trading system using the MoldUDP64 transport:

```
market-data {

    # The IP address or name of the network interface for the MoldUDP64 session.
    multicast-interface = 127.0.0.1

    # The IP address of the multicast group for the MoldUDP64 session.
    multicast-group = 224.0.0.1

    # The UDP port for the MoldUDP64 session.
    multicast-port = 5000

    # The IP address of the MoldUDP64 request server.
    request-address = 127.0.0.1

    # The UDP port of the MoldUDP64 request server.
    request-port = 5001

}
```

The following configuration parameters are required when connecting to the
trading system using the SoupBinTCP transport:

```
market-data {

    # The IP address of the SoupBinTCP server.
    address = 127.0.0.1

    # The TCP port of the SoupBinTCP server.
    port = 5000

    # The SoupBinTCP username.
    username = parity

    # The SoupBinTCP password.
    password = parity

}
```

The following configuration parameters are required always:

```
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

See the `etc` directory for example configuration files.

## License

Released under the Apache License, Version 2.0.
