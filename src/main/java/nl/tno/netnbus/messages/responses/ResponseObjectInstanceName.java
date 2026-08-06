package nl.tno.netnbus.messages.responses;

import nl.tno.netnbus.messages.MessageObject;

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
