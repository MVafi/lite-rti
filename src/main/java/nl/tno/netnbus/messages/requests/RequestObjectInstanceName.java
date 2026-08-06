package nl.tno.netnbus.messages.requests;

import hla.rti1516e.ObjectInstanceHandle;
import nl.tno.netnbus.messages.MessageObject;

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
