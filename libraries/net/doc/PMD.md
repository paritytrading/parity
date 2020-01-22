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

Prices are represented in terms of the minimum price increment.

Quantities are represented in terms of the minimum quantity increment.

Timestamps are represented as nanoseconds since the midnight of the day on
which the trading session started.

## Messages

All messages are sent by the trading system to the client application.

### Version

A Version message indicates the protocol version. The trading system sends a
Version message as the first message in a trading session.

Name         | Length | Type   | Notes
-------------|--------|--------|------
Message Type |      1 | Text   | `V`
Version      |      4 | Number | 2

### Order Added

An Order Added message indicates that an order has been added to the order
book. The order number is the trading system's identifier for the order.

Name         | Length | Type   | Notes
-------------|--------|--------|----------
Message Type |      1 | Text   | `A`
Timestamp    |      8 | Number |
Order Number |      8 | Number |
Side         |      1 | Text   | See below
Instrument   |      8 | Text   |
Quantity     |      8 | Number |
Price        |      8 | Number |

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
Timestamp    |      8 | Number |
Order Number |      8 | Number |
Quantity     |      8 | Number |
Match Number |      4 | Number |

### Order Canceled

An Order Canceled message indicates that an order has been canceled in part
or fully.

Name              | Length | Type   | Notes
------------------|--------|--------|------
Message Type      |      1 | Text   | `X`
Timestamp         |      8 | Number |
Order Number      |      8 | Number |
Canceled Quantity |      8 | Number |

## History

- **Version 2.** This version contains the following changes:
  - Remove the Seconds message
  - Remove the Order Deleted message
  - Remove the Broken Trade message
  - Represent prices using the minimum price increment
  - Represent quantities using the minimum quantity increment
  - Represent prices using 8 bytes
  - Represent quantities using 8 bytes
  - Represent timestamps using 8 bytes
- **Version 1.** Initial version.
