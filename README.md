Parity
======

Parity is an open source software platform for trading venues. It can be
used to run a financial marketplace, develop algorithmic trading agents,
or research market microstructure.

Parity requires Java Runtime Environment (JRE) 8 or newer.


Features
--------

Parity consists of the following applications:

  - [**Parity Trading System**](parity-system) is a server application for
    running a trading venue.

  - [**Parity Terminal Client**](parity-client) is a simple console application
    for entering orders into the trading system.

  - [**Parity Stock Ticker**](parity-ticker) is a simple console application
    that displays the best bids and offers (BBOs) and latest trades in the
    trading system.

  - [**Parity Trade Reporter**](parity-reporter) is a simple console
    application that displays occurred trades in the trading system.

  - [**Parity FIX Gateway**](parity-fix) adds Financial Information Exchange
    (FIX) interface to the trading system.

In addition, Parity contains the following libraries:

  - [**Parity File Formats**](parity-file) contains file format specifications
    and reference implementations for file formats used by the trading system.

  - [**Parity Network Protocols**](parity-net) contains protocol specifications
    and reference implementations for network protocols used by the trading
    system.

  - [**Parity Top of Book**](parity-top) implements high-performance order book
    reconstruction on the JVM.

See the [Wiki][] for additional applications.

  [Wiki]: https://github.com/jvirtanen/parity/wiki


Build
-----

See the [Developer Guide](HACKING.md).


License
-------

Parity is released under the Apache License, Version 2.0. See `LICENSE` for
details.
