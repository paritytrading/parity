# Parity Network Protocols

Parity Network Protocols specifies and implements network protocols used by
the trading system.

## Features

Parity Network Protocols specifies and implements the following protocols:

- [**POE**](doc/POE.md): the native order entry protocol, used by market
  participants to enter orders into the trading system.

- [**PMD**](doc/PMD.md): the native market data protocol, used by all
  parties interested in the state of the market.

- [**PMR**](doc/PMR.md): the native market reporting protocol, used by
  market surveillance systems and post-trade processing systems.

Parity Network Protocols uses [Nassau][] for NASDAQ transport protocol
support.

  [Nassau]: https://github.com/paritytrading/nassau

## Dependencies

Parity Network Protocols depends on the following libraries:

- [Foundation][] 0.2.1
- [Nassau][] Core 0.13.0

  [Foundation]: https://github.com/paritytrading/foundation

## Download

Add a Maven dependency to Parity Network Protocols:

```xml
<dependency>
  <groupId>com.paritytrading.parity</groupId>
  <artifactId>parity-net</artifactId>
  <version><!-- latest release --></version>
</dependency>
```

See the [latest release][] on GitHub.

  [latest release]: https://github.com/paritytrading/parity/releases/latest

## License

Released under the Apache License, Version 2.0.
