This application is **not** an RTI! It only serves as a socket to transfer NETN objects and interactions between federates, using the same interfaces as an RTI.

# NETN BUS

This is a test repo that tests the validity of a Fom based message bus, transfering NETN objects and interactions between federates, following the HLA structure. It does not enforce HLA standards, where it only makes sure that the correct messages are send to the according federates. More advanced HLA feature require an RTI.

The NETN Bus should only care for FOM version, e.g. NETN4 or something else. It should not be interested in lrc version etc...

The Bus works by setting up a TCP server that, to which the federates connect to as clients

NetnBusApplication (TCP Server)
        ↑
        | TCP connections
        ↓
testfederate (TCP Client)  ← connects via NetnBusOORTIambassador → NetnBusSocketClient

Flow:

NetnBusApplication starts → creates NetnBusSocketServer listening on TCP port 4567
testfederate starts → creates NetnBusOORTIambassador
testfederate calls oortiamb.connect(this, CallbackModel.HLA_EVOKED)
NetnBusOORTIambassador internally creates NetnBusSocketClient
NetnBusSocketClient opens a TCP socket: new Socket("localhost", 4567)
Server's ServerSocket.accept() receives the connection
Server spawns a ClientHandler thread for that client
Federate and server now communicate bidirectionally over TCP

This repo separates communication between the servers and the client using the TCP connection, no direct JVMConnection is available

This repo has been inspired heavily by the OpenSource RTI implementation Portico

Current system has been developed with 1515e in mind