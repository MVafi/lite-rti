package nl.literti.messages.requests;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.ObjectClassHandle;
import nl.literti.messages.MessageObject;

public class RequestPublishObject extends MessageObject {

  private ObjectClassHandle ObjectClass;
  private AttributeHandleSet AttributeList;
    
  public RequestPublishObject(ObjectClassHandle theClass, AttributeHandleSet attributeList, int requestHandle) {
    super(requestHandle);
    this.ObjectClass = theClass;
    this.AttributeList = attributeList;
  }

  public ObjectClassHandle getObjectClassHandle() {
    return this.ObjectClass;
  }

  public AttributeHandleSet getAttributeHandleSet() {
    return this.AttributeList;
  }
}
