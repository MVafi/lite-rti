package nl.tno.netnbus.client;

import nl.tno.netnbus.fom.FederationObjectModel;

/** Context for keeping track of the states of the client and its singular federate */
public class NetnBusClientContext {

  // Fom stored at client side (deserialized ObjectModel object in memory)
  private FederationObjectModel fom;

  public void setLocalFOM(FederationObjectModel fom) {
    this.fom = fom;
  }

  public FederationObjectModel getLocalFOM() {
    return this.fom;
  }

}
