This folder contains files concerning the client (federate). Important files are:

RtiAmbassador   - API that the federate uses to communicate with the client ecosystem (entrypoint)
ClientContext   - Keeps track of the state of the client, containing all methods
ClientSocket    - IO configuration for the client to communicate to the TCP server
ClientReceiver  - Class for the client to receive communication from the TCP server
ClientSender    - Class for the client to send communication to the TCP server

Structure:
The Ambassador class receives calls from the federate, these calls trigger events/methods within the client context. The client context is able to send requests to the TCP server using the clientSender. The clientSender will create futures after sending a message, where the clientSender awaits a respone. In a separate thread, the clientReceiver is listening for responses from the TCP server, where it will route any traffic to the clientContext. The clientReceiver triggers methods or events that change the state of the clientContext based on the messages it has received. These methods are able to resolve the earlier created futures, completing or throwing errors based on the response message. Whether the events/methods have completed (succesfully or exceptionally), the previously created futures are resolved, where the condition (succes or exception) is communicated back the clientSender. Since the clientSender has been called by the Ambassador, the conditions are communicated back to the federate through the Ambassador (method finished succesfully / method failed). In other words: The clientSender and clientReceiver are transport layers that send, receive and route messages to(/from) the client context *only*. The client context therefore decides what should happen with incomming calls, or updates from both the TCP server as the federate, where it is able to route the information using the other transport classes.

TODO:
Many request do not really need a response, is it fire and forget?