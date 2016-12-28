Parity
======

Parity is an open source software platform for trading venues. It can be
used to run a financial marketplace, develop algorithmic trading agents,
or research market microstructure.

Parity requires Java Runtime Environment (JRE) 8 or newer.


Download
--------

See the [latest release][] on GitHub.

  [latest release]: https://github.com/paritytrading/parity/releases/latest


Modules
-------

Parity contains the following applications:

- [**Parity Trading System**](applications/system) is a server application for
  running a financial exchange.

- [**Parity FIX Gateway**](applications/fix) is a server application that adds
  Financial Information Exchange (FIX) support to the trading system.

- [**Parity Terminal Client**](applications/client) is a simple console
  application for entering orders into the trading system.

- [**Parity Stock Ticker**](applications/ticker) is a simple console
  application that displays the best prices and latest trades in the trading
  system.

- [**Parity Trade Reporter**](applications/reporter) is a simple console
  application that displays all occurred trades in the trading system.

See the [Wiki][] for additional applications.

  [Wiki]: https://github.com/paritytrading/parity/wiki

Parity contains the following libraries:

- [**Parity Order Book**](libraries/book) implements high-performance order
  book reconstruction on the JVM.

- [**Parity Network Protocols**](libraries/net) contains protocol
  specifications and reference implementations for network protocols used by
  the trading system.

- [**Parity File Formats**](libraries/file) specifies and implements file
  formats used by the trading system.

- [**Parity Matching Engine**](libraries/match) implements a high-performance
  matching engine for the JVM.

- [**Parity Utilities**](libraries/util) contains support functions used by
  the trading system.

Parity contains the following test applications:

- [**Parity Order Book Performance Test**](tests/book-perf-test) contains
  microbenchmarks for the order book reconstruction.

- [**Parity Matching Engine Performance Test**](tests/match-perf-test)
  contains microbenchmarks for the matching engine.


Build
-----

Build Parity with Maven:

```
mvn package
```


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
