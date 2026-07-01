package nl.tno.netnbus.server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import nl.tno.netnbus.FederationExecution;

/** Context for keeping track of the states of the federates and the server */
public class NetnBusContext {

  private final Set<String> connectedFederates = ConcurrentHashMap.newKeySet();
  private final Map<String, FederationExecution> federationExecutions = new ConcurrentHashMap<>();
    
  
  public NetnBusContext() {
  }

  void registerFederate(String name) {
    connectedFederates.add(name);
    System.out.println("[NetnBus] Federate connected: " + name);
    System.out.println("[NetnBus] Total federates: " + connectedFederates.size());
  }

  void unregisterFederate(String name) {
    connectedFederates.remove(name);
    System.out.println("[NetnBus] Federate disconnected: " + name);
    System.out.println("[NetnBus] Remaining federates: " + connectedFederates.size());
  }

  void createFederationExecution(String federationName) throws FederationExecutionAlreadyExists {
    if (federationExecutions.containsKey(federationName)) {
      throw new FederationExecutionAlreadyExists("Federation already exists: " + federationName);
    }
    FederationExecution fed = new FederationExecution(federationName);
    federationExecutions.put(federationName, fed);
    System.out.println("[NetnBus] Federation created: " + federationName);
  }

  void destroyFederationExecution(String federationName) throws FederationExecutionDoesNotExist {
    if (!federationExecutions.containsKey(federationName)) {
      throw new FederationExecutionDoesNotExist("Federation not found: " + federationName);
    }
    federationExecutions.remove(federationName);
    System.out.println("[NetnBus] Federation destroyed: " + federationName);
  }
  
  FederationExecution getFederation(String federationName) throws FederationExecutionDoesNotExist {
    FederationExecution fed = federationExecutions.get(federationName);
    if (fed == null) {
      throw new FederationExecutionDoesNotExist("Federation not found: " + federationName);
    }
    return fed;
  }

}
