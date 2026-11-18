# Client (federate)

This package connects a federate to the LiteRTI server and delivers server responses and federate callbacks.

## Components

| Component | Responsibility |
| --- | --- |
| [LiteRtiAmbassador](LiteRtiAmbassador.java) | API entry point used by the federate. Delegates RTI service calls to the client context. |
| [LiteRtiClientContext](LiteRtiClientContext.java) | Maintains client state and pending response futures, handles service results, and owns the callback dispatcher. |
| [LiteRtiClientSocket](LiteRtiClientSocket.java) | Establishes the TCP connection and starts the socket receiver. |
| [LiteRtiClientSender](LiteRtiClientSender.java) | Serializes outgoing requests and waits for responses when required. |
| [LiteRtiClientReceiver](LiteRtiClientReceiver.java) | Reads incoming messages, resolves response futures, and routes unsolicited callbacks through the context. |
| [LiteRtiCallbackDispatcher](LiteRtiCallbackDispatcher.java) | Queues callbacks and delivers them serially on a dedicated worker, independently of socket reading. |

## Request/response flow

1. The federate calls an RTI service through `LiteRtiAmbassador`.
2. `LiteRtiClientContext` validates the call and delegates sending to `LiteRtiClientSender`.
3. For a request requiring a response, the sender registers a `CompletableFuture` in the context **before sending**, keyed by the request's message handle.
4. The sender writes the request and waits for its future.
5. The receiver reads the response on its socket-reading thread, finds the matching future, and completes it with the result or an exception using the response handler at the client context.
6. The sender removes the pending entry when the call finishes, including on failure or timeout. 
7. When a context response handler throws an exception, the receiver uses it to complete the sender’s waiting future exceptionally. The sender unwraps and rethrows the exception, which propagates through the context and ambassador to the federate, subject to the ambassador’s exception handling.

>> All try catch logic for errors from futures should be in the RtiAmbassador
RtiAmbassador try{} -> ClientContext -> ClientSender (send msg, create future)
wait...
ClientReceiver -> ClientContext -> ClientReceiver (pass or fail future)
then...
ClientSender (resolve future) -> ClientContext -> RtiAmbassador

Requests that do not require a response are sent without waiting for a future.

Registration failures arrive as `ResponseRegisterObjectInstance` messages containing the original request handle and an `ERROR` status. The client completes the waiting future exceptionally with the server's failure reason. A registration response without an object instance handle is also rejected.

Late or unmatched responses are logged and ignored before response handlers run. Callback messages are classified separately before any future lookup, so they do not require a pending request.

## Callback flow and threading

Discovery, reflected attribute values, requests to provide attribute values, and received interactions follow a separate path:

1. `LiteRtiClientReceiver` identifies the callback message.
2. The receiver calls `LiteRtiClientContext.dispatchCallback()` to enqueue its handler in `LiteRtiCallbackDispatcher`, then returns to reading the socket.
3. The dispatcher invokes the context's handler on its single daemon worker, named `lite-rti-client-callback`.
4. The context delivers the callback to the configured `FederateAmbassador`.

The dispatcher preserves FIFO order and never holds its queue lock while invoking callback code. A callback can therefore call a synchronous RTI service without blocking the socket reader that must receive its response. A slow callback still delays subsequent callbacks, since delivery is deliberately serial.

### Enabling and disabling callbacks

- `disableCallbacks()` pauses delivery and retains queued callbacks. A callback already taken by the worker may finish.
- `enableCallbacks()` resumes queued callbacks **asynchronously on the same worker**, not on the calling thread. Previously queued callbacks remain ahead of newly queued callbacks.
- Disabling callbacks does not prevent the receiver from reading messages or completing request futures.
- A `RuntimeException` from a callback is logged without stopping subsequent callback delivery.

When the receiver connection closes, its cleanup closes the dispatcher. Closing clears queued callbacks, requests interruption of the worker, and ignores future dispatch attempts. Running callback code must cooperate with interruption to stop promptly.

## Outgoing message framing

Each message consists of a length prefix followed by its serialized payload. The sender locks the shared socket across the prefix, payload, and flush. This prevents different sender instances or forwarding threads from interleaving frames on the same connection.

## Limitations and TODOs

- Passive subscriptions are not currently supported.
- Review which remaining requests genuinely require a response rather than fire-and-forget delivery.