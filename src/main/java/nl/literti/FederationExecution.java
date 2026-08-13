package nl.literti;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import nl.literti.fom.FederationObjectModel;
import nl.literti.impl.ObjectInstanceHandleImpl;

// TODO: create handleRegistery that keeps tracks of all the int handles for object classes,
// attributes, interactions, parameters, etc.

public class FederationExecution {
  private final String name;
  private final Map<String, String> joinedFederates =
      new ConcurrentHashMap<>(); // federateName -> federateType
  private final long createdAt = System.currentTimeMillis();
  private FederationObjectModel fom; // The Federation Object Model

  // ------------- PUB SUB ----------------
  // Store subscriptions: federateName -> (objectClassHandle -> attributeHandleSet)
  private final Map<String, Map<ObjectClassHandle, AttributeHandleSet>> federateSubscriptions =
      new ConcurrentHashMap<>();

  // Store publications: federateName -> (objectClassHandle -> attributeHandleSet)
  private final Map<String, Map<ObjectClassHandle, AttributeHandleSet>> federatePublications =
      new ConcurrentHashMap<>();

  // ------------- Object registration ----------------
  // Counter for generating unique object instance handles
  private final AtomicInteger nextHandleValue = new AtomicInteger(3000);

  // Store registered object instances: objectInstanceName -> objectInstanceHandle
  private final Map<String, ObjectInstanceHandle> registeredObjectInstances =
      new ConcurrentHashMap<>();

  // Track which federate owns each object instance: objectInstanceHandle -> federateName
  private final Map<ObjectInstanceHandle, String> objectInstanceOwner = new ConcurrentHashMap<>();

  // Track which object class each instance belongs to: objectInstanceHandle -> objectClassHandle
  private final Map<ObjectInstanceHandle, ObjectClassHandle> objectInstanceClass =
      new ConcurrentHashMap<>();

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

  public void addFederate(String federateName, String federateType) {
    joinedFederates.put(
        federateName,
        federateType); // TODO: currently only federateType is used, federateName is not support in
                       // the whole application
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

  public void subscribeToObjectClass(
      String federateType, ObjectClassHandle objectClassHandle, AttributeHandleSet attributeIds) {
    // Store a clone of the attributeIds to avoid mutation issues
    // TODO: is this cloning necessary?
    AttributeHandleSet storedAttributes = attributeIds != null ? attributeIds.clone() : null;
    federateSubscriptions
        .computeIfAbsent(federateType, k -> new ConcurrentHashMap<>())
        .put(objectClassHandle, storedAttributes);

    System.err.println(
        "[FederationExecution] Federate "
            + federateType
            + " subscribed to object class "
            + objectClassHandle
            + " with attributes: "
            + attributeIds);
  }

  public AttributeHandleSet getSubscribedAttributes(
      String federateName, ObjectClassHandle objectClassHandle) {
    Map<ObjectClassHandle, AttributeHandleSet> federateSubscription =
        federateSubscriptions.get(federateName);
    if (federateSubscription == null) {
      return null;
    }
    return federateSubscription.get(objectClassHandle);
  }

  public Map<String, AttributeHandleSet> getSubscribedFederates(
      ObjectClassHandle objectClassHandle) {
    Map<String, AttributeHandleSet> result = new HashMap<>();
    for (Map.Entry<String, Map<ObjectClassHandle, AttributeHandleSet>> entry :
        federateSubscriptions.entrySet()) {
      String federateName = entry.getKey();
      Map<ObjectClassHandle, AttributeHandleSet> subscriptions = entry.getValue();
      if (subscriptions.containsKey(objectClassHandle)) {
        result.put(federateName, subscriptions.get(objectClassHandle));
      }
    }
    return result;
  }

  public void publishObjectClass(
      String federateName, ObjectClassHandle objectClassHandle, AttributeHandleSet attributeIds) {
    // Store a clone of the attributeIds to avoid mutation issues
    // TODO: is this cloning necessary?
    AttributeHandleSet storedAttributes = attributeIds != null ? attributeIds.clone() : null;

    federatePublications
        .computeIfAbsent(federateName, k -> new ConcurrentHashMap<>())
        .put(objectClassHandle, storedAttributes);

    System.err.println(
        "[FederationExecution] Federate "
            + federateName
            + " published to object class "
            + objectClassHandle
            + " with attributes: "
            + storedAttributes);
  }

  /**
   * Register an object instance with an auto-generated name.
   *
   * @param federateName The name of the federate registering the object
   * @param objectClassHandle The object class handle
   * @return The object instance handle assigned to this instance
   * @throws IllegalArgumentException if the federate has not published the object class
   */
  public ObjectInstanceHandle registerObjectInstance(
      String federateName, ObjectClassHandle objectClassHandle) {

    // Verify federate has published this object class
    Map<ObjectClassHandle, AttributeHandleSet> fedPubs = federatePublications.get(federateName);
    if (fedPubs == null || !fedPubs.containsKey(objectClassHandle)) {
      throw new IllegalArgumentException(
          "Federate '" + federateName + "' has not published object class " + objectClassHandle);
    }

    // Generate new unique handle and auto-name for the object instance
    int handleValue = nextHandleValue.getAndIncrement();
    ObjectInstanceHandle handle = new ObjectInstanceHandleImpl(handleValue);
    String autoName = objectClassHandle + "_instance_" + handleValue;

    // Register new object instance within map, its owner federate, and its class
    registeredObjectInstances.put(autoName, handle);
    objectInstanceOwner.put(handle, federateName);
    objectInstanceClass.put(handle, objectClassHandle);

    System.err.println(
        "[FederationExecution] Federate "
            + federateName
            + " registered object instance: "
            + autoName
            + " with handle: "
            + handle);
    return handle;
  }

  // /**
  //  * Register an object instance with a specific name.
  //  *
  //  * @param federateName The name of the federate registering the object
  //  * @param objectClassHandle The object class handle
  //  * @param objectInstanceName The specific name for this object instance
  //  * @return The object instance handle assigned to this instance
  //  * @throws IllegalArgumentException if the federate has not published the object class or if
  // the name is already in use
  //  */
  // public int registerObjectInstance(String federateName, String objectClassHandle, String
  // objectInstanceName) {
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

  //   System.err.println("[FederationExecution] Federate " + federateName + " registered object
  // instance: " + objectInstanceName + " with handle: " + handle);
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
  public String getObjectInstanceName(ObjectInstanceHandle objectInstanceHandle) {
    for (Map.Entry<String, ObjectInstanceHandle> entry : registeredObjectInstances.entrySet()) {
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
  public ObjectClassHandle getObjectInstanceClass(ObjectInstanceHandle objectInstanceHandle) {
    return objectInstanceClass.get(objectInstanceHandle);
  }
}
