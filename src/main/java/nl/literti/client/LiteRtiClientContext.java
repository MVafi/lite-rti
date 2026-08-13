package nl.literti.client;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.NameNotFound;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import nl.literti.fom.FederationObjectModel;
import nl.literti.fom.FomMerger;
import nl.literti.fom.FomObjectClass;
import nl.literti.fom.parser.FomParser;
import nl.literti.impl.AttributeHandleImpl;
import nl.literti.impl.ObjectClassHandleImpl;
import nl.literti.messages.requests.UpdateAttributeValues;

/** Context for keeping track of the states and events of the client and its singular federate */
public class LiteRtiClientContext {

  // Establish TCP connection handlers
  private final LiteRtiClientSocket
      clientSocket; // Reference to the client socket for connecting to the TCP server
  private final LiteRtiClientSender clientSender; // Sender for sending requests to the TCP server

  private FederationObjectModel
      fom; // Fom stored at client side (deserialized ObjectModel object in memory)
  private FederateAmbassador
      federateAmbassador; // RTI reference for delivering callbacks to the federate

  // Map of responses that are still expected by the client
  private final Map<Integer, CompletableFuture<Object>> pendingResponses =
      new ConcurrentHashMap<>();

  public LiteRtiClientContext() {
    this.clientSocket = new LiteRtiClientSocket(this);
    this.fom = null;
    this.federateAmbassador = null;
    this.clientSender = this.clientSocket.getClientSender();
  }

  // ==== Connect Federate =====

  public boolean connectFederate(String federateType) {
    try {
      System.out.println("[LiteRtiClientContext] Connecting federate: " + federateType);
      clientSender.sendConnectFederateRequest(federateType);
      System.out.println("[LiteRtiClientContext] Successfully connected federate: " + federateType);
      return true;
    } catch (Exception e) {
      System.err.println(
          "[LiteRtiClientContext] Failed to connect federate "
              + federateType
              + ": "
              + e.getMessage());
      return false;
    }
  }

  public void handleConnectFederateResponse(String responseMsg) {
    if (responseMsg.startsWith("OK|FEDERATE_CONNECTED")) {
      System.out.println("[LiteRtiClientContext] Federate connected successfully");
      // Success, do nothing
    } else if (responseMsg.startsWith("ERROR|FEDERATE_ALREADY_CONNECTED")) {
      throw new RuntimeException("Federate already connected");
    } else {
      throw new RuntimeException("Failed to connect federate: " + responseMsg);
    }
  }

  // ==== Disconnect Federate =====

  public void disconnect() {
    clientSocket.checkConnection();
    try {
      clientSender.sendDisconnectFederateRequest(); // Blocking
    } catch (IOException e) {
      // Ignore
    }
    clientSocket.cleanup();
  }

  // ==== Create Federation =====

  public void createFederationExecution(String federationExecutionName, URL[] fomModules)
      throws FederationExecutionAlreadyExists, Exception {
    clientSocket.checkConnection();

    if (federationExecutionName == null) {
      throw new IllegalArgumentException("Federation execution name cannot be null or empty");
    }

    // Parse fom
    List<FederationObjectModel> foms = new ArrayList<FederationObjectModel>();
    for (URL module : fomModules) {
      System.out.println("[LiteRtiClientContext] Parsing FOM module: " + module);
      foms.add(FomParser.parse(module));
    }

    // Sort FOMs: put MIM first (if present), then others
    // MIM should be identified by filename containing "MIM" or "mim"
    // TODO: Currently FOM merging is hyrachical, not sure whether this can be an issue in the
    // future
    foms.sort(
        (a, b) -> {
          String aFileName = a.getFileName() != null ? a.getFileName().toLowerCase() : "";
          String bFileName = b.getFileName() != null ? b.getFileName().toLowerCase() : "";
          boolean aIsMim = aFileName.contains("mim");
          boolean bIsMim = bFileName.contains("mim");
          if (aIsMim && !bIsMim) return -1; // a comes first (MIM)
          if (!aIsMim && bIsMim) return 1; // b comes first (MIM)
          return 0; // maintain original order for others
        });

    System.out.println("[LiteRtiClientContext] FOM merge order:");
    for (FederationObjectModel fom : foms) {
      System.out.println("  - " + (fom.getFileName() != null ? fom.getFileName() : "unknown"));
    }

    // Merge Foms
    FederationObjectModel combinedFOM = FomMerger.merge(foms);
    System.out.println("[LiteRtiClientContext] Merged FOM modules into combined FOM");

    // Send serialized message and wait for response
    try {
      System.out.println("[LiteRtiClientContext] Sending federation creation request to server...");
      clientSender.sendCreateFederationRequest(federationExecutionName, combinedFOM);
      System.out.println("[LiteRtiClientContext] Federation creation request sent successfully");
    } catch (Exception e) {
      throw new RuntimeException(
          "[LiteRtiClientContext] Error creating federation: " + e.getMessage(), e);
    }
  }

  public void handleCreateFederationResponse(String responseMsg)
      throws FederationExecutionAlreadyExists {
    System.out.println(
        "[LiteRtiClientContext] Received federation creation response: " + responseMsg);

    if (responseMsg.startsWith("OK|FEDERATION_CREATED")) {
      System.out.println("[LiteRtiClientContext] Federation created successfully");
    } else if (responseMsg.startsWith("ERROR|FEDERATION_ALREADY_EXISTS")) {
      throw new FederationExecutionAlreadyExists("Federation already exists");
    } else {
      throw new RuntimeException(
          "[LiteRtiClientContext] Failed to create federation: " + responseMsg);
    }
  }

  // ==== Join Federation =====

  public void joinFederationExecution(
      String federateType, String federationExecutionName, URL[] fomModules) {
    clientSocket.checkConnection();

    try {
      // Step 1: Request and receive the federation's FOM from the server
      System.out.println("[LiteRtiClientContext] Requesting federation FOM from server...");
      Object result = clientSender.sendFederationFomRequest(federationExecutionName);
      if (!(result instanceof FederationObjectModel serverFom)) {
        throw new IOException(
            "Expected FederationObjectModel but received: "
                + (result != null ? result.getClass().getName() : "null"));
      }
      System.out.println("[LiteRtiClientContext] Received federation FOM from server");

      // Step 2: Validate that local FOM extensions can merge with federation FOM
      try {
        List<FederationObjectModel> localExtensions = new ArrayList<>();
        if (fomModules != null) {
          for (URL module : fomModules) {
            localExtensions.add(FomParser.parse(module));
          }
        }

        // Try merging federation FOM + local extensions
        // TODO : FOM extension during runtime is not yet supported (still needs to be communicated
        // back to the server)
        FomMerger.merge(serverFom, localExtensions);
        System.out.println(
            "[LiteRtiClientContext] FOM validation successful - FOMs are compatible");

      } catch (Exception e) {
        throw new RuntimeException(
            "[LiteRtiClientContext] FOM validation failed - incompatible FOMs: " + e.getMessage(),
            e);
      }

      // Step 3: Send confirmation that we've accepted the FOM and want to join (blocking until
      // server confirms)
      System.out.println("[LiteRtiClientContext] Sending join federation request to server...");
      clientSender.sendJoinFederationRequest(federateType, federationExecutionName);

      // Step 4: Store the federation FOM in client context upon succesful join
      this.setLocalFOM(serverFom);
      System.out.println(
          "[LiteRtiClientContext] Joined federation: "
              + federationExecutionName
              + " as "
              + federateType);

    } catch (Exception e) {
      throw new RuntimeException(
          "[LiteRtiClientContext] Error joining federation: " + e.getMessage(), e);
    }
  }

  public void handleJoinFederationResponse(String responseMsg)
      throws FederationExecutionAlreadyExists {
    if (responseMsg.startsWith("OK|JOINED_FEDERATION")) {
      System.out.println("[LiteRtiClientContext] Joined federation successfully");
    } else {
      throw new RuntimeException(
          "[LiteRtiClientContext] Failed to join federation: " + responseMsg);
    }
  }

  // ==== Publish Objects =====

  public void publishObjectClassAttributes(
      ObjectClassHandle theClass, AttributeHandleSet attributeList) {
    clientSocket.checkConnection();

    try {
      if (theClass == null) {
        throw new RuntimeException("[LiteRtiClientContext] ObjectClassHandle cannot be null");
      }

      // Send publication request to server
      clientSender.sendPublishObjectRequest(theClass, attributeList); // Blocking
    } catch (Exception e) {
      throw new RuntimeException(
          "[LiteRtiClientContext] Error publishing object class attributes: " + e.getMessage(), e);
    }
  }

  public void handlePublishObjectResponse(String responseMsg) {
    // TODO: check this error handling
    if (responseMsg.startsWith("OK|OBJECT_CLASS_PUBLISHED")) {
      System.out.println("[LiteRtiClientContext] Successfully published object class attributes");
    } else if (responseMsg.startsWith("ERROR|PUBLISH_OBJECT_CLASS")) {
      throw new RuntimeException(
          "[LiteRtiClientContext] Failed to publish object class attributes: " + responseMsg);
    } else {
      throw new RuntimeException(
          "[LiteRtiClientContext] Unexpected response for publish object class: " + responseMsg);
    }
  }

  // ==== Object Subscription =====

  public void subscribeObjectClassAttributes(
      ObjectClassHandle theClass, AttributeHandleSet attributeList) {
    clientSocket.checkConnection();
    try {
      if (theClass == null) {
        throw new RuntimeException("[LiteRtiClientContext] ObjectClassHandle cannot be null");
      }

      clientSender.sendSubscribeObjectRequest(theClass, attributeList); // Blocking
    } catch (IOException e) {
      throw new RuntimeException(
          "[LiteRtiClientContext] Error subscribing to object class attributes: " + e.getMessage(),
          e);
    }
  }

  public void handleSubscribeObjectResponse(String responseMsg) {
    if (responseMsg == null) {
      throw new RuntimeException(
          "[LiteRtiClientContext] No response received from server when subscribing to object class");
    } else if (responseMsg.startsWith("OK|")) {
      System.out.println("[LiteRtiClientContext] Successfully subscribed to object class");
    } else if (responseMsg.startsWith("ERROR|")) {
      String errorMsg = responseMsg.substring(6);
      throw new RuntimeException(
          "[LiteRtiClientContext] Server error subscribing to object class: " + errorMsg);
    } else {
      throw new RuntimeException(
          "[LiteRtiClientContext] Unexpected response from server: " + responseMsg);
    }
  }

  // ==== Object Instances =====

  public ObjectInstanceHandle registerObjectInstance(
      ObjectClassHandle theClass, String theObjectName) {
    clientSocket.checkConnection();

    try {
      if (theClass == null) {
        throw new RuntimeException("[LiteRtiClientContext] ObjectClassHandle cannot be null");
      }
      if (theObjectName == null || theObjectName.isEmpty()) {
        throw new RuntimeException("[LiteRtiClientContext] Object name cannot be null or empty");
      }

      Object result =
          clientSender.sendRegisterObjectInstanceRequest(theClass, theObjectName); // Blocking

      if (result instanceof ObjectInstanceHandle) {
        return (ObjectInstanceHandle) result;
      } else {
        throw new RuntimeException(
            "[LiteRtiClientContext] Unexpected response type when registering object instance: "
                + result);
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "[LiteRtiClientContext] Error registering object instance: " + e.getMessage(), e);
    }
  }

  public String getObjectInstanceName(ObjectInstanceHandle theHandle) {
    clientSocket.checkConnection();

    try {
      Object result = clientSender.sendObjectInstanceNameRequest(theHandle); // Blocking
      if (result instanceof String) {
        return (String) result;
      } else {
        throw new RuntimeException(
            "[LiteRtiClientContext] Unexpected response type when requesting object instance name: "
                + result);
      }

    } catch (Exception e) {
      throw new RuntimeException(
          "[LiteRtiClientContext] Error publishing object class attributes: " + e.getMessage(), e);
    }
  }

  // ==== Object Attribute updates =====

  public void updateAttributeValues(
      ObjectInstanceHandle theObject,
      AttributeHandleValueMap theAttributes,
      byte[] userSuppliedTag) {
    clientSocket.checkConnection();

    try {
      if (theObject == null) {
        throw new RuntimeException("[LiteRtiClientContext] ObjectInstanceHandle cannot be null");
      }
      if (theAttributes == null || theAttributes.isEmpty()) {
        throw new RuntimeException(
            "[LiteRtiClientContext] AttributeHandleValueMap cannot be null or empty");
      }

      clientSender.sendUpdateAttributeValuesRequest(theObject, theAttributes, userSuppliedTag);
    } catch (Exception e) {
      throw new RuntimeException(
          "[LiteRtiClientContext] Error updating attribute values: " + e.getMessage(), e);
    }
  }

  public void handleUpdateAttributeValues(UpdateAttributeValues update) {
    if (this.federateAmbassador == null) {
      System.err.println(
          "[LiteRtiClientContext] No federate ambassador set, cannot deliver reflected attributes");
      return;
    }

    try {

      // Call the federate ambassador's reflectAttributeValues callback
      federateAmbassador.reflectAttributeValues(
          update.getObjectInstanceHandle(),
          update.getAttributeValues(),
          update.getUserSuppliedTag(),
          null, // OrderType
          null, // TransportationTypeHandle
          null // ReflectInfo
          );

      System.out.println(
          "[LiteRtiClientContext] Delivered reflected attribute values for object instance: "
              + update.getObjectInstanceHandle());
    } catch (Exception e) {
      System.err.println(
          "[LiteRtiClientContext] Error delivering reflected attribute values: " + e.getMessage());
    }
  }

  // !!!!!! not sure about these following methods whether they are necessary !!!!!

  public void setLocalFOM(FederationObjectModel fom) {
    this.fom = fom;
  }

  public FederationObjectModel getLocalFOM() {
    return this.fom;
  }

  public ObjectClassHandle getObjectClassHandle(String objectClassName) throws NameNotFound {
    if (fom == null) {
      throw new RuntimeException("FOM not loaded - federate may not be joined to federation");
    }

    int handle = fom.getObjectClassHandle(objectClassName);
    if (handle == FederationObjectModel.INVALID_HANDLE) {
      throw new NameNotFound("Object class '" + objectClassName + "' not found in FOM");
    }

    ObjectClassHandle och = new ObjectClassHandleImpl(handle);
    System.out.println(
        "[LiteRtiClientContext] getObjectClassHandle: " + objectClassName + " -> " + handle);
    return och;
  }

  public AttributeHandle getAttributeHandle(ObjectClassHandle whichClass, String attributeName)
      throws NameNotFound {
    if (fom == null) {
      throw new RuntimeException(
          "FOM not loaded - unable to obtain attribute handle for " + attributeName);
    }

    // Extract the int handle from ObjectClassHandle
    int classHandle = ((ObjectClassHandleImpl) whichClass).getHandle();

    // Get the object class from FOM
    FomObjectClass objectClass = fom.getObjectClass(classHandle);
    if (objectClass == null) {
      throw new NameNotFound("Object class not found with handle: " + classHandle);
    }

    System.out.println(
        "[LiteRtiClientContext] getAttributeHandle: looking up attribute '"
            + attributeName
            + "' in object class '"
            + objectClass.getQualifiedName()
            + "' (handle: "
            + classHandle
            + ")");

    // Look up the attribute by name
    int attributeHandle = objectClass.getAttributeHandle(attributeName);
    if (attributeHandle == FederationObjectModel.INVALID_HANDLE) {
      throw new NameNotFound("Attribute '" + attributeName + "' not found in object class");
    }

    // Return wrapped handle
    System.out.println(
        "[LiteRtiClientContext] getAttributeHandle: " + attributeName + " -> " + attributeHandle);
    return new AttributeHandleImpl(attributeHandle);
  }

  public void setFederateAmbassador(FederateAmbassador ambassador) {
    this.federateAmbassador = ambassador;
  }

  public FederateAmbassador getFederateAmbassador() {
    return this.federateAmbassador;
  }

  // ===== Responses API =====
  // The ClientSocket sends requests, where these requests expect responses. The clientContext keeps
  // track of the pending responses

  public void registerPendingResponse(int msgHandle, CompletableFuture<Object> future) {
    pendingResponses.put(msgHandle, future);
  }

  public CompletableFuture<Object> getPendingResponse(int msgHandle) {
    return pendingResponses.get(msgHandle);
  }

  public void removePendingResponse(int msgHandle) {
    pendingResponses.remove(msgHandle);
  }
}
