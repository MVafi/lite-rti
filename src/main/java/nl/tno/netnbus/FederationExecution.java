package nl.tno.netnbus;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FederationExecution {
  private final String name;
  private final Set<String> joinedFederates = ConcurrentHashMap.newKeySet();
  private final long createdAt = System.currentTimeMillis();
  
  public FederationExecution(String name) {
    this.name = name;
  }
  
  public void addFederate(String federateName) {
    joinedFederates.add(federateName);
  }
  
  public void removeFederate(String federateName) {
    joinedFederates.remove(federateName);
  }
  
  public Set<String> getJoinedFederates() {
    return Set.copyOf(joinedFederates);
  }
}
