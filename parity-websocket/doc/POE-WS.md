POE-WS
======

POE-WS is an adaptation of [POE][] on WebSocket.

  [POE]: ../../parity-net/doc/POE.md


Inbound Messages
----------------

Inbound messages are sent by the client application to the WebSocket gateway.


### Login Request

This message corresponds to the Login Request packet in SoupBinTCP.

Attribute                 | Type   | Notes
--------------------------|--------|------
`messageType`             | String | `"l"`
`username`                | String |
`password`                | String |
`requestedSession`        | String |
`requestedSequenceNumber` | Number |


### Logout Request

This message corresponds to the Logout Request packet in SoupBinTCP.

Attribute     | Type   | Notes
--------------|--------|------
`messageType` | String | `"o"`


### Enter Order

This message corresponds to the Enter Order message in POE.

Attribute     | Type   | Notes
--------------|--------|-------------
`messageType` | String | `"E"`
`orderId`     | String |
`side`        | String | `"B"`, `"S"`
`instrument`  | String |
`quantity`    | Number |
`price`       | Number |


### Cancel Order

This message corresponds to the Cancel Order message in POE.

Attribute     | Type   | Notes
--------------|--------|------
`messageType` | String | `"X"`
`orderId`     | String |
`quantity`    | Number |


Outbound Messages
-----------------

Outbound messages are sent by the WebSocket gateway to the client application.


### Login Accepted

This message corresponds to the Login Accepted packet in SoupBinTCP.

Attribute        | Type   | Notes
-----------------|--------|------
`messageType`    | String | `"a"`
`session`        | String |
`sequenceNumber` | Number |


### Login Rejected

This message corresponds to the Login Rejected packet in SoupBinTCP.

Attribute          | Type   | Notes
-------------------|--------|-------------
`messageType`      | String | `"j"`
`rejectReasonCode` | String | `"A"`, `"S"`


### Order Accepted

This message corresponds to the Order Accepted message in POE.

Attribute     | Type   | Notes
--------------|--------|------
`messageType` | String | `"A"`
`timestamp`   | Number |
`orderId`     | String |
`side`        | String |
`instrument`  | String |
`quantity`    | Number |
`price`       | Number |
`orderNumber` | Number |


### Order Rejected 

This message corresponds to the Order Rejected message in POE.

Attribute     | Type   | Notes
--------------|--------|------
`messageType` | String | `"R"`
`timestamp`   | Number |
`orderId`     | String |
`reason`      | String | `"I"`


### Order Executed

This message corresponds to the Order Executed message in POE.

Attribute       | Type   | Notes
----------------|--------|-------------
`messageType`   | String | `"R"`
`timestamp`     | Number |
`orderId`       | String |
`quantity`      | Number |
`price`         | Number |
`liquidityFlag` | String | `"A"`, `"R"`
`matchNumber`   | Number |


### Order Canceled

This message corresponds to the Order Canceled message in POE.

Attribute          | Type   | Notes
-------------------|--------|-------------
`messageType`      | String | `"R"`
`timestamp`        | Number |
`orderId`          | String |
`canceledQuantity` | Number |
`reason`           | String | `"R"`, `"S"`


### Broken Trade

This message corresponds to the Broken Trade message in POE.

Attribute          | Type   | Notes
-------------------|--------|-------------
`messageType`      | String | `"R"`
`timestamp`        | Number |
`orderId`          | String |
`matchNumber`      | Number |
`reason`           | String | `"C"`, `"S"`
