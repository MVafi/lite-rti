package nl.literti.messages.requests;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import nl.literti.messages.MessageObject;

// TODO: check this file
public class UpdateAttributeValues extends MessageObject {

  private final ObjectInstanceHandle objectInstanceHandle;
  private final AttributeHandleValueMap theAttributes;
  private final byte[] userSuppliedTag;

  public UpdateAttributeValues(
      ObjectInstanceHandle objectInstanceHandle,
      AttributeHandleValueMap theAttributes,
      byte[] userSuppliedTag,
      int msgHandle) {
    super(msgHandle);
    this.objectInstanceHandle = objectInstanceHandle;
    this.theAttributes = theAttributes;
    this.userSuppliedTag = userSuppliedTag;
    
    // // Convert AttributeHandleValueMap to a serializable Map<Integer, byte[]>
    // this.attributeValues = new HashMap<>();
    // if (theAttributes != null && !theAttributes.isEmpty()) {
    //   for (AttributeHandle handle : theAttributes.keySet()) {
    //     byte[] value = theAttributes.get(handle);
    //     // Extract the integer value from the handle
    //     int handleValue = ((AttributeHandleImpl) handle).getHandle();
    //     this.attributeValues.put(handleValue, value);
    //   }
    // }
  }

  public ObjectInstanceHandle getObjectInstanceHandle() {
    return this.objectInstanceHandle;
  }

  public AttributeHandleValueMap getAttributeValues() {
    return this.theAttributes;
  }

  public byte[] getUserSuppliedTag() {
    return this.userSuppliedTag;
  }
}
