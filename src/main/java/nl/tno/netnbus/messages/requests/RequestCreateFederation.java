package nl.tno.netnbus.messages.requests;

import nl.tno.netnbus.fom.FederationObjectModel;
import nl.tno.netnbus.messages.MessageObject;

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