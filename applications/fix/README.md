Parity FIX Gateway
==================

Parity FIX Gateway adds a Financial Information Exchange (FIX) interface to
Parity Trading System.


Protocols
---------

Parity FIX Gateway specifies and implements the following protocols:

- [**FIX**](doc/FIX.md): an alternative order entry protocol


Usage
-----

Run Parity FIX Gateway with Java:

    java -jar parity-fix.jar <configuration-file>

The command line arguments are as follows:

- `<configuration-file>`: A configuration file. The configuration specifies
  how to connect to the trading system and accept FIX connections.

Once started, the application starts listening for FIX connections initiated
by market participants.


Configuration
-------------

Parity FIX Gateway uses a configuration file to specify how to connect to the
trading system and accept FIX connections.

The following configuration parameters are required:

    fix {

        # The local IP address for the FIX acceptor.
        address = 0.0.0.0

        # The local TCP port for the FIX acceptor.
        port = 4010

        # SenderCompID(49) for the FIX acceptor.
        sender-comp-id = parity

    }

    order-entry {

        # The IP address of the trading system.
        address = 127.0.0.1

        # The TCP port for order entry at the trading system.
        port = 4000

    }

See the `etc` directory for an example configuration file.


License
-------

Released under Apache License, Version 2.0.