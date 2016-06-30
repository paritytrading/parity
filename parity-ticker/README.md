Parity Stock Ticker
===================

Parity Stock Ticker is a simple console application that displays the best
bids and offers (BBOs) and latest trades in Parity Trading System.


Usage
-----

Run Parity Stock Ticker with Java:

    java -jar <executable> <command>

To listen to a live market data feed, use the `listen` command:

    java -jar <executable> listen [-t] <configuration-file>

When listening to a live market data feed, the stock ticker first replays
market events that have taken place before it started. Then it proceeds to
display market events in real time.

To read a historical market data file, use the `read` command:

    java -jar <executable> read [-t] <input-file> [<instrument> ...]

By default, the stock ticker formats its output for display. If the `-t`
option is given, it formats the output as [TAQ][] instead.

  [TAQ]: ../parity-file/doc/TAQ.md


License
-------

Parity Stock Ticker is released under the Apache License, Version 2.0.
