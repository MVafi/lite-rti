package nl.literti.messages.responses;

import hla.rti1516e.ObjectInstanceHandle;
import nl.literti.messages.MessageObject;

public class ResponseRegisterObjectInstance extends MessageObject {

  ObjectInstanceHandle objectInstanceHandle;

  public ResponseRegisterObjectInstance(int msgHandle, ObjectInstanceHandle objectInstanceHandle) {
    super(msgHandle);
    this.objectInstanceHandle = objectInstanceHandle;
  }

  public ObjectInstanceHandle getObjectInstanceHandle() {
    return objectInstanceHandle;
  }
}
