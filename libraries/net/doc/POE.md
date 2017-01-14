POE
===

POE is Parity's native order entry protocol. With it market participants can
enter orders, cancel them and receive status updates on them.


Architecture
------------

POE consists of logical messages passed between a client application and a
trading system. Each message type has a fixed message length.

Messages from the trading system to the client application are sequenced. In
case of a network disconnection, the client application can request messages
starting from the last known sequence number.

Messages from the client application to the trading system can be resent
benignly. In case of a network disconnection, the client application can
resend messages to which it has not yet received acknowledgements.

POE uses NASDAQ SoupBinTCP 3.00 as the underlying transport protocol. The
protocol provides authentication and keep-alive as well as the aforementioned
message sequencing mechanism.


Data Types
----------

Number fields are encoded as unsigned integers in network byte order.

Text fields are encoded as ASCII, left-justified and padded on the right with
the space character.

Prices have decimal fixed-point representation with a six-digit integral part
and a four-digit fractional part.

Timestamps are represented as nanoseconds since the midnight of the day on
which the trading session started.


Inbound Messages
----------------

Inbound messages are sent by the client application to the trading system.


### Enter Order

Enter an order. The order identifier must be unique within the trading session.

Name         | Length | Type   | Notes
-------------|--------|--------|----------
Message Type |      1 | Text   | `E`
Order ID     |     16 | Text   |
Side         |      1 | Text   | See below
Instrument   |      8 | Text   |
Quantity     |      4 | Number |
Price        |      4 | Number |

The sides are enumerated below.

Side | Description
-----|------------
`B`  | Buy
`S`  | Sell


### Cancel Order

Cancel an order. The quantity refers to the new order size. To cancel an order
fully, set the quantity to zero.

Name         | Length | Type   | Notes
-------------|--------|--------|------
Message Type |      1 | Text   | `X`
Order ID     |     16 | Text   |
Quantity     |      4 | Number |


Outbound Messages
-----------------

Outbound messages are sent by the trading system to the client application.


### Order Accepted

An Order Accepted message is sent as a response to an Enter Order message to
acknowledge the entered order as accepted. The order number is the trading
system's identifier for the order.

Name         | Length | Type   | Notes
-------------|--------|--------|------
Message Type |      1 | Text   | `A`
Timestamp    |      8 | Number |
Order ID     |     16 | Text   |
Side         |      1 | Text   |
Instrument   |      8 | Text   |
Quantity     |      4 | Number |
Price        |      4 | Number |
Order Number |      8 | Number |


### Order Rejected

An Order Rejected message is sent as a response to an Enter Order message if
the order is not accepted.

Name         | Length | Type   | Notes
-------------|--------|--------|----------
Message Type |      1 | Text   | `R`
Timestamp    |      8 | Number |
Order ID     |     16 | Text   |
Reason       |      1 | Text   | See below

The reasons for a rejection are enumerated below.

Reason | Description
-------|-------------------
`I`    | Unknown instrument


### Order Executed

An Order Executed message indicates that an order has been executed in part or
fully. The match number is the trading system's identifier for the trade.

Name           | Length | Type   | Notes
---------------|--------|--------|------
Message Type   |      1 | Text   | `E`
Timestamp      |      8 | Number |
Order ID       |     16 | Text   |
Quantity       |      4 | Number |
Price          |      4 | Number |
Liquidity Flag |      1 | Text   |
Match Number   |      4 | Number |

The liquidity flags are enumerated below.

Liquidity Flag | Description
---------------|------------------
`A`            | Added liquidity
`R`            | Removed liquidity


### Order Canceled

An Order Canceled message indicates that an order has been canceled in part or
fully.

Name              | Length | Type   | Notes
------------------|--------|--------|----------
Message Type      |      1 | Text   | `X`
Timestamp         |      8 | Number |
Order ID          |     16 | Text   |
Canceled Quantity |      4 | Number |
Reason            |      1 | Text   | See below

The reasons for a cancellation are enumerated below.

Reason | Description
-------|------------
`R`    | Request
`S`    | Supervisory


### Broken Trade

A Broken Trade message indicates that a trade has been rendered void.

Name         | Length | Type   | Notes
-------------|--------|--------|----------
Message Type |      1 | Text   | `B`
Timestamp    |      8 | Number |
Order ID     |     16 | Text   |
Match Number |      4 | Number |
Reason       |      1 | Text   | See below

The reasons for a break are enumerated below.

Reason | Description
-------|------------
`C`    | Consent
`S`    | Supervisory


History
-------

- **Version 1.** Initial version.
