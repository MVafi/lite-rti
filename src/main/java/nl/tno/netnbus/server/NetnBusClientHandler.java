package nl.tno.netnbus.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import nl.tno.netnbus.FederationExecution;
import nl.tno.netnbus.client.requests.CreateFederationRequest;
import nl.tno.netnbus.fom.FederationObjectModel;
import nl.tno.netnbus.utils.BinaryHelper;

/**
 * Client handler (server-side endpoint) for managing communication with an individual federate
 * SocketClient
 */
public class NetnBusClientHandler {

  // Handles communication with federate client
  private final Socket socket;

  // Reference to the context to manage federate events and state
  private final NetnBusServerContext context;

  // todo: atm federateName is used as the handle, but this is not very robust. Consider using a
  // federate handle in the future.
  private String federateName; // e.g. "simfederate@12345678"
  private String federateType; // e.g. "SimFederate"
  private String federationName; // e.g. "ExampleFederation"
  private DataOutputStream out;
  private DataInputStream in;

  // Message type constants (hybrid messaging protocol for easy debugging)
  private static final byte MSG_TEXT = 0;
  private static final byte MSG_BINARY = 1;

  NetnBusClientHandler(Socket socket, NetnBusServerContext context) {
    this.socket = socket;
    this.context = context;
  }

  // Keep handling messages while still receiving them (within separate thread)
  public void handle() {
    System.out.println("[ClientHandler] New connection, starting handler");
    try {
      // Data streams from client socket
      this.out = new DataOutputStream(socket.getOutputStream());
      this.in = new DataInputStream(socket.getInputStream());

      System.out.println("[ClientHandler] Waiting for messages...");
      while (true) {
        try {
          byte messageType = in.readByte();

          // Handling string messages (for commands and debugging)
          if (messageType == MSG_TEXT) {
            String message = in.readUTF();
            System.out.println("[ClientHandler] Received: " + message);
            handleStringMessage(message);

            // Handling binary messages (for HLA)
          } else if (messageType == MSG_BINARY) {
            int length = in.readInt();
            byte[] data = new byte[length];
            in.readFully(data);
            System.out.println("[ClientHandler] Received binary message, " + length + " bytes");
            handleBinaryMessage(data);

          } else {
            System.err.println("[ClientHandler] Unknown message type: " + messageType);
          }
        } catch (IOException e) {
          // Connection closed or error reading
          throw e;
        }
      }
    } catch (IOException e) {
      System.out.println("[ClientHandler] Connection closed: " + e.getMessage());
    } finally {
      // Currently this is sometimes done double
      cleanupClient();
    }
  }

  private void cleanupClient() {
    if (federateName != null) {
      System.out.println("[ClientHandler] Cleaning up federate: " + federateName);
      context.removeFederateFromAllFederations(federateName);
      context.unregisterFederate(federateName);
    }
    try {
      socket.close();
    } catch (IOException e) {
      // Ignore
    }
  }

  // ===== String message API =====

  private void handleStringMessage(String message) {
    String[] parts = message.split("\\|", 2);
    String command = parts[0];
    String payload = parts.length > 1 ? parts[1] : "";
    try {
      switch (command) {
        case "CONNECT":
          handleConnectFederate(payload);
          break;
        case "DISCONNECT":
          handleDisconnectFederate();
          break;
        case "CREATE_FEDERATION":
          handleCreateFederation(payload);
          break;
        case "JOIN_FEDERATION":
          handleJoinFederation(payload);
          break;
        case "CONFIRM_JOIN":
          handleConfirmJoin(payload);
          break;
        // case "GET_INTERACTION_HANDLE":
        //   handleGetInteractionHandle(payload);
        //   break;
        // case "GET_PARAMETER_HANDLE":
        //   handleGetParameterHandle(payload);
        //   break;
        case "SUBSCRIBE_OBJECT_CLASS":
          handleSubscribeObjectClass(payload);
          break;
        case "PUBLISH_OBJECT_CLASS":
          handlePublishObjectClass(payload);
          break;
        // case "COUNT":
        //   int count = context.getFederateCount();
        //   sendTextMessage("OK|" + count);
        //   break;
        default:
          sendTextMessage("ERROR|Unknown command: " + command);
      }
    } catch (IOException e) {
      System.err.println("[ClientHandler] Error handling message: " + e.getMessage());
    }
  }

  private void sendTextMessage(String message) throws IOException {
    System.out.println("[ClientHandler] Sending response: " + message);
    out.writeByte(MSG_TEXT);
    out.writeUTF(message);
    out.flush();
  }

  private void handleConnectFederate(String payload) throws IOException {
    this.federateName = payload;
    context.registerFederate(this.federateName);
    sendTextMessage("OK|CONNECTED");
  }

  private void handleDisconnectFederate() throws IOException {
    context.unregisterFederate(this.federateName);
    sendTextMessage("OK|DISCONNECTED");
    cleanupClient();
  }

  private void handleCreateFederation(String payload) throws IOException {
    try {
      context.createFederationExecution(payload);
      sendTextMessage("OK|FEDERATION_CREATED");
    } catch (FederationExecutionAlreadyExists e) {
      sendTextMessage("ERROR|FEDERATION_ALREADY_EXISTS");
    } catch (Exception e) {
      sendTextMessage("ERROR|" + e.getMessage());
    }
  }

  private void handleJoinFederation(String payload) throws IOException {
    String federationName = payload;
    try {
      // Get the federation to retrieve its FOM
      FederationObjectModel fom = context.retreiveFederationFom(federationName);
      if (fom == null) {
        sendTextMessage("ERROR|FEDERATION_DOES_NOT_EXIST");
        return;
      }

      byte[] serializedFom = BinaryHelper.serializeRequestObject(fom);
      
      // Send the FOM as a binary message
      System.out.println("[ClientHandler] Sending federation FOM to joining federate...");
      sendMessage(serializedFom);
      
    } catch (FederationExecutionDoesNotExist e) {
      sendTextMessage("ERROR|FEDERATION_DOES_NOT_EXIST");
    } catch (Exception e) {
      System.err.println("[ClientHandler] Error sending FOM: " + e.getMessage());
      sendTextMessage("ERROR|" + e.getMessage());
    }
  }

  private void handleConfirmJoin(String payload) throws IOException {
    // Client has validated the FOM and confirmed join - extract federation/federate info from payload
    String[] parts = payload.split("\\|");
    if (parts.length != 2) {
      sendTextMessage("ERROR|Invalid CONFIRM_JOIN format");
      return;
    }
    String federationName = parts[0];
    String federateType = parts[1];
    
    try {
      // Store federation/federate info now that client has confirmed
      this.federationName = federationName;
      this.federateType = federateType;
      
      context.joinFederationExecution(this.federationName, this.federateType, this.federateName);
      sendTextMessage("OK|JOINED_FEDERATION");
      System.out.println("[ClientHandler] Federate " + this.federateName + " confirmed join to " + this.federationName);
    } catch (FederationExecutionDoesNotExist e) {
      sendTextMessage("ERROR|FEDERATION_DOES_NOT_EXIST");
    } catch (Exception e) {
      sendTextMessage("ERROR|" + e.getMessage());
    }
  }

  // private void handleGetInteractionHandle(String interactionName) throws IOException {
  //   if (federationName == null) {
  //     sendTextMessage("ERROR|Not joined to a federation");
  //     return;
  //   }
  //   nl.tno.netnbus.FederationExecution fed = context.getFederation(federationName);
  //   if (fed == null) {
  //     sendTextMessage("ERROR|Federation not found");
  //     return;
  //   }
  //   int handle = fed.getHandleRegistry().getInteractionClassHandle(interactionName);
  //   sendTextMessage("OK|" + handle);
  // }

  // private void handleGetParameterHandle(String parameterName) throws IOException {
  //   if (federationName == null) {
  //     sendTextMessage("ERROR|Not joined to a federation");
  //     return;
  //   }
  //   nl.tno.netnbus.FederationExecution fed = context.getFederation(federationName);
  //   if (fed == null) {
  //     sendTextMessage("ERROR|Federation not found");
  //     return;
  //   }
  //   int handle = fed.getHandleRegistry().getParameterHandle(parameterName);
  //   sendTextMessage("OK|" + handle);
  // }

  private void handleSubscribeObjectClass(String payload) throws IOException {
    // Parse: classHandle|attr1,attr2,...
    String[] parts = payload.split("\\|", 2);
    if (parts.length != 2) {
      sendTextMessage("ERROR|Invalid SUBSCRIBE_OBJECT_CLASS format");
      return;
    }
    
    String classHandle = parts[0];
    String attributeIds = parts[1];
    
    try {
      if (federationName == null) {
        sendTextMessage("ERROR|Not joined to a federation");
        return;
      }
      
      FederationExecution fedEx = context.getFederation(federationName);
      if (fedEx == null) {
        sendTextMessage("ERROR|Federation not found");
        return;
      }
      
      // Store subscription in federation
      fedEx.subscribeToObjectClass(federateName, classHandle, attributeIds);
      
      System.out.println("[ClientHandler] Federate " + federateName + " subscribed to object class " + classHandle + " with attributes: " + attributeIds);
      sendTextMessage("OK|SUBSCRIBED");
    } catch (Exception e) {
      sendTextMessage("ERROR|" + e.getMessage());
    }
  }

  private void handlePublishObjectClass(String payload) throws IOException {
    // Parse: classHandle|attr1,attr2,...
    String[] parts = payload.split("\\|", 2);
    if (parts.length != 2) {
      sendTextMessage("ERROR|Invalid PUBLISH_OBJECT_CLASS format");
      return;
    }
    
    String classHandle = parts[0];
    String attributeIds = parts[1];
    
    try {
      if (federationName == null) {
        sendTextMessage("ERROR|Not joined to a federation");
        return;
      }
      
      FederationExecution fedEx = context.getFederation(federationName);
      if (fedEx == null) {
        sendTextMessage("ERROR|Federation not found");
        return;
      }
      
      // Store publication in federation
      fedEx.publishObjectClass(federateName, classHandle, attributeIds);
      
      System.out.println("[ClientHandler] Federate " + federateName + " publishing object class " + classHandle + " with attributes: " + attributeIds);
      sendTextMessage("OK|PUBLISHED");

    } catch (Exception e) {
      sendTextMessage("ERROR|" + e.getMessage());
    }
  }

  // ===== Binary message API =====

  private void handleBinaryMessage(byte[] data) {
    try {
      // Deserialize the binary data into a Request object
      Object obj = BinaryHelper.deserializeBinaryMessage(data);

      if (obj instanceof CreateFederationRequest reqObj) {
        context.createFederationExecutionWithFOM(reqObj.getFederationName(), reqObj.getFom());
        sendTextMessage("OK|FEDERATION_CREATED");
      }
      
      
  




    // TODO: have a better look at these exceptions
    } catch (FederationExecutionAlreadyExists e) {
      // Federation with this name already exists
      try {
        sendTextMessage("ERROR|FEDERATION_ALREADY_EXISTS");
      } catch (IOException ex) {
        System.err.println("[ClientHandler] Error sending error response: " + ex.getMessage());
      }
    } catch (ClassNotFoundException e) {
      // Failed to deserialize the request
      System.err.println("[ClientHandler] Failed to deserialize CreateFederationRequest: " + e.getMessage());
      try {
        sendTextMessage("ERROR|Failed to deserialize request");
      } catch (IOException ex) {
        System.err.println("[ClientHandler] Error sending error response: " + ex.getMessage());
      }
    } catch (Exception e) {
      // General error handling
      System.err.println("[ClientHandler] Error handling binary message: " + e.getMessage());
      e.printStackTrace();
      try {
        sendTextMessage("ERROR|" + e.getMessage());
      } catch (IOException ex) {
        System.err.println("[ClientHandler] Error sending error response: " + ex.getMessage());
      }
    }
  }

  public void sendMessage(byte[] data) throws IOException {
    out.writeByte(MSG_BINARY);
    out.writeInt(data.length);
    out.write(data);
    out.flush();
  }
}

// //   /** Unregister a disconnected federate. */
// //   void unregisterFederate(String name) {
// //     // connectedFederates.remove(name);
// //     System.out.println("[NetnBus] Federate disconnected: " + name);
// //     System.out.println("[NetnBus] Remaining federates: " + connectedFederates.size());
// //   }

// //   /** Get count of connected federates. */
// //   public int getFederateCount() {
// //     // return connectedFederates.size();
// //   }

// //   /** Check if server is running. */
// //   public boolean isRunning() {
// //     return running;
// //   }

//   /** Handles communication with a single federate client. */
//   static class ClientHandler implements Runnable {
//     private final Socket socket;
//     private final NetnBusContext context;
//     private String federateName;
//     private PrintWriter out;
//     private BufferedReader in;

//     ClientHandler(Socket socket, NetnBusContext context) {
//       this.socket = socket;
//       this.context = context;
//     }

//     @Override
//     public void run() {
//       try {
//         out = new PrintWriter(socket.getOutputStream(), true);
//         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//         String line;
//         while ((line = in.readLine()) != null) {
//           handleMessage(line);
//         }
//       } catch (IOException e) {
//         // Connection closed
//       } finally {
//         cleanup();
//       }
//     }

//     void send(String message) {
//       if (out != null) {
//         out.println(message);
//       }
//     }

//   }
