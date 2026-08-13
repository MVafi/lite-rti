package nl.literti.messages.requests;

import hla.rti1516e.ObjectClassHandle;
import nl.literti.messages.MessageObject;

public class RequestRegisterObjectInstance extends MessageObject {

  private ObjectClassHandle ObjectClass;
  private String ObjectName;
    
  public RequestRegisterObjectInstance(ObjectClassHandle theClass, String theObjectName, int requestHandle) {
    super(requestHandle);
    this.ObjectClass = theClass;
    this.ObjectName = theObjectName;
  }

  public ObjectClassHandle getObjectClassHandle() {
    return this.ObjectClass;
  }

  public String getObjectName() {
    return this.ObjectName;
  }
    
}
