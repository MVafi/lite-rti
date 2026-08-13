package nl.literti.messages.responses;

import nl.literti.messages.MessageObject;

public class ResponseObjectInstanceName extends MessageObject {

    String objectInstanceName;

  public ResponseObjectInstanceName(int msgHandle, String objectInstanceName) {
    super(msgHandle);
    this.objectInstanceName = objectInstanceName;
  }

  public String getObjectInstanceName() {
    return objectInstanceName;
  }
}
