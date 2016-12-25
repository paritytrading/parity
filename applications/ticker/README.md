Parity Stock Ticker
===================

Parity Stock Ticker is a simple console application that displays the best
bids and offers (BBOs) and latest trades in Parity Trading System.


Usage
-----

Run Parity Stock Ticker with Java:

```
java -jar parity-ticker.jar
```

The application can either listen to a live market data feed or to read a
historical market data file.

To listen to a live market data feed, pass a configuration file:

```
java -jar parity-ticker.jar [-t] <configuration-file>
```

The command line options are as follows:

- `-t`: Format the output as [TAQ][].

  [TAQ]: ../../libraries/file/doc/TAQ.md

The command line arguments are as follows:

- `<configuration-file>`: The configuration file. The configuration file
  specifies how to connect to the trading system.

When listening to a live market data feed, the application first replays
market events that have taken place so far. Then it proceeds to display
market events in real time.

To read a historical market data file, pass an input file:

```
java -jar parity-ticker.jar [-t] <input-file> [<instrument> ...]
```

The command line options are as follows:

- `-t`: Format the output as [TAQ][].

The command line arguments are as follows:

- `<input-file>`: The input file. The application reads market events from
  the input file in the NASDAQ BinaryFILE format.

- `[<instrument> ...]`: Zero or more instruments.


Configuration
-------------

Parity Stock Ticker uses a configuration file to specify how to connect to the
trading system. It supports two transport options: NASDAQ MoldUDP64 and NASDAQ
SoupBinTCP.

The following configuration parameters are required when using the MoldUDP64
transport:

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

# A list of zero or more instruments.
instruments = [ FOO, BAR, BAZ ]
```

The following configuration parameters are required when using the SoupBinTCP
transport:

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

# A list of zero or more instruments.
instruments = [ FOO, BAR, BAZ ]
```

See the `etc` directory for example configuration files.


License
-------

Released under the Apache License, Version 2.0.
