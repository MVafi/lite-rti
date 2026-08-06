package nl.tno.netnbus.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import nl.tno.netnbus.FederationExecution;
import nl.tno.netnbus.fom.FederationObjectModel;
import nl.tno.netnbus.messages.requests.RequestConnectFederate;
import nl.tno.netnbus.messages.requests.RequestCreateFederation;
import nl.tno.netnbus.messages.requests.RequestDisconnectFederate;
import nl.tno.netnbus.messages.requests.RequestFederationFom;
import nl.tno.netnbus.messages.requests.RequestJoinFederation;
import nl.tno.netnbus.messages.requests.RequestObjectInstanceName;
import nl.tno.netnbus.messages.requests.RequestPublishObject;
import nl.tno.netnbus.messages.requests.RequestRegisterObjectInstance;
import nl.tno.netnbus.messages.requests.RequestSubscribeObject;

/** Context for keeping track of the states of the server and its federates */
public class NetnBusServerContext {

  private final NetnBusServerSender serverSender;

  // Connection map
  private final Map<Integer, String> connectionHandle2Socket = new ConcurrentHashMap<>();  // This map is used when messages need forwarding to other federates, to find the socket for a given connection handle. These handles are global as they can be re-used for connecting to different federations, unlike federate handles

  // Federation map
  private final Map<String, FederationExecution> federationName2FederationExecutions = new ConcurrentHashMap<>();    // Keep track of all federation executions by federation name

  public NetnBusServerContext() {
    this.serverSender = new NetnBusServerSender();
  }

  // ==== Connect Federate =====

  public void handleConnectFederateRequest(Socket socket, RequestConnectFederate reqObj, int connectionHandle) throws IOException {
    registerFederateConnection(socket, connectionHandle);
    serverSender.sendFederateConnectedResponse(socket, reqObj.getMsgHandle()); //<< this is strictly not needed from the spec
  }

  public void registerFederateConnection(Socket socket, int connectionHandle) {
    connectionHandle2Socket.put(connectionHandle, socket.toString());
  }

  // ==== Disconnect Federate =====

  public void handleDisconnectFederateRequest(Socket socket, RequestDisconnectFederate reqObj, int connectionHandle) throws IOException {
    unregisterFederateConnection(connectionHandle);
  }

  public void unregisterFederateConnection(int connectionHandle) {
    connectionHandle2Socket.remove(connectionHandle);
  }

  // ==== Create Federation =====

  public void handleCreateFederationRequest(Socket socket, RequestCreateFederation reqObj) throws IOException {
    String msg;
    try{
      createFederationExecutionWithFOM(reqObj.getFederationName(), reqObj.getFom());
      msg = "OK|FEDERATION_CREATED";
    } catch (FederationExecutionAlreadyExists e) {
      msg = "ERROR|FEDERATION_ALREADY_EXISTS";
    }
    serverSender.sendFederationCreatedResponse(socket, reqObj.getMsgHandle(), msg);
  }

   void createFederationExecutionWithFOM(String federationName, FederationObjectModel fom) throws FederationExecutionAlreadyExists {
    // TODO: Check if federation already exists??
    
    if (this.federationName2FederationExecutions.containsKey(federationName)) {
      throw new FederationExecutionAlreadyExists("Federation already exists: " + federationName);
    }

    // Create federation and store the FOM
    FederationExecution fedEx = new FederationExecution(federationName);
    fedEx.setFOM(fom);
    this.federationName2FederationExecutions.put(federationName, fedEx);
  }

  // ==== Request FOM =====

  void handleFederationFomRequest(Socket socket, RequestFederationFom reqObj) throws IOException {
    String msg;
    FederationObjectModel fom = null;
    try{
      fom = retreiveFederationFom(reqObj.getFederationName());
      msg = "OK|FEDERATION_FOM_RETRIEVED";
    } catch (FederationExecutionDoesNotExist e) {
      msg = "ERROR|FEDERATION_DOES_NOT_EXIST";
    } catch (Exception e) {
      msg = "ERROR|" + e.getMessage();
    }
    serverSender.sendFederationFomResponse(socket, reqObj.getMsgHandle(), fom, msg);
  }

  FederationObjectModel retreiveFederationFom(String federationName) throws FederationExecutionDoesNotExist {
    FederationExecution fedEx = this.federationName2FederationExecutions.get(federationName);
    if (fedEx == null) {
      throw new FederationExecutionDoesNotExist("Federation not found: " + federationName);
    }
    return fedEx.getFOM();
  }

  // ==== Joining Federations =====

  void handleJoinFederationRequest(Socket socket, RequestJoinFederation reqObj) throws IOException {
    String msg;
    try{
      joinFederationExecution(reqObj.getFederationName(), reqObj.getFederateType());
      msg = "OK|JOINED_FEDERATION";
    } catch (FederationExecutionDoesNotExist e) {
      msg = "ERROR|FEDERATION_DOES_NOT_EXIST";
    } catch (Exception e) {
      msg = "ERROR|" + e.getMessage();
    }
    serverSender.sendJoinFederationResponse(socket, reqObj.getMsgHandle(), msg);
  }

  void joinFederationExecution(String federationName, String federateType) throws FederationExecutionDoesNotExist {
    FederationExecution fedEx = this.federationName2FederationExecutions.get(federationName);
    if (fedEx == null) {
      throw new FederationExecutionDoesNotExist("Federation not found: " + federationName);
    }
    fedEx.addFederate(federateType, federateType);
  }

  void removeFederateFromAllFederations(String federateType) {
    for (FederationExecution fedEx : this.federationName2FederationExecutions.values()) {
      if (fedEx.getJoinedFederateKeys().contains(federateType)) {
        fedEx.removeFederate(federateType);
      }
    }
  }

  // ==== Publish Object Class =====

  void handlePublishObjectClass(Socket socket, RequestPublishObject reqObj, String federationName, String federateType) throws IOException {   
    String msg;
    
    if (federationName == null) {
      msg = "ERROR|FEDERATION_NAME_NOT_PROVIDED";
      serverSender.sendPublishObjectResponse(socket, reqObj.getMsgHandle(), msg);
      return;
    }

    FederationExecution fedEx = this.federationName2FederationExecutions.get(federationName);
    if (fedEx == null) {
      msg = "ERROR|FEDERATION_DOES_NOT_EXIST";
      serverSender.sendPublishObjectResponse(socket, reqObj.getMsgHandle(), msg);
      return;
    }

    fedEx.publishObjectClass(federateType, reqObj.getObjectClassHandle(), reqObj.getAttributeHandleSet());
    msg = "OK|OBJECT_CLASS_PUBLISHED";
    serverSender.sendPublishObjectResponse(socket, reqObj.getMsgHandle(), msg);
  }

  // ==== Subscribe Object Class =====

  void handleSubscribeObjectClass(Socket socket, RequestSubscribeObject reqObj, String federationName, String federateType) throws IOException {
    String msg;

    if (federationName == null) {
      msg = "ERROR|FEDERATION_NAME_NOT_PROVIDED";
      serverSender.sendSubscribeObjectResponse(socket, reqObj.getMsgHandle(), msg);
      return;
    }

    FederationExecution fedEx = this.federationName2FederationExecutions.get(federationName);
    if (fedEx == null) {
      msg = "ERROR|FEDERATION_DOES_NOT_EXIST";
      serverSender.sendSubscribeObjectResponse(socket, reqObj.getMsgHandle(), msg);
      return;
    }

    fedEx.subscribeToObjectClass(federateType, reqObj.getObjectClassHandle(), reqObj.getAttributeHandleSet());
    msg = "OK|OBJECT_CLASS_SUBSCRIBED";
    serverSender.sendSubscribeObjectResponse(socket, reqObj.getMsgHandle(), msg);
  }

  // ==== Register Object Instance =====

  void handleRegisterObjectInstance(Socket socket, RequestRegisterObjectInstance reqObj, String federationName, String federateType) throws IOException {
    String msg;

    if (federationName == null) {
      msg = "ERROR|FEDERATION_NAME_NOT_PROVIDED";
      serverSender.sendRegisterObjectInstanceResponse(socket, reqObj.getMsgHandle(), null, msg);
      return;
    }

    FederationExecution fedEx = this.federationName2FederationExecutions.get(federationName);
    if (fedEx == null) {
      msg = "ERROR|FEDERATION_DOES_NOT_EXIST";
      serverSender.sendRegisterObjectInstanceResponse(socket, reqObj.getMsgHandle(), null, msg);
      return;
    }

    ObjectInstanceHandle objectInstanceHandle = fedEx.registerObjectInstance(federateType, reqObj.getObjectClassHandle());
    msg = "OK|OBJECT_INSTANCE_REGISTERED";
    serverSender.sendRegisterObjectInstanceResponse(socket, reqObj.getMsgHandle(), objectInstanceHandle, msg);
  }

  void handleObjectInstanceNameRequest(Socket socket, RequestObjectInstanceName reqObj, String federationName, String federateType) throws IOException {
    String msg;

    if (federationName == null) {
      msg = "ERROR|FEDERATION_NAME_NOT_PROVIDED";
      serverSender.sendObjectInstanceNameResponse(socket, reqObj.getMsgHandle(), null, msg);
      return;
    }

    FederationExecution fedEx = this.federationName2FederationExecutions.get(federationName);
    if (fedEx == null) {
      msg = "ERROR|FEDERATION_DOES_NOT_EXIST";
      serverSender.sendObjectInstanceNameResponse(socket, reqObj.getMsgHandle(), null, msg);
      return;
    }

    String objectInstanceName = fedEx.getObjectInstanceName(reqObj.getObjectInstanceHandle());
    msg = "OK|OBJECT_INSTANCE_NAME_RETRIEVED";
    serverSender.sendObjectInstanceNameResponse(socket, reqObj.getMsgHandle(), objectInstanceName, msg);
  }



  
  // public int getNextFederateHandle() {
  //   return this.federateHandleCounter.getAndIncrement();
  // }



  // void unregisterFederate(Integer federateHandle) {
  //   this.connectedFederateHandle2FederateType.remove(federateHandle);
  // }

  // void createFederationExecution(String federationType) throws FederationExecutionAlreadyExists {
  //   if (this.federateType2FederationExecutions.containsKey(federationType)) {
  //     throw new FederationExecutionAlreadyExists("Federation already exists: " + federationType );
  //   }
  //   FederationExecution fed = new FederationExecution(federationType);
  //   this.federateType2FederationExecutions.put(federationType, fed);
  //   System.out.println("[NetnBusContext] Federation created: " + federationType);
  // }


  // ===== ONLY FOR DEBUG ATM =====

  public Set<String> getAllFederationExecutions() {
    return Set.copyOf(this.federationName2FederationExecutions.keySet());
  }

  public Set<String> getConnectedFederates() {
    return Set.copyOf(this.connectionHandle2Socket.values());
  }

  public FederationExecution getFederation(String federationName) {
    return this.federationName2FederationExecutions.get(federationName);
  }

  // void destroyFederationExecution(String federationName) throws FederationExecutionDoesNotExist {
  //   if (!this.federationExecutions.containsKey(federationName)) {
  //     throw new FederationExecutionDoesNotExist("Federation not found: " + federationName);
  //   }
  //   this.federationExecutions.remove(federationName);
  //   System.out.println("[NetnBusContext] Federation destroyed: " + federationName);
  // }



  // public void handleUpdateAttributeValues(FederationExecution fedEx, UpdateAttributeValues request, String federateName) throws IOException {
  //   try {
  //     System.out.println("[ServerReceiver] Federate " + federateName + " updating attribute values for object instance: " + request.getObjectInstanceHandle() + " with " + request.getAttributeValues().size() + " attribute(s)");
  //     // Get the object class for this instance
  //     ObjectClassHandle  objectClassHandle = fedEx.getObjectInstanceClass(request.getObjectInstanceHandle());
  //     if (objectClassHandle == null) {
  //       // todo error handling
  //       return;
  //     }

  //     System.out.println(">>>>>>>>>> not null");

  //     // Find federates subscribed to this object class
  //     Map<String, AttributeHandleSet> subscribedFederates = fedEx.getSubscribedFederates(objectClassHandle);

  //     System.out.println(subscribedFederates.entrySet());
      
  //     // Forward the update to each subscribed federate (except the publisher)
  //     for (Map.Entry<String, AttributeHandleSet> entry : subscribedFederates.entrySet()) {
  //       String subscriberName = entry.getKey();
  //       if (subscriberName.equals(federateName)) {
  //         // Don't send to ourselves
  //         continue;
  //       }

  //       // Get the handler for this 
  //       // TODO: might want to create a better map, that combines the subscribed federates and their handlers, to avoid looking up the handler each time
  //       NetnBusServerReceiver subscriberHandler = this.getFederateHandler(subscriberName);
  //       if (subscriberHandler != null) {
  //         try {
  //           // Forward the update to the subscriber
  //           byte[] serializedUpdate = BinaryHelper.serializeRequestObject(request);
  //           subscriberHandler.sendMessage(serializedUpdate);
  //           System.out.println("[ServerReceiver] Forwarded attribute update to federate: " + subscriberName);
  //         } catch (Exception e) {
  //           System.err.println("[ServerReceiver] Failed to forward update to " + subscriberName + ": " + e.getMessage());
  //         }
  //       } else {
  //         System.err.println("[ServerReceiver] No handler found for federate: " + subscriberName);
  //       }
  //     }

  //     // System.out.println("[ServerReceiver] Federate " + federateName + 
  //     //     " updated " + attributeValues.size() + 
  //     //     " attribute(s) for object instance: " + objectInstanceHandle +
  //     //     " (forwarded to " + (subscribedFederates.size() - 1) + " subscriber(s))");

  //     // sendTextMessage("OK|ATTRIBUTE_VALUES_UPDATED");
  //   } catch (Exception e) {
  //     System.err.println("[ServerReceiver] Error updating attribute values: " + e.getMessage());
  //     // sendTextMessage("ERROR|" + e.getMessage());
  //   }
  // }

  // // ---- Federate Handler Management ----
  // // Keeping track of the client handlers for each federate, so that we can send messages to individual clients when needed

  // void registerFederateHandler(String federateType, NetnBusServerReceiver handler) {
  //   federateType2FederateHandlers.put(federateType, handler);
  // }

  // void unregisterFederateHandler(String federateType) {
  //   federateType2FederateHandlers.remove(federateType);
  // }

  // NetnBusServerReceiver getFederateHandler(String federateType) {
  //   return federateType2FederateHandlers.get(federateType);
  // }
}
