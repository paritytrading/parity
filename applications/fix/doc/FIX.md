# FIX

FIX is an alternative order entry protocol for Parity.

## Architecture

FIX is based on the Financial Information Exchange (FIX) protocol version
FIX 4.4. It is terminated at a FIX gateway, which is located between the
client application and the trading system.

## Common Definitions

These definitions apply to both inbound and outbound messages.

### Fields

Account(1) is optional. If it is present in a New Order Single message,
it will be present in subsequent Execution Report and Order Cancel Reject
messages.

ClOrdID(11) has a maximum length of 16 characters.

### Message Header

All messages start with the message header.

Tag                  | Req'd | Description
---------------------|-------|------------
BeginString(8)       |   Y   | FIX.4.4
BodyLength(9)        |   Y   |
MsgType(35)          |   Y   |
SenderCompID(49)     |   Y   |
TargetCompID(56)     |   Y   |
MsgSeqNum(34)        |   Y   |
PossDupFlag(43)      |   N   | When needed
PossResend(97)       |   N   | When needed
SendingTime(52)      |   Y   |
OrigSendingTime(122) |   N   | When needed

### Message Trailer

All messages end with the message trailer.

Tag          | Req'd | Description
-------------|-------|------------
CheckSum(10) |   Y   |

### Administrative Messages

Refer to the FIX 4.4 protocol specification regarding the following
administrative messages:

  - Heartbeat
  - Test Request
  - Resend Request
  - Reject
  - Sequence Reset

## Inbound Messages

Inbound messages are sent by the client application to the FIX gateway.

### Initiate Session

Initiate a session with a Logon message.

Tag               | Req'd | Description
------------------|-------|------------------------
_Message Header_  |   Y   | MsgType(35) = A (Logon)
EncryptMethod(98) |   Y   | 0 (None)
HeartBtInt(108)   |   Y   | 30
Username(553)     |   Y   |
Password(554)     |   Y   |
_Message Trailer_ |   Y   |

### Terminate Session

Terminate a session with a Logout message.

Tag               | Req'd | Description
------------------|-------|-------------------------
_Message Header_  |   Y   | MsgType(35) = 5 (Logout)
_Message Trailer_ |   Y   |

### Enter Order

Enter an order with a New Order Single message.

Tag               | Req'd | Description
------------------|-------|-----------------------------------
_Message Header_  |   Y   | MsgType(35) = D (New Order Single)
ClOrdID(11)       |   Y   | Unique identifier for the order
Account(1)        |   N   |
Symbol(55)        |   Y   |
Side(54)          |   Y   | See below
TransactTime(60)  |   Y   |
OrderQty(38)      |   Y   |
OrdType(40)       |   Y   | 2 (Limit)
Price(44)         |   Y   |
_Message Trailer_ |   Y   |

The sides are enumerated below.

Side(54) | Description
---------|------------
1        | Buy
2        | Sell

### Cancel Order Partially

Cancel an order partially with an Order Cancel/Replace Request message.

Tag               | Req'd | Description
------------------|-------|-----------------------------------------------
_Message Header_  |   Y   | MsgType(35) = G (Order Cancel/Replace Request)
OrigClOrdID(41)   |   Y   | Must match a previous non-rejected order
ClOrdID(11)       |   Y   | Unique identifier for the order
Account(1)        |   N   |
Side(54)          |   Y   | Must match the original order
TransactTime(60)  |   Y   |
OrderQty(38)      |   Y   |
OrdType(40)       |   Y   | Must match the original order
_Message Trailer_ |   Y   |

### Cancel Order Fully

Cancel an order fully with an Order Cancel Request message.

Tag               | Req'd | Description
------------------|-------|-----------------------------------------
_Message Header_  |   Y   | MsgType(35) = F (Order Cancel Request)
OrigClOrdID(41)   |   Y   | Must match a previous non-rejected order
ClOrdID(11)       |   Y   | Unique identifier for the order
Account(1)        |   N   |
Side(54)          |   Y   | Must match the original order
TransactTime(60)  |   Y   |
_Message Trailer_ |   Y   |

## Outbound Messages

Outbound messages are sent by the FIX gateway to the client application.

### Order Accepted

An order is accepted with an Execution Report message with ExecType(151) of
0 (New). OrderID(37) contains the trading system's identifier for the order.

Tag               | Req'd | Description
------------------|-------|-----------------------------------
_Message Header_  |   Y   | MsgType(35) = 8 (Execution Report)
OrderID(37)       |   Y   |
ClOrdID(11)       |   Y   |
ExecID(17)        |   Y   |
ExecType(150)     |   Y   | 0 (New)
OrdStatus(39      |   Y   | 0 (New)
Account(1)        |   N   |
Symbol(55)        |   Y   |
Side(54)          |   Y   |
OrderQty(38)      |   Y   |
LeavesQty(151)    |   Y   |
CumQty(14)        |   Y   |
AvgPx(6)          |   Y   |
_Message Trailer_ |   Y   |

### Order Rejected

An order is rejected with an Execution Report message with ExecType(151) of
8 (Rejected).

Tag               | Req'd | Description
------------------|-------|-----------------------------------
_Message Header_  |   Y   | MsgType(35) = 8 (Execution Report)
OrderID(37)       |   Y   |
ClOrdID(11)       |   Y   |
ExecID(17)        |   Y   |
ExecType(150)     |   Y   | 8 (Rejected)
OrdStatus(39)     |   Y   | 8 (Rejected)
OrdRejReason(103) |   Y   | See below
Account(1)        |   N   |
Symbol(55)        |   Y   |
Side(54)          |   Y   |
OrderQty(38)      |   Y   |
LeavesQty(151)    |   Y   |
CumQty(14)        |   Y   |
AvgPx(6)          |   Y   |
_Message Trailer_ |   Y   |

The reasons for order rejection are enumerated below.

OrdRejReason(103) | Description
------------------|-----------------------
0                 | Broker/exchange option
1                 | Unknown symbol
6                 | Duplicate order
13                | Incorrect quantity

### Order Executed

An order execution is indicated with an Execution Report message with
ExecType(150) of F (Trade).

Tag               | Req'd | Description
------------------|-------|-----------------------------------
_Message Header_  |   Y   | MsgType(35) = 8 (Execution Report)
OrderID(37)       |   Y   |
ClOrdID(11)       |   Y   |
ExecID(17)        |   Y   |
ExecType(150)     |   Y   | F (Trade)
OrdStatus(39)     |   Y   |
Account(1)        |   N   |
Symbol(55)        |   Y   |
Side(54)          |   Y   |
OrderQty(38)      |   Y   |
LastQty(32)       |   Y   |
LastPx(31)        |   Y   |
LeavesQty(151)    |   Y   |
CumQty(14)        |   Y   |
AvgPx(6)          |   Y   |
_Message Trailer_ |   Y   |

### Order Cancel Acknowledgement

An order cancellation is immediately acknowledged with an Execution Report
message with ExecType(150) of 6 (Pending cancel) or E (Pending replace).

Tag               | Req'd | Description
------------------|-------|-----------------------------------
_Message Header_  |   Y   | MsgType(35) = 8 (Execution Report)
OrderID(37)       |   Y   |
ClOrdID(11)       |   Y   |
OrigClOrdID(41)   |   Y   |
ExecID(17)        |   Y   |
ExecType(150)     |   Y   | See below
OrdStatus(39)     |   Y   |
Account(1)        |   N   |
Symbol(55)        |   Y   |
Side(54)          |   Y   |
OrderQty(38)      |   Y   |
LeavesQty(151)    |   Y   |
CumQty(14)        |   Y   |
AvgPx(6)          |   Y   |
_Message Trailer_ |   Y   |

The execution types for order cancellation acknowledgement are enumerated
below.

ExecType(150) | Description
--------------|----------------
6             | Pending cancel
E             | Pending replace

### Order Canceled

An order cancellation is indicated with an Execution Report message with
ExecType(150) of 4 (Canceled) or 5 (Replaced).

Tag               | Req'd | Description
------------------|-------|-----------------------------------
_Message Header_  |   Y   | MsgType(35) = 8 (Execution Report)
OrderID(37)       |   Y   |
ClOrdID(11)       |   Y   |
OrigClOrdID(41)   |   Y   |
ExecID(17)        |   Y   |
ExecType(150)     |   Y   | See below
OrdStatus(39)     |   Y   |
Account(1)        |   N   |
Symbol(55)        |   Y   |
Side(54)          |   Y   |
OrderQty(38)      |   Y   |
LeavesQty(151)    |   Y   |
CumQty(14)        |   Y   |
AvgPx(6)          |   Y   |
_Message Trailer_ |   Y   |

The execution types for order cancellation are enumerated below.

ExecType(150) | Description
--------------|------------
4             | Canceled
5             | Replaced

### Order Cancel Rejected

An order cancel rejection is indicated with an Order Cancel Reject message.

Tag                   | Req'd | Description
----------------------|-------|--------------------------------------
_Message Header_      |   Y   | MsgType(35) = 9 (Order Cancel Reject)
OrderID(37)           |   Y   |
ClOrdID(11)           |   Y   |
OrigClOrdID(41)       |   Y   |
OrdStatus(39)         |   Y   |
Account(1)            |   N   |
CxlRejResponseTo(434) |   Y   | See below
CxlRejReason(102)     |   Y   | See below
_Message Trailer_     |   Y   |

Types of requests that the Order Cancel Reject message is in response to are
enumerated below.

CxlRejResponseTo(434) | Description
----------------------|-----------------------------
1                     | Order Cancel Request
2                     | Order Cancel/Replace Request

The reasons for order cancel rejection are enumerated below.

CxlRejReason(102) | Description
------------------|----------------------------------------------------------
0                 | Too late to cancel
1                 | Unknown order
3                 | Order already in Pending Cancel or Pending Replace status
6                 | Duplicate ClOrdID(11)

## History

- **Version 1.** Initial version.
