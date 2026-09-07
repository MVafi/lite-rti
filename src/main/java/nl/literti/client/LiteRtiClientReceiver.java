package nl.literti.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import nl.literti.AbstractLiteRtiReceiver;
import nl.literti.messages.MessageObject;
import nl.literti.messages.requests.DiscoveredObjectInstance;
import nl.literti.messages.requests.UpdateAttributeValues;
import nl.literti.messages.responses.ResponseFederateConnected;
import nl.literti.messages.responses.ResponseFederationCreated;
import nl.literti.messages.responses.ResponseFederationFom;
import nl.literti.messages.responses.ResponseJoinFederation;
import nl.literti.messages.responses.ResponseObjectInstanceName;
import nl.literti.messages.responses.ResponsePublishObject;
import nl.literti.messages.responses.ResponseRegisterObjectInstance;
import nl.literti.messages.responses.ResponseSubscribeObject;
import nl.literti.utils.BinaryHelper;

/**
 * Transportation layer for handling binary messages received by the server (client-side), handling
 * communication that is received from the server side
 */
public class LiteRtiClientReceiver extends AbstractLiteRtiReceiver {

  private final LiteRtiClientContext clientContext;

  LiteRtiClientReceiver(Socket socket, LiteRtiClientContext clientContext) throws IOException {
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

    // Todo: would be cleaner to move this line into the switch methods below
    CompletableFuture<Object> future = clientContext.getPendingResponse(msgObj.getMsgHandle());

    // Route responses, futures created in the clientSender will be completed here when the response
    // is received
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
          future.complete(
              response.getFom()); // Let the future in the clientSender return with the FOM object
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
        case DiscoveredObjectInstance response -> {
          // No msgHandle is needed
          clientContext.handleDiscoveredObjectInstances(response);
        }
        case ResponseRegisterObjectInstance response -> {
          future.complete(
              response.getObjectInstanceHandle()); // Complete with the actual handle object
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
