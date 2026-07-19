package nl.tno.netnbus.client;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import nl.tno.netnbus.fom.FederationObjectModel;
import nl.tno.netnbus.fom.FomObjectClass;
import nl.tno.netnbus.impl.AttributeHandleImpl;
import nl.tno.netnbus.impl.ObjectClassHandleImpl;

/** Context for keeping track of the states and events of the client and its singular federate */
public class NetnBusClientContext {

  private FederationObjectModel fom;                  // Fom stored at client side (deserialized ObjectModel object in memory)
  private FederateAmbassador federateAmbassador;      // RTI reference for delivering callbacks to the federate

  public void setLocalFOM(FederationObjectModel fom) {
    this.fom = fom;
  }

  public FederationObjectModel getLocalFOM() {
    return this.fom;
  }

  /**
   * Get the ObjectClassHandle for a given object class name from the FOM.
   *
   * @param objectClassName The name of the object class
   * @return ObjectClassHandle wrapper for the class
   * @throws NameNotFound if the object class is not found in the FOM
   * @throws RuntimeException if FOM is not loaded
   */
  public ObjectClassHandle getObjectClassHandle(String objectClassName) throws NameNotFound {
    if (fom == null) {
      throw new RuntimeException("FOM not loaded - federate may not be joined to federation");
    }

    int handle = fom.getObjectClassHandle(objectClassName);
    if (handle == FederationObjectModel.INVALID_HANDLE) {
      throw new NameNotFound("Object class '" + objectClassName + "' not found in FOM");
    }

    ObjectClassHandle och = new ObjectClassHandleImpl(handle);
    System.out.println("[NetnBusClientContext] getObjectClassHandle: " + objectClassName + " -> " + handle);
    return och;
  }

  /**
   * Get the AttributeHandle for a given attribute name within an object class from the FOM.
   *
   * @param whichClass The object class handle
   * @param attributeName The name of the attribute
   * @return AttributeHandle wrapper for the attribute
   * @throws NameNotFound if the attribute is not found in the object class
   * @throws RuntimeException if FOM is not loaded
   */
  public AttributeHandle getAttributeHandle(ObjectClassHandle whichClass, String attributeName) throws NameNotFound {
    if (fom == null) {
      throw new RuntimeException("FOM not loaded - unable to obtain attribute handle for " + attributeName);
    }

    // Extract the int handle from ObjectClassHandle
    int classHandle = ((ObjectClassHandleImpl) whichClass).getHandle();

    // Get the object class from FOM
    FomObjectClass objectClass = fom.getObjectClass(classHandle);
    if (objectClass == null) {
      throw new NameNotFound("Object class not found with handle: " + classHandle);
    }

    System.out.println("[NetnBusClientContext] getAttributeHandle: looking up attribute '" + attributeName + "' in object class '" + objectClass.getQualifiedName() + "' (handle: " + classHandle + ")");

    // Look up the attribute by name
    int attributeHandle = objectClass.getAttributeHandle(attributeName);
    if (attributeHandle == FederationObjectModel.INVALID_HANDLE) {
      throw new NameNotFound("Attribute '" + attributeName + "' not found in object class");
    }

    // Return wrapped handle
    System.out.println("[NetnBusClientContext] getAttributeHandle: " + attributeName + " -> " + attributeHandle);
    return new AttributeHandleImpl(attributeHandle);
  }

  public void setFederateAmbassador(FederateAmbassador ambassador) {
    this.federateAmbassador = ambassador;
  }

  public FederateAmbassador getFederateAmbassador() {
    return this.federateAmbassador;
  }

  // /**
  //  * Deliver reflected attribute values from the server to the federate via its ambassador.
  //  * Called by the client receiver when attribute updates arrive from other federates.
  //  *
  //  * @param request The UpdateAttributeValuesRequest containing object handle and attribute values
  //  */
  // public void deliverReflectedAttributeValues(UpdateAttributeValuesRequest request) {
  //   if (federateAmbassador == null) {
  //     System.err.println("[NetnBusClientContext] No federate ambassador set, cannot deliver reflected attributes");
  //     return;
  //   }

  //   try {
  //     int objectInstanceHandle = request.getObjectInstanceHandle();
  //     Map<Integer, byte[]> attributeValues = request.getAttributeValues();

  //     // Convert Map<Integer, byte[]> to AttributeHandleValueMap
  //     AttributeHandleValueMap attrMap = new nl.tno.netnbus.utils.AttributeHandleValueMapImpl();
      
  //     for (Map.Entry<Integer, byte[]> entry : attributeValues.entrySet()) {
  //       int attrHandle = entry.getKey();
  //       byte[] value = entry.getValue();
  //       attrMap.put(new AttributeHandleImpl(attrHandle), value);
  //     }

  //     // Create ObjectInstanceHandle from the integer
  //     ObjectInstanceHandle objHandle = new ObjectInstanceHandleImpl(objectInstanceHandle);

  //     // Call the federate ambassador's reflectAttributeValues callback
  //     federateAmbassador.reflectAttributeValues(
  //         objHandle,
  //         attrMap,
  //         null, // userSuppliedTag
  //         OrderType.RECEIVE,
  //         null, // transportationTypeHandle
  //         null); // reflectInfo

  //     System.out.println(
  //         "[NetnBusClientContext] Delivered reflected attribute values for object instance: "
  //             + objectInstanceHandle);
  //   } catch (Exception e) {
  //     System.err.println(
  //         "[NetnBusClientContext] Error delivering reflected attribute values: " + e.getMessage());
  //   }
  // }

}
