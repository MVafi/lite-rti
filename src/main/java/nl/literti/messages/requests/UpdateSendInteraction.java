package nl.literti.messages.requests;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandleValueMap;
import nl.literti.messages.MessageObject;

// TODO: check this file
public class UpdateSendInteraction extends MessageObject {

  private final InteractionClassHandle interactionClassHandle;
  private final ParameterHandleValueMap theParameters;
  private final byte[] userSuppliedTag;

  public UpdateSendInteraction(
      InteractionClassHandle interactionClassHandle,
      ParameterHandleValueMap theParameters,
      byte[] userSuppliedTag,
      int msgHandle) {
    super(msgHandle);
    this.interactionClassHandle = interactionClassHandle;
    this.theParameters = theParameters;
    this.userSuppliedTag = userSuppliedTag;
  }

  public InteractionClassHandle getInteractionClassHandle() {
    return this.interactionClassHandle;
  }

  public ParameterHandleValueMap getParameterHandleValueMap() {
    return this.theParameters;
  }

  public byte[] getUserSuppliedTag() {
    return this.userSuppliedTag;
  }
}
