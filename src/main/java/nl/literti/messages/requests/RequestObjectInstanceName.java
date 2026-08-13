package nl.literti.messages.requests;

import hla.rti1516e.ObjectInstanceHandle;
import nl.literti.messages.MessageObject;

public class RequestObjectInstanceName extends MessageObject {

  private ObjectInstanceHandle objectInstanceHandle;

  public RequestObjectInstanceName(ObjectInstanceHandle theHandle, int requestHandle) {
    super(requestHandle);
    this.objectInstanceHandle = theHandle;
  }

  public ObjectInstanceHandle getObjectInstanceHandle() {
    return this.objectInstanceHandle;
  }
    
}
