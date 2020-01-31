# Profiling and adaptation of ParityTrading Parity for Transactive Energy
This repository contains modified code based on [ParityTrading Parity](https://github.com/paritytrading/parity). Thanks to those authors for key elements of a clean architectural solution for a local micromarket and microgrid implementation.

This project is part of The Energy Mashup Lab's [NIST-CTS-Agents](https://github.com/EnergyMashupLab/NIST-CTS-Agents) which uses the Common Transactive Services for market participation. See that project's project architecture and description. This code is used in the open source implementation of a Local Market Engine (LME).

The Common Transactive Services implementation and this code, in addition to [ParityTrading Parity](https://github.com/paritytrading/parity), is under the Apache 2.0 License.

For a description of the Parity project, see the README from [ParityTrading Parity](https://github.com/paritytrading/parity).

# Modifications to the base code
We adapt two key libraries from Parity, along with code on which they depend, to build an independent actor implementing a micromarket:

- [**Parity Order Book**](libraries/book) implements high-performance order
  book reconstruction on the JVM.

- [**Parity Matching Algorithm**](libraries/match) implements the matching
  algorithm used by the trading system.
  
  Code on which these profiled libraries depend includes

- [**Parity Utilities**](libraries/util) contains support functions used by
  the trading system.
  
 - [[**fastutil**](https://github.com/vigna/fastutil) which extends the Java™ Collections Framework by providing type-specific maps, sets, lists and queues with a small memory footprint and fast access and insertion.
  
  We use primarily the profiled Java classes implementing the order book and its contents, together with the matching algorithms;
  - public class OrderBook and Order
  - public class Market

As this project evolves, we will continue to remove (profile out) things that aren't needed for the Common Transactive Services project. For example, networking is provided by the CTS project using standard RESTful microservices as provided by the [Spring Framework](https://spring.io/) and Spring Boot.

## Background
The concept of micromarkets and the flexibility of structure for energy systems was developed by William Cox and Toby Considine. See [ResearchGate](https://www.researchgate.net/project/Common-Transactive-Services-for-Smart-Energy) and [Cox and Considine publications](http://coxsoftwarearchitects.com/Pages/C_New.html) for references. The most referenced paper is Understanding Microgrids as the Essential Architecture of Smart Energy. 

Applications include local balancing of supply and demand, and dynamic power grid restructuring for fault resilience.

## License

Copyright 2014 Parity authors.
Copyright 2019-2020 The Energy Mashup Lab.

Released under the Apache License, Version 2.0. See `LICENSE.txt` for details.
