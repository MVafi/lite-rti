package nl.tno.netnbus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import nl.tno.netnbus.fom.FederationObjectModel;

public class FederationExecution {
  private final String name;
  private final Map<String, String> joinedFederates = new ConcurrentHashMap<>(); // federateName -> federateType
  private final long createdAt = System.currentTimeMillis();
  private final HandleRegistry handleRegistry = new HandleRegistry();
  private FederationObjectModel fom; // The Federation Object Model
  
  // Store subscriptions: federateName -> (objectClassHandle -> comma-separated attribute IDs)
  private final Map<String, Map<String, String>> federateSubscriptions = new ConcurrentHashMap<>();
  
  // Store publications: federateName -> (objectClassHandle -> comma-separated attribute IDs)
  private final Map<String, Map<String, String>> federatePublications = new ConcurrentHashMap<>();

  public FederationExecution(String name) {
    this.name = name;
    this.fom = null;
  }

  public String getName() {
    return name;
  }

  public void setFOM(FederationObjectModel fom) {
    this.fom = fom;
  }

  public FederationObjectModel getFOM() {
    return fom;
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

  public HandleRegistry getHandleRegistry() {
    return handleRegistry;
  }

  /**
   * Subscribe a federate to object class attributes.
   *
   * @param federateName The name of the federate
   * @param objectClassHandle The object class handle
   * @param attributeIds Comma-separated attribute IDs (e.g., "1,2,3")
   */
  public void subscribeToObjectClass(String federateName, String objectClassHandle, String attributeIds) {
    federateSubscriptions.computeIfAbsent(federateName, k -> new ConcurrentHashMap<>())
        .put(objectClassHandle, attributeIds);

    System.err.println("[FederationExecution] Federate " + federateName + " subscribed to object class " + objectClassHandle + " with attributes: " + attributeIds);
  }

  /**
   * Get the subscribed attributes for a federate's object class.
   *
   * @param federateName The name of the federate
   * @param objectClassHandle The object class handle
   * @return Comma-separated attribute IDs, or null if not subscribed
   */
  public String getSubscribedAttributes(String federateName, String objectClassHandle) {
    Map<String, String> federateSubscription = federateSubscriptions.get(federateName);
    if (federateSubscription == null) {
      return null;
    }
    return federateSubscription.get(objectClassHandle);
  }

  /**
   * Get all federates subscribed to an object class.
   *
   * @param objectClassHandle The object class handle
   * @return Map of federateName -> attributeIds for all federates subscribed to this class
   */
  public Map<String, String> getSubscribedFederates(String objectClassHandle) {
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, Map<String, String>> entry : federateSubscriptions.entrySet()) {
      String federateName = entry.getKey();
      Map<String, String> subscriptions = entry.getValue();
      if (subscriptions.containsKey(objectClassHandle)) {
        result.put(federateName, subscriptions.get(objectClassHandle));
      }
    }
    return result;
  }

  /**
   * Publish a federate to object class attributes.
   *
   * @param federateName The name of the federate
   * @param objectClassHandle The object class handle
   * @param attributeIds Comma-separated attribute IDs (e.g., "1,2,3")
   */
  public void publishObjectClass(String federateName, String objectClassHandle, String attributeIds) {
    federatePublications.computeIfAbsent(federateName, k -> new ConcurrentHashMap<>())
        .put(objectClassHandle, attributeIds);
    
    System.err.println("[FederationExecution] Federate " + federateName + " published to object class " + objectClassHandle + " with attributes: " + attributeIds);
  }

  // /**
  //  * Get the published attributes for a federate's object class.
  //  *
  //  * @param federateName The name of the federate
  //  * @param objectClassHandle The object class handle
  //  * @return Comma-separated attribute IDs, or null if not publishing
  //  */
  // public String getPublishedAttributes(String federateName, String objectClassHandle) {
  //   Map<String, String> federatePublication = federatePublications.get(federateName);
  //   if (federatePublication == null) {
  //     return null;
  //   }
  //   return federatePublication.get(objectClassHandle);
  // }

  // /**
  //  * Get all federates publishing an object class.
  //  *
  //  * @param objectClassHandle The object class handle
  //  * @return Map of federateName -> attributeIds for all federates publishing this class
  //  */
  // public Map<String, String> getPublishingFederates(String objectClassHandle) {
  //   Map<String, String> result = new HashMap<>();
  //   for (Map.Entry<String, Map<String, String>> entry : federatePublications.entrySet()) {
  //     String federateName = entry.getKey();
  //     Map<String, String> publications = entry.getValue();
  //     if (publications.containsKey(objectClassHandle)) {
  //       result.put(federateName, publications.get(objectClassHandle));
  //     }
  //   }
  //   return result;
  // }
}