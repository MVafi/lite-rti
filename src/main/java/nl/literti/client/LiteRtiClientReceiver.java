package nl.literti.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import nl.literti.AbstractLiteRtiReceiver;
import nl.literti.messages.MessageObject;
import nl.literti.messages.requests.DiscoveredObjectInstance;
import nl.literti.messages.requests.RequestAttributeValueUpdate;
import nl.literti.messages.requests.UpdateAttributeValues;
import nl.literti.messages.requests.UpdateSendInteraction;
import nl.literti.messages.responses.ResponseFederateConnected;
import nl.literti.messages.responses.ResponseFederationCreated;
import nl.literti.messages.responses.ResponseFederationFom;
import nl.literti.messages.responses.ResponseJoinFederation;
import nl.literti.messages.responses.ResponseObjectInstanceName;
import nl.literti.messages.responses.ResponsePublishInteraction;
import nl.literti.messages.responses.ResponsePublishObject;
import nl.literti.messages.responses.ResponseRegisterObjectInstance;
import nl.literti.messages.responses.ResponseSubscribeInteraction;
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

    // Send non response messages to the clientDispatcher (these are responses without a future to complete)
    switch (msgObj) {
      case DiscoveredObjectInstance callback -> {
        clientContext.queueCallback(() -> clientContext.handleDiscoveredObjectInstances(callback));
        return;
      }
      case UpdateAttributeValues callback -> {
        clientContext.queueCallback(() -> clientContext.handleUpdateAttributeValues(callback));
        return;
      }
      case RequestAttributeValueUpdate callback -> {
        clientContext.queueCallback(() -> clientContext.handleRequestAttributeValueUpdate(callback));
        return;
      }
      case UpdateSendInteraction callback -> {
        clientContext.queueCallback(() -> clientContext.handleUpdateSendInteraction(callback));
        return;
      }
      default -> {}
    }

    // Response messages with a future
    CompletableFuture<Object> future = clientContext.getPendingResponse(msgObj.getMsgHandle());
    if (future == null) {
      System.err.println("[ClientReceiver] Ignoring late or unmatched "
          + msgObj.getClass().getSimpleName() + " (msgHandle " + msgObj.getMsgHandle() + ")");
      return;
    }

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
        case ResponsePublishInteraction response -> {
          clientContext.handlePublishInteractionResponse(response.getMsgString());
          future.complete(response.getMsgString());
        }
        case ResponseSubscribeObject response -> {
          clientContext.handleSubscribeObjectResponse(response.getMsgString());
          future.complete(response.getMsgString());
        }
        case ResponseSubscribeInteraction response -> {
          clientContext.handleSubscribeInteractionResponse(response.getMsgString());
          future.complete(response.getMsgString());
        }
        case ResponseRegisterObjectInstance response -> {
          future.complete(
              response.getObjectInstanceHandle()); // Complete with the actual handle object
        }
        case ResponseObjectInstanceName response -> {
          future.complete(response.getObjectInstanceName()); // Complete with the name string
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

  @Override
  protected void cleanupReceiver() {
    clientContext.shutdownCallbacks();
  }
}
