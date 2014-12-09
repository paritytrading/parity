Parity
======

Parity is an open source technology platform for trading venues.


Download
--------

Download Parity from the [Releases][] page.

  [Releases]: https://github.com/jvirtanen/parity/wiki/Releases


Usage
-----

Parity consists of three applications:

  - [**Parity Trading System**](parity-system) is an open source trading
    system.

  - [**Parity Terminal Client**](parity-client) is a simple console application
    for entering orders into the trading system.

  - [**Parity Stock Ticker**](parity-ticker) is a simple console application
    that displays the best bids and offers (BBOs) and latest trades in the
    trading system.

The trading system uses two network protocols for communication with market
participants:

  - [**POE**](parity-net/doc/POE.md) is the native order entry protocol. The
    terminal client is an example of an application that uses it to enter
    orders.

  - [**PMD**](parity-net/doc/PMD.md) is the native market data protocol. The
    stock ticker is an example of an application that consumes market data
    using it.

See the [Connectivity][] page for a list of implementations of the network
protocols.

  [Connectivity]: https://github.com/jvirtanen/parity/wiki/Connectivity


Build
-----

Build Parity with Maven:

    mvn package


License
-------

Parity is released under the Apache License, Version 2.0. See `LICENSE` for
details.
