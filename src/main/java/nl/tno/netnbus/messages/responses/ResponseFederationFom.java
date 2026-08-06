package nl.tno.netnbus.messages.responses;

import nl.tno.netnbus.fom.FederationObjectModel;
import nl.tno.netnbus.messages.MessageObject;

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
