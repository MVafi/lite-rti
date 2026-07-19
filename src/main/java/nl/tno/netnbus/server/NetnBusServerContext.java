package nl.tno.netnbus.server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import nl.tno.netnbus.FederationExecution;
import nl.tno.netnbus.fom.FederationObjectModel;

/** Context for keeping track of the states of the server and its federates */
public class NetnBusServerContext {

  private final Set<String> connectedFederates = ConcurrentHashMap.newKeySet();
  private final Map<String, FederationExecution> federationExecutions = new ConcurrentHashMap<>();  //Keep track of all federation executions by name
  private final Map<String, NetnBusServerReceiver> federateHandlers = new ConcurrentHashMap<>();     //Keep track of all federate handlers by federate name

  public NetnBusServerContext() {}

  void registerFederate(String name) {
    this.connectedFederates.add(name);
    System.out.println("[NetnBusContext] Federate connected: " + name);
  }

  void unregisterFederate(String name) {
    this.connectedFederates.remove(name);
    System.out.println("[NetnBusContext] Federate disconnected: " + name);
  }

  void createFederationExecution(String federationName) throws FederationExecutionAlreadyExists {
    if (this.federationExecutions.containsKey(federationName)) {
      throw new FederationExecutionAlreadyExists("Federation already exists: " + federationName);
    }
    FederationExecution fed = new FederationExecution(federationName);
    this.federationExecutions.put(federationName, fed);
    System.out.println("[NetnBusContext] Federation created: " + federationName);
  }

  void createFederationExecutionWithFOM(String federationName, FederationObjectModel fom) 
      throws FederationExecutionAlreadyExists {
    if (this.federationExecutions.containsKey(federationName)) {
      throw new FederationExecutionAlreadyExists("Federation already exists: " + federationName);
    }
    // Create federation and store the FOM
    FederationExecution fedEx = new FederationExecution(federationName);
    fedEx.setFOM(fom);
    this.federationExecutions.put(federationName, fedEx);
    System.out.println("[NetnBusContext] Federation created with FOM: " + federationName);
  }

  public Set<String> getAllFederationExecutions() {
    return Set.copyOf(this.federationExecutions.keySet());
  }

  public Set<String> getConnectedFederates() {
    return Set.copyOf(this.connectedFederates);
  }

  public FederationExecution getFederation(String federationName) {
    return this.federationExecutions.get(federationName);
  }

  // void destroyFederationExecution(String federationName) throws FederationExecutionDoesNotExist {
  //   if (!this.federationExecutions.containsKey(federationName)) {
  //     throw new FederationExecutionDoesNotExist("Federation not found: " + federationName);
  //   }
  //   this.federationExecutions.remove(federationName);
  //   System.out.println("[NetnBusContext] Federation destroyed: " + federationName);
  // }

  void removeFederateFromAllFederations(String federateName) {
    for (FederationExecution fedEx : this.federationExecutions.values()) {
      if (fedEx.getJoinedFederateKeys().contains(federateName)) {
        String fedtype = fedEx.removeFederate(federateName);
        System.out.println(
            "[NetnBusContext] Federate "
                + fedtype
                + " ("
                + federateName
                + ") removed from federation "
                + fedEx.getName());
      }
    }
  }

  void joinFederationExecution(String federationName, String federateType, String federateName)
      throws FederationExecutionDoesNotExist {
    FederationExecution fedEx = this.federationExecutions.get(federationName);
    if (fedEx == null) {
      throw new FederationExecutionDoesNotExist("Federation not found: " + federationName);
    }
    fedEx.addFederate(federateType, federateName);
  }

  FederationObjectModel retreiveFederationFom(String federationName) throws FederationExecutionDoesNotExist {
    FederationExecution fedEx = this.federationExecutions.get(federationName);
    if (fedEx == null) {
      return null;
    }
    return fedEx.getFOM();
  }

  // ---- Federate Handler Management ----
  // Keeping track of the client handlers for each federate, so that we can send messages to them when needed

  void registerFederateHandler(String federateName, NetnBusServerReceiver handler) {
    federateHandlers.put(federateName, handler);
  }

  void unregisterFederateHandler(String federateName) {
    federateHandlers.remove(federateName);
  }

  NetnBusServerReceiver getFederateHandler(String federateName) {
    return federateHandlers.get(federateName);
  }
}
