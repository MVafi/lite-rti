package nl.literti;

import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
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

  // ------------- CONNECTION ----------------

  // Store the socket for each federate: federateName -> Socket
  private final Map<String, Socket> federateName2Socket = new ConcurrentHashMap<>();

  // ------------- PUB SUB ----------------
  // todo, might not need this many maps

  // Store subscriptions: federateName -> (objectClassHandle -> attributeHandleSet)
  private final Map<String, Map<ObjectClassHandle, AttributeHandleSet>> federateSubscriptions =
      new ConcurrentHashMap<>();
  
  private final Map<String, Set<InteractionClassHandle>> federateInteractionSubscriptions =
    new ConcurrentHashMap<>();

  // Reverse subscription map: objectClassHandle -> (federateName -> attributeHandleSet)
  private final Map<ObjectClassHandle, Map<String, AttributeHandleSet>> objectClassSubscribers =
      new ConcurrentHashMap<>();

  // Store publications: federateName -> (objectClassHandle -> attributeHandleSet)
  private final Map<String, Map<ObjectClassHandle, AttributeHandleSet>> federatePublications =
      new ConcurrentHashMap<>();

  // Store publications: federateName -> Set<interactionClassHandle>
  private final Map<String, Set<InteractionClassHandle>> federatePubInteractions =
      new ConcurrentHashMap<>();      

  // Reverse publication map: objectClassHandle -> (federateName -> attributeHandleSet)
  private final Map<ObjectClassHandle, Map<String, AttributeHandleSet>> objectClassPublishers =
      new ConcurrentHashMap<>();

  // Reverse publication map: interactionClassHandle -> Set<federateName>
  private final Map<InteractionClassHandle, Set<String>> interactionClassPublishers =
      new ConcurrentHashMap<>();
  
  // Reverse subscription map: interactionClassHandle -> Set<federateName>
  private final Map<InteractionClassHandle, Set<String>> interactionClassSubscribers =
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

  // ------------- Interaction counters ----------------
  // Track interactions sent per federate: federateName -> count
  private final Map<String, AtomicInteger> debugInteractionsSent = new ConcurrentHashMap<>();

  // Track interactions received per federate: federateName -> count
  private final Map<String, AtomicInteger> debugInteractionsReceived = new ConcurrentHashMap<>();

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

  public void addFederate(String federateName, String federateType, Socket socket) {
    joinedFederates.put(
        federateName,
        federateType); // TODO: currently only federateType is used, federateName is not support in
                       // the whole application
    federateName2Socket.put(federateName, socket);
  }

  public String removeFederate(String federateName) {
    String federateType = joinedFederates.get(federateName);
    joinedFederates.remove(federateName);
    federateName2Socket.remove(federateName);

    // Clear federate from subscription map
    Map<ObjectClassHandle, AttributeHandleSet> fedSubs = federateSubscriptions.remove(federateName);
    if (fedSubs != null) {
      // Clear from objectClass-centric subscription map
      for (ObjectClassHandle objClass : fedSubs.keySet()) {
        Map<String, AttributeHandleSet> classSubscribers = objectClassSubscribers.get(objClass);
        if (classSubscribers != null) {
          classSubscribers.remove(federateName);
          if (classSubscribers.isEmpty()) {
            objectClassSubscribers.remove(objClass);
          }
        }
      }
      // Do the same for interactions
    }

    // Also remove publications to prevent them from persisting
    // TODO: check this code ...
    // Map<ObjectClassHandle, AttributeHandleSet> fedPubs = federatePublications.remove(federateName);
    // if (fedPubs != null) {
    //   // Clear from objectClass-centric publication map
    //   for (ObjectClassHandle objClass : fedPubs.keySet()) {
    //     Map<String, AttributeHandleSet> classPublishers = objectClassPublishers.get(objClass);
    //     if (classPublishers != null) {
    //       classPublishers.remove(federateName);
    //       if (classPublishers.isEmpty()) {
    //         objectClassPublishers.remove(objClass);
    //       }
    //     }
    //   }
    // }

    return federateType;
  }

  public Set<String> getJoinedFederateKeys() {
    return Set.copyOf(joinedFederates.keySet());
  }

  public Set<String> getJoinedFederateTypes() {
    return Set.copyOf(joinedFederates.values());
  }

  public void subscribeToObjectClass(
      String federateType, ObjectClassHandle objectClassHandle, AttributeHandleSet attributeIds) {
    // Store a clone of the attributeIds to avoid mutation issues
    // TODO: is this cloning necessary?
    AttributeHandleSet storedAttributes = attributeIds != null ? attributeIds.clone() : null;
    
    // Update federate-centric map
    federateSubscriptions
        .computeIfAbsent(federateType, k -> new ConcurrentHashMap<>())
        .put(objectClassHandle, storedAttributes);

    // Update objectClass-centric map
    objectClassSubscribers
        .computeIfAbsent(objectClassHandle, k -> new ConcurrentHashMap<>())
        .put(federateType, storedAttributes);

    System.err.println(
        "[FederationExecution] Federate "
            + federateType
            + " subscribed to object class "
            + objectClassHandle
            + " with attributes: "
            + attributeIds);
  }

  public void subscribeToInteractionClass(String federateType, InteractionClassHandle interactionClassHandle) {
    // TODO these maps could be better maybe
    // Update federate-centric map
    federateInteractionSubscriptions
        .computeIfAbsent(federateType, k -> ConcurrentHashMap.newKeySet())
        .add(interactionClassHandle);

    // Update interaction-centric map
    interactionClassSubscribers
        .computeIfAbsent(interactionClassHandle, k -> ConcurrentHashMap.newKeySet())
        .add(federateType);

    System.err.println(
      "[FederationExecution] Federate "
      + federateType
      + " subscribed to intercation class "
      + interactionClassHandle);
  }

  // public AttributeHandleSet getSubscribedAttributes(
  //     String federateName, ObjectClassHandle objectClassHandle) {
  //   Map<ObjectClassHandle, AttributeHandleSet> federateSubscription =
  //       federateSubscriptions.get(federateName);
  //   if (federateSubscription == null) {
  //     return null;
  //   }
  //   return federateSubscription.get(objectClassHandle);
  // }

  // public Map<String, AttributeHandleSet> getSubscribedFederates(
  //     ObjectClassHandle objectClassHandle) {
  //   Map<String, AttributeHandleSet> result = new HashMap<>();
  //   for (Map.Entry<String, Map<ObjectClassHandle, AttributeHandleSet>> entry : federateSubscriptions.entrySet()) {
  //     String federateName = entry.getKey();
  //     Map<ObjectClassHandle, AttributeHandleSet> subscriptions = entry.getValue();
  //     if (subscriptions.containsKey(objectClassHandle)) {
  //       result.put(federateName, subscriptions.get(objectClassHandle));
  //     }
  //   }
  //   return result;
  // }

  public void publishObjectClass(
      String federateName, ObjectClassHandle objectClassHandle, AttributeHandleSet attributeIds) {
    // Store a clone of the attributeIds to avoid mutation issues
    // TODO: is this cloning necessary?
    AttributeHandleSet storedAttributes = attributeIds != null ? attributeIds.clone() : null;

    // Update federate-centric map
    this.federatePublications
        .computeIfAbsent(federateName, k -> new ConcurrentHashMap<>())
        .put(objectClassHandle, storedAttributes);

    // Update objectClass-centric map
    this.objectClassPublishers
        .computeIfAbsent(objectClassHandle, k -> new ConcurrentHashMap<>())
        .put(federateName, storedAttributes);

    System.err.println(
        "[FederationExecution] Federate "
            + federateName
            + " published to object class "
            + objectClassHandle
            + " with attributes: "
            + storedAttributes);
  }

  public void publishInteractionClass(
      String federateName, InteractionClassHandle interactionClassHandle) {
    // Update federate-centric map
    this.federatePubInteractions
        .computeIfAbsent(federateName, k -> ConcurrentHashMap.newKeySet())
        .add(interactionClassHandle);

    // Update interaction-centric map
    this.interactionClassPublishers
        .computeIfAbsent(interactionClassHandle, k -> ConcurrentHashMap.newKeySet())
        .add(federateName);

    System.err.println(
        "[FederationExecution] Federate "
            + federateName
            + " published interaction class "
            + interactionClassHandle);
  }

  public boolean isInteractionPubBy(String federateName, InteractionClassHandle interactionClassHandle){
    Set<InteractionClassHandle> pubInteractions = federatePubInteractions.get(federateName);
    return pubInteractions != null && pubInteractions.contains(interactionClassHandle);
  }

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

  public String getObjectInstanceName(ObjectInstanceHandle objectInstanceHandle) {
    for (Map.Entry<String, ObjectInstanceHandle> entry : registeredObjectInstances.entrySet()) {
      if (entry.getValue().equals(objectInstanceHandle)) {
        return entry.getKey();
      }
    }
    return null;
  }

  public ObjectClassHandle getObjectInstanceClass(ObjectInstanceHandle objectInstanceHandle) {
    return objectInstanceClass.get(objectInstanceHandle);
  }

  public Set<ObjectInstanceHandle> getObjectInstancesForClass(ObjectClassHandle objectClassHandle) {
    Set<ObjectInstanceHandle> instances = new java.util.HashSet<>();
    for (Map.Entry<ObjectInstanceHandle, ObjectClassHandle> entry : objectInstanceClass.entrySet()) {
      if (entry.getValue().equals(objectClassHandle)) {
        instances.add(entry.getKey());
      }
    }
    return instances;
  }

  public Socket getFederateSocket(String federateType) {
    return federateName2Socket.get(federateType);
  }

  public Map<String, AttributeHandleSet> getObjectClassSubscribedFederates(ObjectClassHandle objectClassHandle) {
    return objectClassSubscribers.getOrDefault(objectClassHandle, Map.of());
  }

  public Set<String> getInteractionClassSubscribedFederates(InteractionClassHandle interactionClassHandle) {
    return interactionClassSubscribers.getOrDefault(interactionClassHandle, Set.of());
  }

  // Debug method, should not be used in production
  public Set<String> debugGetObjectInstancesForFederate(String federateName) {
    Set<String> instances = new java.util.HashSet<>();
    for (Map.Entry<ObjectInstanceHandle, String> entry : objectInstanceOwner.entrySet()) {
      if (entry.getValue().equals(federateName)) {
        ObjectInstanceHandle handle = entry.getKey();
        String instanceName = getObjectInstanceName(handle);
        if (instanceName != null) {
          instances.add(instanceName);
        }
      }
    }
    return instances;
  }

  // Debug method, returns object instances with their class names
  public java.util.List<Map<String, String>> debugGetObjectInstancesWithClassForFederate(String federateName) {
    java.util.List<Map<String, String>> instances = new java.util.ArrayList<>();
    for (Map.Entry<ObjectInstanceHandle, String> entry : objectInstanceOwner.entrySet()) {
      if (entry.getValue().equals(federateName)) {
        ObjectInstanceHandle handle = entry.getKey();
        String instanceName = getObjectInstanceName(handle);
        ObjectClassHandle classHandle = objectInstanceClass.get(handle);
        
        if (instanceName != null && classHandle != null) {
          String className = fom != null ? fom.getObjectClassName(classHandle.hashCode()) : null;
          
          Map<String, String> instanceInfo = new java.util.LinkedHashMap<>();
          instanceInfo.put("instance", instanceName);
          instanceInfo.put("class", className != null ? className : "Unknown");
          instances.add(instanceInfo);
        }
      }
    }
    return instances;
  }

  public void debugIncrementInteractionsSent(String federateName) {
    debugInteractionsSent.computeIfAbsent(federateName, k -> new AtomicInteger(0)).incrementAndGet();
  }

  public void debugIncrementInteractionsReceived(String federateName) {
    debugInteractionsReceived.computeIfAbsent(federateName, k -> new AtomicInteger(0)).incrementAndGet();
  }

  public long debugGetInteractionsSentCount(String federateName) {
    return debugInteractionsSent.getOrDefault(federateName, new AtomicInteger(0)).get();
  }

  public long debugGetInteractionsReceivedCount(String federateName) {
    return debugInteractionsReceived.getOrDefault(federateName, new AtomicInteger(0)).get();
  }
}
