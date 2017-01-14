TAQ
===

TAQ is one of Parity's historical market data file formats. It consists of the
best bids and offers (BBOs) and trades.


Architecture
------------

TAQ uses the tab-separated values (TSV) file format with ASCII encoding.

A TAQ file consists of a header and zero or more records. The header and each
record consist of a fixed number of fields. One field, Record Type, specifies
which fields are present in a record. Fields that are not present in a record
are empty.


Data Types
----------

Dates are represented as `YYYY-MM-DD` (ISO 8601).

Timestamps are represented as `HH:MM:SS.SSS` (ISO 8601).

Prices and sizes are represented either as integers or as decimal numbers
using decimal point.


Records
-------

A record consists of the fields enumerated below.

Name        | Presence | Notes
------------|----------|------------------------------------------------
Date        | `Q`, `T` |
Timestamp   | `Q`, `T` |
Instrument  | `Q`, `T` |
Record Type | `Q`, `T` | See below
Bid Price   | `Q`      | Empty if not available
Bid Size    | `Q`      | Empty if not available
Ask Price   | `Q`      | Empty if not available
Ask Size    | `Q`      | Empty if not available
Trade Price | `T`      |
Trade Size  | `T`      |
Trade Side  | `T`      | Refers to resting order, empty if not available

The record types are enumerated below.

Record Type | Description
------------|------------
`Q`         | Quote
`T`         | Trade


History
-------

- **Version 1.** Initial version.
