Parity
======

Parity is an open source software platform for trading venues. It can be
used to run a financial marketplace, develop algorithmic trading agents,
or research market microstructure.

Parity requires Java Runtime Environment (JRE) 8 or newer.


Features
--------

Parity consists of the following applications:

- [**Parity Trading System**](applications/system) is a server application for
  running a trading venue.

- [**Parity Terminal Client**](applications/client) is a simple console
  application for entering orders into the trading system.

- [**Parity Stock Ticker**](parity-ticker) is a simple console application
  that displays the best bids and offers (BBOs) and latest trades in the
  trading system.

- [**Parity Trade Reporter**](applications/reporter) is a simple console
  application that displays occurred trades in the trading system.

- [**Parity FIX Gateway**](applications/fix) adds Financial Information
  Exchange (FIX) interface to the trading system.

- [**Parity Matching Engine Performance Test**](tests/match-perf-test)
  contains microbenchmarks for the matching engine.

- [**Parity Order Book Performance Test**](tests/book-perf-test) contains
  microbenchmarks for the order book reconstruction.

In addition, Parity contains the following libraries:

- [**Parity File Formats**](libraries/file) contains file format
  specifications and reference implementations for file formats used by the
  trading system.

- [**Parity Network Protocols**](libraries/net) contains protocol
  specifications and reference implementations for network protocols used by
  the trading system.

- [**Parity Order Book**](libraries/book) implements high-performance order
  book reconstruction on the JVM.

- [**Parity Utilities**](parity-util) contains support functions used by the
  trading system.

- [**Parity Matching Engine**](libraries/match) implements a high-performance
  matching engine for the JVM.

See the [Wiki][] for additional applications.

  [Wiki]: https://github.com/paritytrading/parity/wiki


Links
-----

For more information on Parity:

- See [Parity Guide](https://github.com/paritytrading/documentation) for the
  user and developer documentation
- Follow [@paritytrading](https://twitter.com/paritytrading) on Twitter for
  news and announcements
- Join [paritytrading/chat](https://gitter.im/paritytrading/chat) on Gitter
  for discussions


License
-------

Copyright 2014 Jussi Virtanen and contributors.

Released under the Apache License, Version 2.0. See `LICENSE.txt` for details.
