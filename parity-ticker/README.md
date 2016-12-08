Parity Stock Ticker
===================

Parity Stock Ticker is a simple console application that displays the best
bids and offers (BBOs) and latest trades in Parity Trading System.


Usage
-----

Run Parity Stock Ticker with Java:

    java -jar <executable>

To listen to a live market data feed, pass a configuration file:

    java -jar <executable> [-t] <configuration-file>

When listening to a live market data feed, the stock ticker first replays
market events that have taken place before it started. Then it proceeds to
display market events in real time.

To read a historical market data file, pass an input file:

    java -jar <executable> [-t] <input-file> [<instrument> ...]

By default, the stock ticker formats its output for display. If the `-t`
option is given, it formats the output as [TAQ][] instead.

  [TAQ]: ../libraries/file/doc/TAQ.md


License
-------

Released under the Apache License, Version 2.0.
