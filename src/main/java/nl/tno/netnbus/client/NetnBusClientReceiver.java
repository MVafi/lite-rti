package nl.tno.netnbus.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import nl.tno.netnbus.AbstractNetnBusReceiver;
import nl.tno.netnbus.messages.MessageObject;
import nl.tno.netnbus.messages.requests.UpdateAttributeValues;
import nl.tno.netnbus.messages.responses.ResponseFederateConnected;
import nl.tno.netnbus.messages.responses.ResponseFederationCreated;
import nl.tno.netnbus.messages.responses.ResponseFederationFom;
import nl.tno.netnbus.messages.responses.ResponseJoinFederation;
import nl.tno.netnbus.messages.responses.ResponseObjectInstanceName;
import nl.tno.netnbus.messages.responses.ResponsePublishObject;
import nl.tno.netnbus.messages.responses.ResponseRegisterObjectInstance;
import nl.tno.netnbus.messages.responses.ResponseSubscribeObject;
import nl.tno.netnbus.utils.BinaryHelper;

/**
 * Transportation layer for handling binary messages received by the server (client-side), handling communication that is received from the server side
 */
public class NetnBusClientReceiver extends AbstractNetnBusReceiver {

  private final NetnBusClientContext clientContext;

  NetnBusClientReceiver(Socket socket, NetnBusClientContext clientContext) throws IOException {
    super(socket);
    this.clientContext = clientContext;
  }

  // ===== Binary message handling =====
  
  @Override
  protected void handleBinaryMessage(byte[] data) throws IOException {
    Object obj = BinaryHelper.deserializeBinaryMessage(data);
    if (!(obj instanceof MessageObject msgObj)) {
      return;
    }
    CompletableFuture<Object> future = clientContext.getPendingResponse(msgObj.getMsgHandle());

    // Route responses, futures created in the clientSender will be completed here when the response is received
    try {
      switch (obj) {
        case ResponseFederateConnected response -> {
          clientContext.handleConnectFederateResponse(response.getMsgString());
          future.complete(response.getMsgString());
        }
        case ResponseFederationCreated response -> {
          clientContext.handleCreateFederationResponse(response.getMsgString());
          future.complete(response.getMsgString());
        }
        case ResponseFederationFom response -> {
          future.complete(response.getFom()); // Let the future in the clientSender return with the FOM object
        }
        case ResponseJoinFederation response -> {
          clientContext.handleJoinFederationResponse(response.getMsgString());
          future.complete(response.getMsgString());
        }
        case ResponsePublishObject response -> {
          clientContext.handlePublishObjectResponse(response.getMsgString());
          future.complete(response.getMsgString());
        }
        case ResponseSubscribeObject response -> {
          clientContext.handleSubscribeObjectResponse(response.getMsgString());
          future.complete(response.getMsgString());
        }
        case ResponseRegisterObjectInstance response -> {
          future.complete(response.getObjectInstanceHandle()); // Complete with the actual handle object
        }
        case ResponseObjectInstanceName response -> {
          future.complete(response.getObjectInstanceName()); // Complete with the name string
        }
        case UpdateAttributeValues update -> {
          System.out.println("[ClientReceiver] Received UpdateAttributeValues");
          clientContext.handleUpdateAttributeValues(update);
        }
        default -> {
          throw new RuntimeException("Unknown message type: " + obj.getClass().getName());
        }
      }
    } catch (Exception e) {
      System.err.println("[ClientReceiver] THE FUTURE HAS FAILED: " + e.getMessage());
      future.completeExceptionally(e);
    }
  }
}