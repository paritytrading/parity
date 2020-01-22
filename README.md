# Modifications to ParityTrading Parity for Transactive Energy
This repository contains modified code based on a clone from [ParityTrading Parity](https://github.com/paritytrading/parity). Thanks to those authors for key elements of a clean architectural solution to a local micromarket implementation.

The project in which this is used is The Energy Mashup Lab's [NIST-CTS-Agents](https://github.com/EnergyMashupLab/NIST-CTS-Agents) which uses the Common Transactive Services for market participation. See that project's project architecture and description. This code will be part of the open source implementation of a Local Market Engine (LME).

These modifications, as well as the base code, is under the Apache 2.0 License.

Base repository README from [ParityTrading Parity](https://github.com/paritytrading/parity):

# Parity

Parity is an open source software platform for trading venues. It can be
used to run a financial marketplace, develop algorithmic trading agents,
or research market microstructure.

Parity requires Java Runtime Environment (JRE) 8 or newer.

## Download

See the [latest release][] on GitHub.

  [latest release]: https://github.com/paritytrading/parity/releases/latest

## Modules

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

- [**Parity Network Protocols**](libraries/net) specifies and implements
  network protocols used by the trading system.

- [**Parity File Formats**](libraries/file) specifies and implements file
  formats used by the trading system.

- [**Parity Matching Algorithm**](libraries/match) implements the matching
  algorithm used by the trading system.

- [**Parity Utilities**](libraries/util) contains support functions used by
  the trading system.

Parity contains the following test applications:

- [**Parity Order Book Performance Test**](tests/book-perf-test) contains
  microbenchmarks for the order book reconstruction.

- [**Parity Matching Algorithm Performance Test**](tests/match-perf-test)
  contains microbenchmarks for the matching algorithm.

## Build

Build Parity with Maven:

```
mvn package
```

## Links

For more information on Parity:

- Follow [@paritytrading](https://twitter.com/paritytrading) on Twitter for
  news and announcements
- Join [paritytrading/chat](https://gitter.im/paritytrading/chat) on Gitter
  for discussions

## License

Copyright 2014 Parity authors.

Released under the Apache License, Version 2.0. See `LICENSE.txt` for details.
