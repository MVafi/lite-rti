package nl.literti.messages.requests;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.ObjectInstanceHandle;
import nl.literti.messages.MessageObject;

public class RequestAttributeValueUpdate extends MessageObject {

  private final ObjectInstanceHandle objectInstanceHandle;
  private final AttributeHandleSet theAttributes;
  private final byte[] userSuppliedTag;

  public RequestAttributeValueUpdate(
      ObjectInstanceHandle objectInstanceHandle,
      AttributeHandleSet theAttributes,
      byte[] userSuppliedTag,
      int msgHandle) {
    super(msgHandle);
    this.objectInstanceHandle = objectInstanceHandle;
    this.theAttributes = theAttributes;
    this.userSuppliedTag = userSuppliedTag;
  }

  public ObjectInstanceHandle getObjectInstanceHandle() {
    return objectInstanceHandle;
  }

  public AttributeHandleSet getTheAttributes() {
    return theAttributes;
  }

  public byte[] getUserSuppliedTag() {
    return userSuppliedTag;
  }
}
