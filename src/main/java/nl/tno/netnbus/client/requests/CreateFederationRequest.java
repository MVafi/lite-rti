package nl.tno.netnbus.client.requests;

import nl.tno.netnbus.fom.FederationObjectModel;

public class CreateFederationRequest extends RequestObject {
  
  // Serialization ID for this class
  private static final long serialVersionUID = 98121116105109L;

  private String federationName;
  private FederationObjectModel fom;

  public CreateFederationRequest(String federationName, FederationObjectModel fom) {
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