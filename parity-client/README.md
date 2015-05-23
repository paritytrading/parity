Parity Terminal Client
======================

Parity Terminal Client is a simple console application for entering orders
into Parity Trading System.


Usage
-----

Run Parity Terminal Client with Java:

    java -jar <executable> <configuration-file>

When started, the terminal client displays a command prompt:

    Type 'help' for help.
    >

You can interact with the terminal client by entering commands into the command
prompt. For example, to enter a buy order, use the `buy` command:

    > buy 100 FOO 10.00

The command enters a buy order of 100 units of the instrument FOO at the limit
price of 10.00 into the trading system. Assuming that FOO can be traded on the
trading system and that there is no matching sell order in the order book, the
order will remain open. To list the open orders, use the `orders` command:

    > orders

Use the `help` command to see a list of all available commands.


Build
-----

See the [Developer Guide](../HACKING.md).


License
-------

Parity Terminal Client is released under the Apache License, Version 2.0.
