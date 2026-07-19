package nl.tno.netnbus.client.requests;

import java.util.HashMap;
import java.util.Map;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import nl.tno.netnbus.impl.AttributeHandleImpl;

// TODO: check this file
public class UpdateAttributeValuesRequest extends MessageObject {
  
  // Serialization ID for this class
  private static final long serialVersionUID = 103117101109121L;

  private final int objectInstanceHandle;
  // Store attributes as a serializable map: handle (as Integer) -> byte[]
  private final Map<Integer, byte[]> attributeValues;
  private final byte[] userSuppliedTag;

  public UpdateAttributeValuesRequest(
      int objectInstanceHandle,
      AttributeHandleValueMap theAttributes,
      byte[] userSuppliedTag,
      int msgHandle) {
    super(msgHandle);
    this.objectInstanceHandle = objectInstanceHandle;
    this.userSuppliedTag = userSuppliedTag;
    
    // Convert AttributeHandleValueMap to a serializable Map<Integer, byte[]>
    this.attributeValues = new HashMap<>();
    if (theAttributes != null && !theAttributes.isEmpty()) {
      for (AttributeHandle handle : theAttributes.keySet()) {
        byte[] value = theAttributes.get(handle);
        // Extract the integer value from the handle
        int handleValue = ((AttributeHandleImpl) handle).getHandle();
        this.attributeValues.put(handleValue, value);
      }
    }
  }

  public int getObjectInstanceHandle() {
    return objectInstanceHandle;
  }

  public Map<Integer, byte[]> getAttributeValues() {
    return attributeValues;
  }

  public byte[] getUserSuppliedTag() {
    return userSuppliedTag;
  }
}
