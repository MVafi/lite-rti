package nl.literti.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import nl.literti.AbstractLiteRtiSender;
import nl.literti.fom.FederationObjectModel;
import nl.literti.messages.MessageObject;
import nl.literti.messages.requests.RequestConnectFederate;
import nl.literti.messages.requests.RequestCreateFederation;
import nl.literti.messages.requests.RequestDisconnectFederate;
import nl.literti.messages.requests.RequestFederationFom;
import nl.literti.messages.requests.RequestJoinFederation;
import nl.literti.messages.requests.RequestObjectInstanceName;
import nl.literti.messages.requests.RequestPublishInteraction;
import nl.literti.messages.requests.RequestPublishObject;
import nl.literti.messages.requests.RequestRegisterObjectInstance;
import nl.literti.messages.requests.RequestSubscribeInteraction;
import nl.literti.messages.requests.RequestSubscribeObject;
import nl.literti.messages.requests.UpdateAttributeValues;
import nl.literti.messages.requests.UpdateSendInteraction;

public class LiteRtiClientSender extends AbstractLiteRtiSender {

  private final Socket socket;

  private final AtomicInteger requestId = new AtomicInteger(0);
  private final LiteRtiClientContext clientContext;

  LiteRtiClientSender(Socket socket, LiteRtiClientContext clientContext) throws IOException {
    this.socket = socket;
    this.clientContext = clientContext;
  }

  private Object serializeSendMsgAndWait(MessageObject request) throws Throwable {
    // All request are registered in the context, responses are managed by the ClientReceiver
    System.out.println("sending msg to server");
    CompletableFuture<Object> future = new CompletableFuture<>();
    clientContext.registerPendingResponse(request.getMsgHandle(), future);

    try {
      serializeSendMsg(this.socket, request);
      Object result = future.get(30L, TimeUnit.SECONDS); // await result
      return result;
    } catch (ExecutionException e) {
      throw e.getCause();
    } catch (Exception e) {
      throw new IOException("Future failed due to unexpected exception: " + e.getMessage(), e);
    } finally {
      clientContext.removePendingResponse(request.getMsgHandle());
    }
  }

  // ===== Request Handling =====

  public void sendConnectFederateRequest(String federateType) throws Throwable {
    RequestConnectFederate request = new RequestConnectFederate(federateType, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public void sendDisconnectFederateRequest() throws Throwable {
    RequestDisconnectFederate request = new RequestDisconnectFederate(requestId.incrementAndGet());
    serializeSendMsg(this.socket, request);
  }

  public void sendCreateFederationRequest(String federationExecutionName, FederationObjectModel combinedFOM) throws Throwable {
    RequestCreateFederation request = new RequestCreateFederation(federationExecutionName, combinedFOM, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public Object sendFederationFomRequest(String federationExecutionName) throws Throwable {
    RequestFederationFom request = new RequestFederationFom(federationExecutionName, requestId.incrementAndGet());
    return serializeSendMsgAndWait(request); // Completing the blocking future will return the FOM object
  }

  public void sendJoinFederationRequest(String federateType, String federationExecutionName) throws Throwable {
    // TODO: implement runtime fom merging, null for now
    RequestJoinFederation request = new RequestJoinFederation(federateType, federationExecutionName, null, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public void sendPublishObjectRequest(ObjectClassHandle theClass, AttributeHandleSet attributeList) throws Throwable {
    RequestPublishObject request = new RequestPublishObject(theClass, attributeList, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public void sendPublishInteractionRequest(InteractionClassHandle theInteraction) throws Throwable {
    RequestPublishInteraction request = new RequestPublishInteraction(theInteraction, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public void sendSubscribeObjectRequest(ObjectClassHandle theClass, AttributeHandleSet attributeList) throws Throwable {
    RequestSubscribeObject request = new RequestSubscribeObject(theClass, attributeList, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public void sendSubscribeInteractionRequest(InteractionClassHandle theClass) throws Throwable {
    RequestSubscribeInteraction request = new RequestSubscribeInteraction(theClass, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public Object sendRegisterObjectInstanceRequest(ObjectClassHandle theClass, String theObjectName) throws Throwable {
    RequestRegisterObjectInstance request = new RequestRegisterObjectInstance(theClass, theObjectName, requestId.incrementAndGet());
    return serializeSendMsgAndWait(request);
  }

  public Object sendObjectInstanceNameRequest(ObjectInstanceHandle theHandle) throws Throwable {
    RequestObjectInstanceName request = new RequestObjectInstanceName(theHandle, requestId.incrementAndGet());
    return serializeSendMsgAndWait(request);
  }

  public void sendAttributeValuesUpdate(ObjectInstanceHandle theHandle, AttributeHandleValueMap theAttributes, byte[] userSuppliedTag) throws Throwable {
    UpdateAttributeValues request = new UpdateAttributeValues(theHandle, theAttributes, userSuppliedTag, requestId.incrementAndGet());
    serializeSendMsg(this.socket, request);
  }

  public void sendInteraction(InteractionClassHandle theHandle, ParameterHandleValueMap theParameters, byte[] userSuppliedTag) throws Throwable {
    UpdateSendInteraction request = new UpdateSendInteraction(theHandle, theParameters, userSuppliedTag, requestId.incrementAndGet());
    serializeSendMsg(this.socket, request);
  }
}
