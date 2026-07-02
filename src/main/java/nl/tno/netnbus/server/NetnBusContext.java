package nl.tno.netnbus.server;

import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import nl.tno.netnbus.FederationExecution;

/** Context for keeping track of the states of the server and its federates */
public class NetnBusContext {

  private final Set<String> connectedFederates = ConcurrentHashMap.newKeySet();
  private final Map<String, FederationExecution> federationExecutions = new ConcurrentHashMap<>();

  public NetnBusContext() {}

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

  // FederationExecution getFederation(String federationName) throws FederationExecutionDoesNotExist
  // {
  //   FederationExecution fed = this.federationExecutions.get(federationName);
  //   if (fed == null) {
  //     throw new FederationExecutionDoesNotExist("Federation not found: " + federationName);
  //   }
  //   return fed;
  // }

}
