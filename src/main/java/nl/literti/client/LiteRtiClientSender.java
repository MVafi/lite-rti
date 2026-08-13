package nl.literti.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import nl.literti.AbstractLiteRtiSender;
import nl.literti.fom.FederationObjectModel;
import nl.literti.messages.MessageObject;
import nl.literti.messages.requests.RequestConnectFederate;
import nl.literti.messages.requests.RequestCreateFederation;
import nl.literti.messages.requests.RequestDisconnectFederate;
import nl.literti.messages.requests.RequestFederationFom;
import nl.literti.messages.requests.RequestJoinFederation;
import nl.literti.messages.requests.RequestObjectInstanceName;
import nl.literti.messages.requests.RequestPublishObject;
import nl.literti.messages.requests.RequestRegisterObjectInstance;
import nl.literti.messages.requests.RequestSubscribeObject;
import nl.literti.messages.requests.UpdateAttributeValues;

public class LiteRtiClientSender extends AbstractLiteRtiSender {

  private final Socket socket;

  private final AtomicInteger requestId = new AtomicInteger(0);
  private final LiteRtiClientContext clientContext;

  LiteRtiClientSender(Socket socket, LiteRtiClientContext clientContext) throws IOException {
    this.socket = socket;
    this.clientContext = clientContext;
  }

  private Object serializeSendMsgAndWait(MessageObject request) throws IOException {
    // All request are registered in the context, responses are managed by the ClientReceiver
    System.out.println("sending msg to server");
    CompletableFuture<Object> future = new CompletableFuture<>();
    clientContext.registerPendingResponse(request.getMsgHandle(), future);
  
    // TODO: make the error handling more correct instead of generic IOException
    try {
      serializeSendMsg(this.socket, request);
      Object result = future.get(30L, TimeUnit.SECONDS);  // await result
      return result;
    } catch (Exception e) {
      throw new IOException("Request failed: " + e.getMessage(), e);
    } finally {
      clientContext.removePendingResponse(request.getMsgHandle());
    }
  }

  // ===== Request Handling =====

  public void sendConnectFederateRequest(String federateType) throws IOException {
    RequestConnectFederate request = new RequestConnectFederate(federateType, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public void sendDisconnectFederateRequest() throws IOException {
    RequestDisconnectFederate request = new RequestDisconnectFederate(requestId.incrementAndGet());
    serializeSendMsg(this.socket, request);
  }

  public void sendCreateFederationRequest(String federationExecutionName, FederationObjectModel combinedFOM) throws IOException {
    RequestCreateFederation request = new RequestCreateFederation(federationExecutionName, combinedFOM, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public Object sendFederationFomRequest(String federationExecutionName) throws IOException {
    RequestFederationFom request = new RequestFederationFom(federationExecutionName, requestId.incrementAndGet());
    return serializeSendMsgAndWait(request);  // Completing the blocking future will return the FOM object
  }

  public void sendJoinFederationRequest(String federateType, String federationExecutionName) throws IOException {
    // TODO: implement runtime fom merging, null for now
    RequestJoinFederation request = new RequestJoinFederation(federateType, federationExecutionName, null, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public void sendPublishObjectRequest(ObjectClassHandle theClass, AttributeHandleSet attributeList) throws IOException {
    RequestPublishObject request = new RequestPublishObject(theClass, attributeList, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public void sendSubscribeObjectRequest(ObjectClassHandle theClass, AttributeHandleSet attributeList) throws IOException {
    RequestSubscribeObject request = new RequestSubscribeObject(theClass, attributeList, requestId.incrementAndGet());
    serializeSendMsgAndWait(request);
  }

  public Object sendRegisterObjectInstanceRequest(ObjectClassHandle theClass, String theObjectName) throws IOException {
    RequestRegisterObjectInstance request = new RequestRegisterObjectInstance(theClass, theObjectName, requestId.incrementAndGet());
    return serializeSendMsgAndWait(request);
  }

  public Object sendObjectInstanceNameRequest(ObjectInstanceHandle theHandle) throws IOException {
    RequestObjectInstanceName request = new RequestObjectInstanceName(theHandle, requestId.incrementAndGet());
    return serializeSendMsgAndWait(request);
  }

  public void sendUpdateAttributeValuesRequest(ObjectInstanceHandle theHandle, AttributeHandleValueMap theAttributes, byte[] userSuppliedTag) throws IOException {
    UpdateAttributeValues request = new UpdateAttributeValues(theHandle, theAttributes, userSuppliedTag, requestId.incrementAndGet());
    serializeSendMsg(this.socket, request);
  }
}
