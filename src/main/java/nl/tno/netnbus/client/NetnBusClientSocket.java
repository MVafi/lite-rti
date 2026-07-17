package nl.tno.netnbus.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import nl.tno.netnbus.client.requests.CreateFederationRequest;
import nl.tno.netnbus.impl.ObjectInstanceHandleImpl;
import nl.tno.netnbus.fom.FederationObjectModel;
import nl.tno.netnbus.fom.FomMerger;
import nl.tno.netnbus.fom.parser.FomParser;
import nl.tno.netnbus.server.NetnBusServerSocket;
import nl.tno.netnbus.utils.BinaryHelper;

// TODO check this file
/**
 * Socket client for connecting to the NETN Bus server. Used by federates to connect to a running
 * NetnBusApplication.
 */
public class NetnBusClientSocket {

  private final String host;
  private final int port;
  private Socket socket;
  private DataOutputStream out;
  private DataInputStream in;
  private boolean connected = false;
  private NetnBusClientContext clientContext;

//   1. Sender (CreateFederation Handler)
//    ├─ ObjectModel fom (in memory)
//    └─ serialized → byte[] via Util.objectToByteBuffer()

// 2. Network (JGroups)
//    ├─ Transmitted as serialized bytes
//    └─ Received by all members

// 3. Receiver (Federation.receiveCreateFederation)
//    ├─ byte[] payload received
//    ├─ deserialized → ObjectModel via Util.objectFromByteBuffer(payload)
//    └─ manifest.federationCreated( ObjectModel )

// 4. Manifest stores the deserialized object
//    ├─ this.fom = fom;  // In-memory ObjectModel
//    └─ Never stored as bytes

  // Message type constants
  private static final byte MSG_TEXT = 0;
  private static final byte MSG_BINARY = 1;

  public NetnBusClientSocket(String host, int port, NetnBusClientContext clientContext) {
    this.host = host;
    this.port = port;
    this.clientContext = clientContext;
  }

  public NetnBusClientSocket(NetnBusClientContext clientContext) {
    this("localhost", NetnBusServerSocket.DEFAULT_PORT, clientContext);
  }

  // ===== Connection API =====

  public boolean connect(String federateName) {
    try {
      System.out.println("[NetnBusClient] Connecting to " + host + ":" + port);

      // Open TCP connection to host server and get input/output streams
      this.socket = new Socket(host, port);
      this.socket.setSoTimeout(5000); // 5 second timeout for socket reads
      this.out = new DataOutputStream(socket.getOutputStream());
      this.in = new DataInputStream(socket.getInputStream());

      // Send connect command
      System.out.println("[NetnBusClient] Sending CONNECT|" + federateName);
      sendTextMessage("CONNECT|" + federateName);

      // Wait for response
      System.out.println("[NetnBusClient] Waiting for response...");
      String response = receiveTextMessage();
      System.out.println("[NetnBusClient] Got response: " + response);
      if (response != null && response.startsWith("OK|CONNECTED")) {
        this.connected = true;
        return true;
      }
    } catch (IOException e) {
      System.err.println("Failed to connect to NetnBus at " + host + ":" + port);
      System.err.println("Is NetnBusApplication running? Error: " + e.getMessage());
    }
    return false;
  }

  public void disconnect() {
    if (connected) {
      try {
        sendTextMessage("DISCONNECT");
        receiveTextMessage(); // Wait for acknowledgment
      } catch (IOException e) {
        // Ignore
      }
      cleanup();
    }
  }

  public void checkConnection() {
    if (!this.connected) {
      System.err.println("[NetnBusClient] Not connected to bus, cannot create federation");
    }
  }

  /** Check if connected to the bus. */
  // public boolean isConnected() {
  //   return connected;
  // }

  // ===== Federation API =====

  public void createFederationExecution(String federationExecutionName, URL[] fomModules)
      throws FederationExecutionAlreadyExists, Exception {
    this.checkConnection();

    if (federationExecutionName == null) {
      throw new IllegalArgumentException("Federation execution name cannot be null or empty");
    }

    // Parse fom
    List<FederationObjectModel> foms = new ArrayList<FederationObjectModel>();
    for (URL module : fomModules) {
      System.out.println("[NetnBusClient] Parsing FOM module: " + module);
      foms.add(FomParser.parse(module));
    }

    // Sort FOMs: put MIM first (if present), then others
    // MIM should be identified by filename containing "MIM" or "mim"
    // TODO: Currently FOM merging is hyrachical, not sure whether this can be an issue in the future
    foms.sort((a, b) -> {
      String aFileName = a.getFileName() != null ? a.getFileName().toLowerCase() : "";
      String bFileName = b.getFileName() != null ? b.getFileName().toLowerCase() : "";
      boolean aIsMim = aFileName.contains("mim");
      boolean bIsMim = bFileName.contains("mim");
      if (aIsMim && !bIsMim) return -1; // a comes first (MIM)
      if (!aIsMim && bIsMim) return 1;  // b comes first (MIM)
      return 0; // maintain original order for others
    });

    System.out.println("[NetnBusClient] FOM merge order:");
    for (FederationObjectModel fom : foms) {
      System.out.println("  - " + (fom.getFileName() != null ? fom.getFileName() : "unknown"));
    }

    // Merge Foms
    FederationObjectModel combinedFOM = FomMerger.merge( foms );
    System.out.println("[NetnBusClient] Merged FOM modules into combined FOM");

    // Try sending serialized message
    try {
      CreateFederationRequest request = new CreateFederationRequest(federationExecutionName, combinedFOM);
      byte[] requestBytes = BinaryHelper.serializeRequestObject(request);

      // Send serialized request to server
      System.out.println("[NetnBusClient] Sending federation creation request to server...");
      sendMessage(requestBytes);
    } catch (IOException e) {
      throw new RuntimeException("[NetnBusClient] Error creating federation: " + e.getMessage(), e);
    }

    // Wait for response from server
    try {
      String response = receiveTextMessage();
      if (response == null) {
        throw new RuntimeException(
            "[NetnBusClient] No response received from server when creating federation: "
                + federationExecutionName);
      } else if (response.startsWith("OK|FEDERATION_CREATED")) {
        System.out.println("[NetnBusClient] Federation created: " + federationExecutionName);
      } else if (response.startsWith("ERROR|FEDERATION_ALREADY_EXISTS")) {
        throw new FederationExecutionAlreadyExists(
            "Federation already exists: " + federationExecutionName);
      } else {
        throw new RuntimeException("[NetnBusClient] Failed to create federation: " + response);
      }
    } catch (IOException e) {
      throw new RuntimeException("[NetnBusClient] Error receiving response from server: " + e.getMessage(), e);
    }
  }


  public void joinFederationExecution(String federateType, String federationExecutionName, URL[] fomModules) {
    this.checkConnection();
    try {
      // Send join request to server
      sendTextMessage("JOIN_FEDERATION|" + federationExecutionName);

      // Receive the federation's FOM from the server (sent as binary)
      System.out.println("[NetnBusClient] Waiting to receive federation FOM from server...");
      Object obj = BinaryHelper.deserializeBinaryMessage(receiveMessage());

      if (!(obj instanceof FederationObjectModel serverFom)) {
        throw new RuntimeException("[NetnBusClient] Received unexpected object type from server: " + obj.getClass().getName());
      }
      System.out.println("[NetnBusClient] Received federation FOM from server");

      // Validate: Can local FOM extensions merge with federation FOM?
      try {
        List<FederationObjectModel> localExtensions = new ArrayList<>();
        if (fomModules != null) {
          for (URL module : fomModules) {
            localExtensions.add(FomParser.parse(module));
          }
        }
        
        // Try merging federation FOM + local extensions
        // TODO : FOM extension during runtime is not yet supported (still needs to be communicated back to the server)
        FomMerger.merge(serverFom, localExtensions);
        System.out.println("[NetnBusClient] FOM validation successful - FOMs are compatible");
        
      } catch (Exception e) {
        throw new RuntimeException("[NetnBusClient] FOM validation failed - incompatible FOMs: " + e.getMessage(), e);
      }
      
      // Success: send confirmation that we've accepted the FOM
      sendTextMessage("CONFIRM_JOIN|" + federationExecutionName + "|" + federateType);
      
      // Receive final confirmation
      String response = receiveTextMessage();
      if (response == null) {
        throw new RuntimeException(
            "[NetnBusClient] No response received from server when joining federation: "
                + federationExecutionName);
      } else if (response.startsWith("OK|JOINED_FEDERATION")) {

        // Store the federation FOM in client context
        this.clientContext.setLocalFOM(serverFom);

        System.out.println(
            "[NetnBusClient] Joined federation: "
                + federationExecutionName
                + " as "
                + federateType);
      } else {
        throw new RuntimeException("[NetnBusClient] Failed to join federation: " + response);
      }

    } catch (IOException e) {
      throw new RuntimeException("[NetnBusClient] Error joining federation: " + e.getMessage(), e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("[NetnBusClient] Failed to deserialize federation FOM: " + e.getMessage(), e);
    }
  }

  public void subscribeObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList) {
    this.checkConnection();
    try {
      if (theClass == null) {
        throw new RuntimeException("[NetnBusClient] ObjectClassHandle cannot be null");
      }
      
      // Build comma-separated list of attribute handles
      // null/empty attributeList means subscribe to all attributes
      StringBuilder attributeIds = new StringBuilder();
      if (attributeList != null && !attributeList.isEmpty()) {
        boolean first = true;
        for (AttributeHandle attrHandle : attributeList) {
          if (attrHandle == null) {
            throw new RuntimeException("[NetnBusClient] AttributeHandle cannot be null in AttributeHandleSet");
          }
          if (!first) attributeIds.append(",");
          attributeIds.append(attrHandle.toString());
          first = false;
        }
      } else {
        // Empty set or null means "all attributes"
        attributeIds.append("*");
      }
      
      // Send subscription request to server
      sendTextMessage("SUBSCRIBE_OBJECT_CLASS|" + theClass.toString() + "|" + attributeIds.toString());
      
      // Wait for server response
      String response = receiveTextMessage();
      if (response == null) {
        throw new RuntimeException("[NetnBusClient] No response received from server when subscribing to object class");
      } else if (response.startsWith("OK|")) {
        System.out.println("[NetnBusClient] Successfully subscribed to object class");
      } else if (response.startsWith("ERROR|")) {
        String errorMsg = response.substring(6);
        throw new RuntimeException("[NetnBusClient] Server error subscribing to object class: " + errorMsg);
      } else {
        throw new RuntimeException("[NetnBusClient] Unexpected response from server: " + response);
      }
    } catch (IOException e) {
      throw new RuntimeException("[NetnBusClient] Error subscribing to object class attributes: " + e.getMessage(), e);
    }
  }

  public void publishObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList) {
    this.checkConnection();
    try {
      if (theClass == null) {
        throw new RuntimeException("[NetnBusClient] ObjectClassHandle cannot be null");
      }
      
      // Build comma-separated list of attribute handles
      // null/empty attributeList means publish all attributes
      StringBuilder attributeIds = new StringBuilder();
      if (attributeList != null && !attributeList.isEmpty()) {
        boolean first = true;
        for (AttributeHandle attrHandle : attributeList) {
          if (attrHandle == null) {
            throw new RuntimeException("[NetnBusClient] AttributeHandle cannot be null in AttributeHandleSet");
          }
          if (!first) attributeIds.append(",");
          attributeIds.append(attrHandle.toString());
          first = false;
        }
      } else {
        // Empty set or null means "all attributes"
        attributeIds.append("*");
      }
      
      // Send publication request to server
      sendTextMessage("PUBLISH_OBJECT_CLASS|" + theClass.toString() + "|" + attributeIds.toString());
      
      // Wait for server response
      String response = receiveTextMessage();
      if (response == null) {
        throw new RuntimeException("[NetnBusClient] No response received from server when publishing to object class");
      } else if (response.startsWith("OK|")) {
        String status = response.substring(3);
        System.out.println("[NetnBusClient] Successfully published to object class: " + status);
      } else if (response.startsWith("ERROR|")) {
        String errorMsg = response.substring(6);
        throw new RuntimeException("[NetnBusClient] Server error publishing to object class: " + errorMsg);
      } else {
        throw new RuntimeException("[NetnBusClient] Unexpected response from server: " + response);
      }
    } catch (IOException e) {
      throw new RuntimeException("[NetnBusClient] Error publishing to object class attributes: " + e.getMessage(), e);
    }
  }

  // /** Get federate count from server. */
  // public int getFederateCount() {
  //   if (!connected) return 0;
  //   try {
  //     sendTextMessage("COUNT");
  //     String response = receiveTextMessage();
  //     if (response != null && response.startsWith("OK|")) {
  //       return Integer.parseInt(response.substring(3));
  //     }
  //   } catch (Exception e) {
  //     // Ignore
  //   }
  //   return 0;
  // }

  // ===== Handle Registry API =====

  // TODO: This might be better using binary messages, but for now we can use text messages for
  // simplicity
  public int getInteractionClassHandle(String interactionName) {
    this.checkConnection();
    try {
      sendTextMessage("GET_INTERACTION_HANDLE|" + interactionName);
      String response = receiveTextMessage();
      if (response != null && response.startsWith("OK|")) {

        // Handles in HLA are integer values
        return Integer.parseInt(response.substring(3));
      }
    } catch (IOException e) {
      System.err.println("[NetnBusClient] Error getting interaction handle: " + e.getMessage());
    }
    return -1;
  }

  // TODO: This might be better using binary messages, but for now we can use text messages for
  // simplicity
  public int getParameterHandle(String parameterName) {
    this.checkConnection();
    try {
      sendTextMessage("GET_PARAMETER_HANDLE|" + parameterName);
      String response = receiveTextMessage();
      if (response != null && response.startsWith("OK|")) {

        // Handles in HLA are integer values
        return Integer.parseInt(response.substring(3));
      }
    } catch (IOException e) {
      System.err.println("[NetnBusClient] Error getting parameter handle: " + e.getMessage());
    }
    return -1;
  }

  // ===== Objects API =====

  public ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass) {
    this.checkConnection();
    try {
      if (theClass == null) {
        throw new RuntimeException("[NetnBusClient] ObjectClassHandle cannot be null");
      }
      
      // Send registration request to server without a specific name (auto-generated)
      sendTextMessage("REGISTER_OBJECT_INSTANCE|" + theClass.toString());
      
      // Wait for server response
      String response = receiveTextMessage();
      if (response == null) {
        throw new RuntimeException("[NetnBusClient] No response received from server when registering object instance");
      } else if (response.startsWith("OK|")) {
        // Extract the object instance handle from response
        String handleStr = response.substring(3);
        try {
          int handle = Integer.parseInt(handleStr);
          System.out.println("[NetnBusClient] Successfully registered object instance with handle: " + handle);
          return new ObjectInstanceHandleImpl(handle);
        } catch (NumberFormatException e) {
          throw new RuntimeException("[NetnBusClient] Invalid object instance handle in server response: " + handleStr, e);
        }
      } else if (response.startsWith("ERROR|")) {
        String errorMsg = response.substring(6);
        throw new RuntimeException("[NetnBusClient] Server error registering object instance: " + errorMsg);
      } else {
        throw new RuntimeException("[NetnBusClient] Unexpected response from server: " + response);
      }
    } catch (IOException e) {
      throw new RuntimeException("[NetnBusClient] Error registering object instance: " + e.getMessage(), e);
    }
  }

  // public ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass, String theObjectName) {
  //   this.checkConnection();
  //   try {
  //     if (theClass == null) {
  //       throw new RuntimeException("[NetnBusClient] ObjectClassHandle cannot be null");
  //     }
  //     if (theObjectName == null || theObjectName.isEmpty()) {
  //       throw new RuntimeException("[NetnBusClient] Object name cannot be null or empty");
  //     }
      
  //     // Send registration request to server with a specific name
  //     sendTextMessage("REGISTER_OBJECT_INSTANCE|" + theClass.toString() + "|" + theObjectName);
      
  //     // Wait for server response
  //     String response = receiveTextMessage();
  //     if (response == null) {
  //       throw new RuntimeException("[NetnBusClient] No response received from server when registering object instance");
  //     } else if (response.startsWith("OK|")) {
  //       // Extract the object instance handle from response
  //       String handleStr = response.substring(3);
  //       try {
  //         int handle = Integer.parseInt(handleStr);
  //         System.out.println("[NetnBusClient] Successfully registered object instance '" + theObjectName + "' with handle: " + handle);
  //         return new ObjectInstanceHandleImpl(handle);
  //       } catch (NumberFormatException e) {
  //         throw new RuntimeException("[NetnBusClient] Invalid object instance handle in server response: " + handleStr, e);
  //       }
  //     } else if (response.startsWith("ERROR|")) {
  //       String errorMsg = response.substring(6);
  //       throw new RuntimeException("[NetnBusClient] Server error registering object instance: " + errorMsg);
  //     } else {
  //       throw new RuntimeException("[NetnBusClient] Unexpected response from server: " + response);
  //     }
  //   } catch (IOException e) {
  //     throw new RuntimeException("[NetnBusClient] Error registering object instance: " + e.getMessage(), e);
  //   }
  // }

  public String getObjectInstanceName(int objectInstanceHandle) {
    this.checkConnection();
    try {
      sendTextMessage("GET_OBJECT_INSTANCE_NAME|" + objectInstanceHandle);
      String response = receiveTextMessage();
      if (response != null && response.startsWith("OK|")) {
        return response.substring(3);
      } else if (response != null && response.startsWith("ERROR|")) {
        System.err.println("[NetnBusClient] Server error getting object instance name: " + response.substring(6));
      }
    } catch (IOException e) {
      System.err.println("[NetnBusClient] Error getting object instance name: " + e.getMessage());
    }
    return null;
  }

  // ===== Interactions API =====
  // public void subscribeInteractionClass(Class clazz) {
  //   this.checkConnection(); //Not sure if needed
  //   try {
  //     sendTextMessage("SUBSCRIBE_INTERACTION|" + federationExecutionName + "|" + federateType);

  //     String response = receiveTextMessage();
  //     if (response == null){
  //       throw new RuntimeException("[NetnBusClient] No response received from server when
  // subscribing to interaction class: " + federationExecutionName);
  //     } else if (response.startsWith("OK|SUBSCRIBED_INTERACTION")) {
  //       System.out.println("[NetnBusClient] Subscribed to interaction class: " +
  // federationExecutionName + " as " + federateType);
  //     } else {
  //       throw new RuntimeException("[NetnBusClient] Failed to subscribe to interaction class: " +
  // response);
  //     }

  //   } catch (IOException e) {
  //     throw new RuntimeException("[NetnBusClient] Error subscribing to interaction class: " +
  // e.getMessage(), e);
  //   }
  // }

  // ===== Messaging API =====

  /** Send a text control message */
  private void sendTextMessage(String message) throws IOException {
    out.writeByte(MSG_TEXT);
    out.writeUTF(message);
    out.flush();
  }

  /** Block until receiving a text control message */
  private String receiveTextMessage() throws IOException {
    byte type = in.readByte();
    if (type == MSG_TEXT) {
      return in.readUTF();
    }
    throw new IOException("Expected TEXT message, got type: " + type);
  }

  // Send binary message
  public void sendMessage(byte[] data) throws IOException {
    if (!connected) throw new IOException("Not connected to bus");
    out.writeByte(MSG_BINARY);
    out.writeInt(data.length);
    out.write(data);
    out.flush();
  }

  /** Receive binary message */
  public byte[] receiveMessage() throws IOException {
    byte type = in.readByte();
    if (type == MSG_BINARY) {
      int length = in.readInt();
      byte[] data = new byte[length];
      in.readFully(data);
      return data;
    }
    throw new IOException("Expected BINARY message, got type: " + type);
  }

  private void cleanup() {
    connected = false;
    try {
      if (socket != null) socket.close();
    } catch (IOException e) {
      // Ignore
    }
  }
}
