package nl.literti.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import nl.literti.FederationExecution;
import nl.literti.fom.FederationObjectModel;
import nl.literti.messages.requests.RequestConnectFederate;
import nl.literti.messages.requests.RequestCreateFederation;
import nl.literti.messages.requests.RequestDisconnectFederate;
import nl.literti.messages.requests.RequestFederationFom;
import nl.literti.messages.requests.RequestJoinFederation;
import nl.literti.messages.requests.RequestObjectInstanceName;
import nl.literti.messages.requests.RequestPublishObject;
import nl.literti.messages.requests.RequestRegisterObjectInstance;
import nl.literti.messages.requests.RequestSubscribeObject;

/** Context for keeping track of the states of the server and its federates */
public class LiteRtiServerContext {

  private final LiteRtiServerSender serverSender;

  // Connection map
  private final Map<Integer, String> connectionHandle2Socket = new ConcurrentHashMap<>();  // This map is used when messages need forwarding to other federates, to find the socket for a given connection handle. These handles are global as they can be re-used for connecting to different federations, unlike federate handles

  // Federation map
  private final Map<String, FederationExecution> federationName2FederationExecutions = new ConcurrentHashMap<>();    // Keep track of all federation executions by federation name

  public LiteRtiServerContext() {
    this.serverSender = new LiteRtiServerSender();
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

  // ===== ONLY FOR DEBUG ATM, might delete this later =====

  public Set<String> getAllFederationExecutions() {
    return Set.copyOf(this.federationName2FederationExecutions.keySet());
  }

  public Set<String> getConnectedFederates() {
    return Set.copyOf(this.connectionHandle2Socket.values());
  }

  public FederationExecution getFederation(String federationName) {
    return this.federationName2FederationExecutions.get(federationName);
  }

}
