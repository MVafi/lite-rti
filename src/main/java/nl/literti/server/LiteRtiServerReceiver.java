package nl.literti.server;

import java.io.IOException;
import java.net.Socket;

import nl.literti.AbstractLiteRtiReceiver;
import nl.literti.messages.MessageObject;
import nl.literti.messages.requests.RequestConnectFederate;
import nl.literti.messages.requests.RequestCreateFederation;
import nl.literti.messages.requests.RequestDisconnectFederate;
import nl.literti.messages.requests.RequestFederationFom;
import nl.literti.messages.requests.RequestJoinFederation;
import nl.literti.messages.requests.RequestObjectInstanceName;
import nl.literti.messages.requests.RequestPublishObject;
import nl.literti.messages.requests.RequestRegisterObjectInstance;
import nl.literti.messages.requests.RequestSubscribeObject;
import nl.literti.utils.BinaryHelper;

/**
 * Transportation layer for handling messages received by the client (server-side), handling communication that is received from the client side. Each federate has its own serverReceiver
 */
public class LiteRtiServerReceiver extends AbstractLiteRtiReceiver {

  private final int connectionHandle;                 // Unique integer handle assigned to this connection
  private final LiteRtiServerContext serverContext;   // Reference to the context to manage federate events and state
  // private final int federateHandle;                   // Unique integer handle assigned to this federate

  private String federateType;                            // Type of the federate, user supplied (e.g., "FederateA")
  private String federationName;

  LiteRtiServerReceiver(Socket socket, LiteRtiServerContext serverContext, int connectionHandle) throws IOException {
    super(socket);
    this.serverContext = serverContext;
    this.connectionHandle = connectionHandle;
  }

  @Override
  protected void cleanupReceiver() {
    serverContext.unregisterFederateConnection(this.connectionHandle);
    serverContext.removeFederateFromAllFederations(this.federateType);
  }

  // ===== Binary message handling =====

  @Override
  protected void handleBinaryMessage(byte[] data) throws IOException {
    Object obj = BinaryHelper.deserializeBinaryMessage(data);
    if (!(obj instanceof MessageObject)) {
      return;
    }

    try {
      switch (obj) {
        case RequestConnectFederate request -> {
          serverContext.handleConnectFederateRequest(this.socket, request, this.connectionHandle);
        }
        case RequestDisconnectFederate request -> {
          serverContext.handleDisconnectFederateRequest(this.socket, request, this.connectionHandle);
          cleanupReceiver();
        }
        case RequestCreateFederation request -> {
          serverContext.handleCreateFederationRequest(this.socket, request);
        }
        case RequestFederationFom request -> {
          serverContext.handleFederationFomRequest(this.socket, request);
        }
        case RequestJoinFederation request -> {
          this.federateType = request.getFederateType();
          this.federationName = request.getFederationName();
          serverContext.handleJoinFederationRequest(this.socket, request);
        }
        case RequestPublishObject request -> {
          //todo: remove federationname here, i dont think federationname should be stored in the receiver
          serverContext.handlePublishObjectClass(this.socket, request, this.federationName, this.federateType);
        }
        case RequestSubscribeObject request -> {
          //todo: remove federationname here, i dont think federationname should be stored in the receiver
          serverContext.handleSubscribeObjectClass(this.socket, request, this.federationName, this.federateType);
        }
        case RequestRegisterObjectInstance request -> {
          serverContext.handleRegisterObjectInstance(this.socket, request, this.federationName, this.federateType);
        }
        case RequestObjectInstanceName request -> {
          serverContext.handleObjectInstanceNameRequest(this.socket, request, this.federationName, this.federateType);
        }
        default -> {
          throw new RuntimeException("[ServerReceiver] Unknown binary message type: " + obj.getClass().getName());
        }
      }
    } catch (Exception e) {
      System.err.println("[ServerReceiver] Error handling binary message: " + e.getMessage());
      e.printStackTrace();
    }
  }
}