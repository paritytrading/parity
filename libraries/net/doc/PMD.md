# PMD

PMD is Parity's native market data protocol. With it market participants can
maintain a view of the state of the market.

## Architecture

PMD consists of logical, sequenced messages sent by the trading system to the
client application.

PMD uses NASDAQ MoldUDP64 1.00 as the underlying transport protocol. It
implements ordered, reliable IP multicast.

## Data Types

Number fields are encoded as unsigned integers in network byte order.

Text fields are encoded as ASCII, left-justified and padded on the right with
the space character.

Prices have decimal fixed-point representation with a six-digit integral part
and a four-digit fractional part.

Timestamps are represented as nanoseconds since the beginning of the current
second.

## Messages

All messages are sent by the trading system to the client application.

### Version

A Version message indicates the protocol version. The trading system sends a
Version message as the first message in a trading session.

Name         | Length | Type   | Notes
-------------|--------|--------|------
Message Type |      1 | Text   | `V`
Version      |      4 | Number | 1

### Seconds

A Seconds message indicates the number of seconds since the midnight of the
day on which the trading session started. The trading system sends a Seconds
message for every second on which it sends at least one other message.

Name         | Length | Type   | Notes
-------------|--------|--------|------
Message Type |      1 | Text   | `S`
Second       |      4 | Number |

### Order Added

An Order Added message indicates that an order has been added to the order
book. The order number is the trading system's identifier for the order.

Name         | Length | Type   | Notes
-------------|--------|--------|----------
Message Type |      1 | Text   | `A`
Timestamp    |      4 | Number |
Order Number |      8 | Number |
Side         |      1 | Text   | See below
Instrument   |      8 | Text   |
Quantity     |      4 | Number |
Price        |      4 | Number |

The sides are enumerated below.

Side | Description
-----|------------
`B`  | Buy
`S`  | Sell

### Order Executed

An Order Executed message indicates that an order has been executed in part
or fully. The match number is the trading system's identifier for the trade.

Name         | Length | Type   | Notes
-------------|--------|--------|------
Message Type |      1 | Text   | `E`
Timestamp    |      4 | Number |
Order Number |      8 | Number |
Quantity     |      4 | Number |
Match Number |      4 | Number |

### Order Canceled

An Order Canceled message indicates that an order has been canceled in part
or fully.

Name              | Length | Type   | Notes
------------------|--------|--------|------
Message Type      |      1 | Text   | `X`
Timestamp         |      4 | Number |
Order Number      |      8 | Number |
Canceled Quantity |      4 | Number |

### Order Deleted

An Order Deleted message indicates that an order has been canceled fully.

Name         | Length | Type   | Notes
-------------|--------|--------|------
Message Type |      1 | Text   | `D`
Timestamp    |      4 | Number |
Order Number |      8 | Number |

## History

- **Version 1.** Initial version.
