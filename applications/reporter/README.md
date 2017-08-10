# Parity Trade Reporter

Parity Trade Reporter is a simple console application that displays all
occurred trades in the trading system.

## Download

Download the [latest release][] from GitHub.

  [latest release]: https://github.com/paritytrading/parity/releases/latest

## Usage

Run Parity Trade Reporter with Java:

```
java -jar parity-reporter.jar [-t] <configuration-file>
```

The command line options are as follows:

- `-t`: Format the output as tab-separated values (TSV).

The command line arguments are as follows:

- `<configuration-file>`: The configuration file. The configuration file
  specifies how to display instruments and connect to the the trading system.

Once started, the application first replays trades that have taken place so
far. Then it proceeds to display trades in real time.

## Configuration

Parity Trade Reporter uses a configuration file to specify how to display
instruments and connect to the trading system. The application supports two
transport options: NASDAQ MoldUDP64 and NASDAQ SoupBinTCP.

The following configuration parameters are required when using the MoldUDP64
transport:

```
trade-report {

    # The IP address or name of the network interface for the MoldUDP64 session.
    multicast-interface = 127.0.0.1

    # The IP address of the multicast group for the MoldUDP64 session.
    multicast-group = 224.0.0.1

    # The UDP port for the MoldUDP64 session.
    multicast-port = 6000

    # The IP address of the MoldUDP64 request server.
    request-address = 127.0.0.1

    # The UDP port of the MoldUDP64 request server.
    request-port = 6001

}
```

The following configuration parameters are required when using the SoupBinTCP
transport:

```
trade-report {

    # The IP address of the SoupBinTCP server.
    address = 127.0.0.1

    # The TCP port of the SoupBinTCP server.
    port = 6000

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
