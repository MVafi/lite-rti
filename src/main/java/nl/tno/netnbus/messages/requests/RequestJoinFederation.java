package nl.tno.netnbus.messages.requests;

import nl.tno.netnbus.fom.FederationObjectModel;
import nl.tno.netnbus.messages.MessageObject;

public class RequestJoinFederation extends MessageObject {

  private String federateType;
  private String federationName;
  private FederationObjectModel combinedFOM; //not used atm

  public RequestJoinFederation(String federateType, String federationName, FederationObjectModel combinedFOM, int requestHandle) {
    super(requestHandle);
    this.federateType = federateType;
    this.federationName = federationName;
    this.combinedFOM = combinedFOM;
  }

  public String getFederateType() {
    return this.federateType;
  }

  public String getFederationName() {
    return this.federationName;
  }
}
