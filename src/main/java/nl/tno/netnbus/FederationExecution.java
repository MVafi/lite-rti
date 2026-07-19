package nl.tno.netnbus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import nl.tno.netnbus.fom.FederationObjectModel;

// TODO: create handleRegistery that keeps tracks of all the int handles for object classes, attributes, interactions, parameters, etc.

public class FederationExecution {
  private final String name;
  private final Map<String, String> joinedFederates = new ConcurrentHashMap<>(); // federateName -> federateType
  private final long createdAt = System.currentTimeMillis();
  private FederationObjectModel fom; // The Federation Object Model

  // ------------- PUB SUB ----------------
  // Store subscriptions: federateName -> (objectClassHandle -> comma-separated attribute IDs)
  private final Map<String, Map<String, String>> federateSubscriptions = new ConcurrentHashMap<>();
  
  // Store publications: federateName -> (objectClassHandle -> comma-separated attribute IDs)
  private final Map<String, Map<String, String>> federatePublications = new ConcurrentHashMap<>();

  // ------------- Object registration ----------------
  // Counter for generating unique object instance handles
  private final AtomicInteger nextObjectInstanceHandle = new AtomicInteger(3000);

  // Store registered object instances: objectInstanceName -> objectInstanceHandle
  private final Map<String, Integer> registeredObjectInstances = new ConcurrentHashMap<>();
  
  // Track which federate owns each object instance: objectInstanceHandle -> federateName
  private final Map<Integer, String> objectInstanceOwner = new ConcurrentHashMap<>();
  
  // Track which object class each instance belongs to: objectInstanceHandle -> objectClassHandle
  private final Map<Integer, String> objectInstanceClass = new ConcurrentHashMap<>();


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

  // public HandleRegistry getHandleRegistry() {
  //   return handleRegistry;
  // }

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

  /**
   * Register an object instance with an auto-generated name.
   *
   * @param federateName The name of the federate registering the object
   * @param objectClassHandle The object class handle
   * @return The object instance handle assigned to this instance
   * @throws IllegalArgumentException if the federate has not published the object class
   */
  public int registerObjectInstance(String federateName, String objectClassHandle) {

    // Verify federate has published this object class
    Map<String, String> fedPubs = federatePublications.get(federateName);
    if (fedPubs == null || !fedPubs.containsKey(objectClassHandle)) {
      throw new IllegalArgumentException(
          "Federate '" + federateName + "' has not published object class " + objectClassHandle);
    }

    // Generate new unique handle and auto-name for the object instance
    int handle = nextObjectInstanceHandle.getAndIncrement();
    String autoName = objectClassHandle + "_instance_" + handle;

    // Register new object instance within map, its owner federate, and its class
    registeredObjectInstances.put(autoName, handle);
    objectInstanceOwner.put(handle, federateName);
    objectInstanceClass.put(handle, objectClassHandle);
    
    System.err.println("[FederationExecution] Federate " + federateName + " registered object instance: " + autoName + " with handle: " + handle);
    return handle;
  }

  // /**
  //  * Register an object instance with a specific name.
  //  *
  //  * @param federateName The name of the federate registering the object
  //  * @param objectClassHandle The object class handle
  //  * @param objectInstanceName The specific name for this object instance
  //  * @return The object instance handle assigned to this instance
  //  * @throws IllegalArgumentException if the federate has not published the object class or if the name is already in use
  //  */
  // public int registerObjectInstance(String federateName, String objectClassHandle, String objectInstanceName) {
  //   // Verify federate has published this object class
  //   Map<String, String> fPubs = federatePublications.get(federateName);
  //   if (fPubs == null || !fPubs.containsKey(objectClassHandle)) {
  //     throw new IllegalArgumentException(
  //         "Federate '" + federateName + "' has not published object class " + objectClassHandle);
  //   }

  //   // Verify object instance name is not already in use
  //   if (registeredObjectInstances.containsKey(objectInstanceName)) {
  //     throw new IllegalArgumentException(
  //         "Object instance name '" + objectInstanceName + "' is already in use");
  //   }

  //   // Generate new unique handle for the object instance
  //   int handle = nextObjectInstanceHandle.getAndIncrement();

  //   // Register new object instance within map, and its owner federate
  //   registeredObjectInstances.put(objectInstanceName, handle);
  //   objectInstanceOwner.put(handle, federateName);
    
  //   System.err.println("[FederationExecution] Federate " + federateName + " registered object instance: " + objectInstanceName + " with handle: " + handle);
  //   return handle;
  // }

  /**
   * Get the handle for a registered object instance.
   *
   * @param objectInstanceName The name of the object instance
   * @return The object instance handle, or -1 if not found
   */
  // public int getObjectInstanceHandle(String objectInstanceName) {
  //   Integer handle = registeredObjectInstances.get(objectInstanceName);
  //   return handle != null ? handle : -1;
  // }

  /**
   * Get the name for a registered object instance handle.
   *
   * @param objectInstanceHandle The object instance handle
   * @return The object instance name, or null if not found
   */
  public String getObjectInstanceName(int objectInstanceHandle) {
    for (Map.Entry<String, Integer> entry : registeredObjectInstances.entrySet()) {
      if (entry.getValue().equals(objectInstanceHandle)) {
        return entry.getKey();
      }
    }
    return null;
  }

  /**
   * Get the owner (federate) of an object instance.
   *
   * @param objectInstanceHandle The object instance handle
   * @return The federate name that owns this instance, or null if not found
   */
  // public String getObjectInstanceOwner(int objectInstanceHandle) {
  //   return objectInstanceOwner.get(objectInstanceHandle);
  // }

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

  /**
   * Get the object class handle for an object instance.
   *
   * @param objectInstanceHandle The object instance handle
   * @return The object class handle, or null if not found
   */
  public String getObjectInstanceClass(int objectInstanceHandle) {
    return objectInstanceClass.get(objectInstanceHandle);
  }
}