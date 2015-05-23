Parity
======

Parity is an open source software platform for trading venues. It can be
used to run a financial marketplace, develop algorithmic trading agents,
or research market microstructure.

Parity requires Java Runtime Environment (JRE) 8 or newer.


Download
--------

Download Parity from the [Releases][] page.

  [Releases]: https://github.com/jvirtanen/parity/wiki/Releases


Usage
-----

Parity consists of four applications:

  - [**Parity Trading System**](parity-system) is a server application for
    running a trading venue.

  - [**Parity Terminal Client**](parity-client) is a simple console application
    for entering orders into the trading system.

  - [**Parity Stock Ticker**](parity-ticker) is a simple console application
    that displays the best bids and offers (BBOs) and latest trades in the
    trading system.

  - [**Parity Trade Reporter**](parity-reporter) is a simple console
    application that displays occurred trades in the trading system.

The trading system uses two network protocols for communication with market
participants:

  - [**POE**](parity-net/doc/POE.md) is the native order entry protocol. The
    terminal client is an example of an application that uses it to enter
    orders.

  - [**PMD**](parity-net/doc/PMD.md) is the native market data protocol. The
    stock ticker is an example of an application that consumes market data
    using it.

In addition, the trading system uses one network protocol for communication
with post-trade processing and market surveillance systems:

  - [**PMR**](parity-net/doc/PMR.md) is the native market reporting protocol.
    The trade reporter is an example of an application that listens to trade
    reports using it.

See the [Connectivity][] page for a list of implementations of the network
protocols.

  [Connectivity]: https://github.com/jvirtanen/parity/wiki/Connectivity

Historical market data from the trading system can be obtained in two formats:

  - [**TAQ**](parity-file/doc/TAQ.md) is a historical market data file format
    that consists of the best bids and offers (BBOs) and trades. The stock
    ticker can produce TAQ files.

  - [**PMD**](parity-net/doc/PMD.md) consists of all market events. Entire
    market data sessions can be recorded into files for further processing.


Development
-----------

See the [Developer Guide](HACKING.md).


License
-------

Parity is released under the Apache License, Version 2.0. See `LICENSE` for
details.
