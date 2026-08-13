package nl.literti.messages.requests;

import nl.literti.messages.MessageObject;

public class RequestFederationFom extends MessageObject {

  private String federationName;

  public RequestFederationFom(String federationName, int requestHandle) {
    super(requestHandle);
    this.federationName = federationName;
  }

  public String getFederationName() {
    return this.federationName;
  }
}
