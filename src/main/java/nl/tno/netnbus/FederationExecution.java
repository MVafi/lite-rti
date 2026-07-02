package nl.tno.netnbus;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FederationExecution {
  private final String name;
  private final Map<String, String> joinedFederates = new ConcurrentHashMap<>();  // federateName -> federateType
  private final long createdAt = System.currentTimeMillis();
  
  public FederationExecution(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void addFederate(String federateType, String federateName) {
    joinedFederates.put(federateName, federateType);
  }
  
  public String removeFederate(String federateName) {
    String federateType = joinedFederates.get(federateName);
    joinedFederates.remove(federateName);
    return federateType;
  }
  
  public Set<String> getJoinedFederateKeys() {
    return Set.copyOf(joinedFederates.keySet());
  }

  public Set<String> getJoinedFederateTypes() {
    return Set.copyOf(joinedFederates.values());
  }
}
