package nl.literti.messages.requests;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.InteractionClassHandle;
import nl.literti.messages.MessageObject;

public class RequestSubscribeInteraction extends MessageObject {
  private InteractionClassHandle ObjectClass;
  private AttributeHandleSet AttributeList;

  public RequestSubscribeInteraction(
      InteractionClassHandle theClass, int requestHandle) {
    super(requestHandle);
    this.ObjectClass = theClass;
  }

  public InteractionClassHandle getInteractionClassHandle() {
    return this.ObjectClass;
  }

  public AttributeHandleSet getAttributeHandleSet() {
    return this.AttributeList;
  }
}
