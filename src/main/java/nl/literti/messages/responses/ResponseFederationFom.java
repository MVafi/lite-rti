package nl.literti.messages.responses;

import nl.literti.fom.FederationObjectModel;
import nl.literti.messages.MessageObject;

public class ResponseFederationFom extends MessageObject {

  private final FederationObjectModel fom;

  public ResponseFederationFom(FederationObjectModel fom, int requestHandle) {
    super(requestHandle);
    this.fom = fom;
  }

  public FederationObjectModel getFom() {
    return this.fom;
  }
}
