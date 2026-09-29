package nl.literti.messages.requests;

import hla.rti1516e.InteractionClassHandle;
import nl.literti.messages.MessageObject;

public class RequestPublishInteraction extends MessageObject {

  private InteractionClassHandle theInteraction;

  public RequestPublishInteraction(
      InteractionClassHandle theInteraction, int requestHandle) {
    super(requestHandle);
    this.theInteraction = theInteraction;
  }

  public InteractionClassHandle getInteractionClassHandle() {
    return this.theInteraction;
  }
}
