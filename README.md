LiteRTI - A lightweight HLA-compatible RTI based on the IEEE 1516e standard

==== WORK IN PROGRESS ====

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