package nl.literti.messages.requests;

import nl.literti.fom.FederationObjectModel;
import nl.literti.messages.MessageObject;

public class RequestCreateFederation extends MessageObject {
  
  // Serialization ID for this class
  private static final long serialVersionUID = 98121116105109L;

  private String federationName;
  private FederationObjectModel fom;

  public RequestCreateFederation(String federationName, FederationObjectModel fom, int requestHandle) {
    super(requestHandle);
    this.federationName = federationName;
    this.fom = fom;
  }

  public String getFederationName() {
    return federationName;
  }

  public FederationObjectModel getFom() {
    return fom;
  }
}