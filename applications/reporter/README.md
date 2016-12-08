Parity Trade Reporter
=====================

Parity Trade Reporter is a simple console application that displays occurred
trades in Parity Trading System.


Usage
-----

Run Parity Trade Reporter with Java:

    java -jar parity-reporter.jar [-t] <configuration-file>

After starting, the trade reporter first replays trades that have taken place
before it started. Then it proceeds to display trades in real time.

By default, the trade reporter formats its output for display. If the `-t`
option is given, it formats the output as tab-separated values (TSV) instead.


License
-------

Released under the Apache License, Version 2.0.
