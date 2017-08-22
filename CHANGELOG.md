# Release Notes

## 0.7.0 (2017-08-23)

- Improve Parity Matching Engine performance
- Check quantity on order entry in Parity Trading System
- Use direct byte buffer
- Check price on order entry in Parity Trading System
- Upgrade to fastutil 8.1.0
- Remove market order support from Parity Matching Engine
- Remove Broken Trade message from POE protocol
- Remove Broken Trade message from PMD protocol
- Make price and size configurable per instrument in Parity File Formats
- Remove Seconds message from PMD protocol
- Remove Order Deleted message from PMD protocol
- Add Version message to PMR protocol
- Rename Order message to Order Entered message in PMR protocol
- Add Order Added message to PMR protocol
- Rename Cancel message to Order Canceled message in PMR protocol
- Clean up Trade message in PMR protocol
- Update price and quantity to 64 bits in POE protocol
- Update price and quantity to 64 bits in PMD protocol
- Update price and quantity to 64 bits in PMR protocol
- Update price and quantity representation in POE protocol
- Update price and quantity representation in PMD protocol
- Update price and quantity representation in PMR protocol
- Handle market data before order entry in Parity Trading System
- Make price and size format configurable in Parity Terminal Client
- Make price and size format configurable in Parity FIX Gateway
- Make price and size format configurable in Parity Trade Reporter
- Make price and size format configurable in Parity Stock Ticker
- Replace FOO with AAPL in example configuration files
- Replace BAR with ETH-BTC in example configuration files
- Replace BAZ with EUR-USD in example configuration files
- Improve FIX gateway performance
- Update POE protocol to version 2
- Update PMD protocol to version 2
- Update PMR protocol to version 2

## 0.6.0 (2017-01-20)

- Make FIX acceptor address configurable in Parity FIX Gateway
- Make order entry server address configurable in Parity Trading System
- Provide two example configuration files for Parity Trade Reporter
- Remove Parity Top of Book
- Add Parity Order Book
- Update example configuration files
- Improve Parity Matching Engine performance
- Add configuration for TAQ file format in Parity File Formats
- Simplify Parity Stock Ticker usage
- Fix pending cancel status in Parity FIX Gateway
- Make market data request server address configurable in Parity Trading
  System
- Make market reporting request server address configurable in Parity Trading
  System
- Remove Nassau dependency from Parity Utilities
- Remove Config dependency from Parity Utilities
- Improve Parity Network Protocols performance
- Improve Parity FIX Gateway performance
- Improve Parity Trading System performance
- Upgrade to fastutil 7.0.13
- Improve project structure
- Improve documentation
- Add portfolio script for Parity Trade Reporter
- Upgrade to Nassau 0.13.0

## 0.5.0 (2016-07-04)

- Upgrade to Philadelphia 0.4.0
- Fix order cancellation in Parity FIX Gateway
- Improve Parity Top of Book performance
- Rename order accessor in Parity Top of Book
- Provide two example configuration files for Parity Stock Ticker
- Add BinaryFILE reader
- Add BinaryFILE support to Parity Stock Ticker
- Clean up Parity Matching Engine Performance Test
- Add Parity Top of Book Performance Test
- Upgrade to Foundation 0.2.0

## 0.4.0 (2016-04-24)

- Add FIX protocol
- Add Parity FIX Gateway
- Fix PMD reference implementation
- Fix order cancellation in Parity Trading System
- Upgrade to Nassau 0.9.0
- Add support for order modification to Parity Top of Book
- Move to `com.paritytrading` namespace

## 0.3.0 (2015-12-25)

- Improve network protocol performance
- Upgrade to Nassau 0.6.0
- Improve MoldUDP64 client interface
- Fix PMR reference implementation
- Add cancel message to PMR protocol
- Add SoupBinTCP client
- Add SoupBinTCP support to Parity Stock Ticker
- Add SoupBinTCP support to Parity Trade Reporter
- Improve market data configuration in Parity Trading System
- Improve market reporting configuration in Parity Trading System

## 0.2.0 (2015-06-07)

- Upgrade to Java 8
- Add PMR protocol
- Add support for login to Parity Terminal Client
- Add Parity Trade Reporter
- Add support for execution with price to Parity Top of Book
- Add support for accessing orders to Parity Top of Book
- Add TAQ file format
- Add support for TAQ file format to Parity Stock Ticker
- Add support for setting multicast interface by name to Parity Stock Ticker
- Add support for running multiple instances of Parity Stock Ticker on one host
- Improve API documentation for Parity Matching Engine
- Improve API documentation for Parity Network Protocols
- Improve API documentation for Parity Top Of Book

## 0.1.0 (2015-01-06)

- Initial release
