package nl.literti.messages.requests;

import nl.literti.messages.MessageObject;

public class RequestConnectFederate extends MessageObject {
  private static final long serialVersionUID = 103117101109122L;

  private final String federateType;

  public RequestConnectFederate(String federateType, int msgHandle) {
    super(msgHandle);
    this.federateType = federateType;
  }

  public String getFederateType() {
    return federateType;
  }
    
}
