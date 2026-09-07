package nl.literti.messages.requests;

import hla.rti1516e.FederateHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import nl.literti.messages.MessageObject;

public class DiscoveredObjectInstance extends MessageObject {
    private ObjectInstanceHandle instanceHandle;
    private ObjectClassHandle classHandle;
    private String theObjectName;
    private FederateHandle producingFederate;

    public DiscoveredObjectInstance(int msgHandle, ObjectInstanceHandle instanceHandle, ObjectClassHandle classHandle, String theObjectName, FederateHandle producingFederate) {
        super(msgHandle);
        this.instanceHandle = instanceHandle;
        this.classHandle = classHandle;
        this.theObjectName = theObjectName;
        this.producingFederate = producingFederate;
    }

    public ObjectInstanceHandle getInstanceHandle() {
        return this.instanceHandle;
    }

    public ObjectClassHandle getClassHandle() {
        return this.classHandle;
    }

    public String getTheObjectName() {
        return this.theObjectName;
    }

    public FederateHandle getProducingFederate() {
        return this.producingFederate;
    }
}
