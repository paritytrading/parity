Parity FIX Gateway
==================

Parity FIX Gateway adds a Financial Information Exchange (FIX) interface to
Parity Trading System.


Features
--------

Parity FIX Gateway specifies and implements the following protocols:

- [**FIX**](doc/FIX.md): an alternative order entry protocol

Parity FIX Gateway uses [Philadelphia][] for FIX protocol support.

  [Philadelphia]: https://github.com/paritytrading/philadelphia


Usage
-----

Run Parity FIX Gateway with Java:

    java -jar <executable> <configuration-file>

After starting, the gateway starts listening for FIX sessions initiated by
clients.


License
-------

Released under Apache License, Version 2.0.
