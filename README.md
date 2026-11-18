LiteRTI - A lightweight HLA-compatible RTI based on the IEEE 1516e standard

==== WORK IN PROGRESS ====

This application is not a full-featured high performance HLA RTI. It uses a TCP server-centric architecture intended to simplify connectivity and reduce firewall related issues. LiteRTI uses TCP exclusively, meaning that all messages use the `HLAreliable` transportation type.

to be Supported Features
- HLA Object Management
- Reliable and Best-Effort transportation
- Receive Order (RO) message delivery

to be Not Supported Features
- Time Management (TSO)
- Ownership Management
- Data Distribution Management (DDM)

==== General Architecture ====

Current implementation involves a centralized TCP server that transfers the messages to the client federates. Current goal is to first implement a working centralized achitecture where TCP is to handle HLA Reliable and UDP Best-Effort messages using Receive Order delivery.

Current status is that federates can:
- Connect
- Create a federation with a supplied fom
- Join a federation
- Publish Objects
- Subscribe Objects
- Register Object Instances
(to be added more)

==== How to run ====

Run the LiteRtiApplication.java file

==== TODO ====
Optimize publish checks, currently they happen server side
Also currently publish does not improve networking