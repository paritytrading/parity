Parity Network Protocols
========================

Parity Network Protocols contains protocol specifications and reference
implementations for network protocols used by Parity Trading System.


Features
--------

Parity Network Protocols specifies and implements the following protocols:

  - [**POE**](doc/POE.md): the native order entry protocol, used by market
    participants to enter orders into the trading system.

  - [**PMD**](doc/PMD.md): the native market data protocol, used by all
    parties interested in the state of the market.

  - [**PMR**](doc/PMR.md): the native market reporting protocol, used by
    market surveillance systems and post-trade processing systems.


Download
--------

Add a Maven dependency to Parity Network Protocols:

    <dependency>
      <groupId>org.jvirtanen.parity</groupId>
      <artifactId>parity-net</artifactId>
      <version><!-- latest version --></version>
    </dependency>


License
-------

Parity Network Protocols are released under the Apache License, Version 2.0.
