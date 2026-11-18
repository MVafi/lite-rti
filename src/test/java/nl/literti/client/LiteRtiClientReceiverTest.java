package nl.literti.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockMakers;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import nl.literti.impl.ObjectInstanceHandleImpl;
import nl.literti.messages.MessageObject;
import nl.literti.messages.requests.DiscoveredObjectInstance;
import nl.literti.messages.requests.RequestAttributeValueUpdate;
import nl.literti.messages.requests.UpdateAttributeValues;
import nl.literti.messages.requests.UpdateSendInteraction;
import nl.literti.messages.responses.ResponseFederationCreated;
import nl.literti.messages.responses.ResponseRegisterObjectInstance;
import nl.literti.messages.responses.ResponseSubscribeObject;
import nl.literti.utils.BinaryHelper;

public class LiteRtiClientReceiverTest {

  private LiteRtiClientContext context;

  @Before
  public void setUp() {
    // Do not run the context constructor: it connects to a real RTI server.
    context = mock(LiteRtiClientContext.class, withSettings().mockMaker(MockMakers.SUBCLASS));
  }

  @Test
  public void lateResponseDoesNotStopReaderOrApplyResponseSideEffects() throws Exception {
    ResponseSubscribeObject late = new ResponseSubscribeObject(1);
    late.setMsgString("OK|OBJECT_CLASS_SUBSCRIBED");
    CompletableFuture<Object> next = new CompletableFuture<>();
    when(context.getPendingResponse(2)).thenReturn(next);
    ObjectInstanceHandleImpl handle = new ObjectInstanceHandleImpl(20);

    try (Socket socket = inputSocket(List.of(
      new ResponseRegisterObjectInstance(0, handle), late,
      new ResponseRegisterObjectInstance(2, handle)))) {
      new LiteRtiClientReceiver(socket, context).handle();
    }

    assertEquals(handle, next.get(1, TimeUnit.SECONDS));
    verify(context, never()).handleSubscribeObjectResponse(anyString());
    verify(context).shutdownCallbacks();
  }


  @Test
  public void otherServicesPreserveTheirTypedExceptions() throws Exception {
    CompletableFuture<Object> future = new CompletableFuture<>();
    when(context.getPendingResponse(12)).thenReturn(future);
    FederationExecutionAlreadyExists expected = new FederationExecutionAlreadyExists("already exists");
    doThrow(expected).when(context).handleCreateFederationResponse(anyString());
    ResponseFederationCreated error = new ResponseFederationCreated(12);
    error.setMsgString("ERROR|FEDERATION_ALREADY_EXISTS");
    try (Socket socket = inputSocket(List.of(error))) {
      new LiteRtiClientReceiver(socket, context).handle();
    }
    ExecutionException exception = assertThrows(ExecutionException.class,
        () -> future.get(1, TimeUnit.SECONDS));
    assertSame(expected, exception.getCause());
  }


  @Test
  public void callbacksAreDispatchedWithoutLookingUpOrCompletingAFuture() throws Exception {
    List<MessageObject> callbacks = List.of(
        new DiscoveredObjectInstance(9, null, null, "object", null),
        new UpdateAttributeValues(null, null, null, 9),
        new RequestAttributeValueUpdate(null, null, null, 9),
        new UpdateSendInteraction(null, null, null, 9));

    try (Socket socket = inputSocket(callbacks)) {
      new LiteRtiClientReceiver(socket, context).handle();
    }

    verify(context, times(4)).queueCallback(any(Runnable.class));
    verify(context, never()).getPendingResponse(anyInt());
  }

  @Test
  public void responseHandlerFailureDoesNotPreventNextResponse() throws Exception {
    CompletableFuture<Object> failed = new CompletableFuture<>();
    CompletableFuture<Object> next = new CompletableFuture<>();
    when(context.getPendingResponse(10)).thenReturn(failed);
    when(context.getPendingResponse(11)).thenReturn(next);
    doThrow(new IllegalStateException("handler failure"))
        .when(context).handleSubscribeObjectResponse(anyString());
    ResponseSubscribeObject response = new ResponseSubscribeObject(10);
    response.setMsgString("OK|OBJECT_CLASS_SUBSCRIBED");
    ObjectInstanceHandleImpl handle = new ObjectInstanceHandleImpl(30);
    try (Socket socket = inputSocket(List.of(response, new ResponseRegisterObjectInstance(11, handle)))) {
      new LiteRtiClientReceiver(socket, context).handle();
    }
    assertTrue(failed.isCompletedExceptionally());
    assertEquals(handle, next.get(1, TimeUnit.SECONDS));
  }

  private static Socket inputSocket(List<? extends MessageObject> messages) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (DataOutputStream output = new DataOutputStream(bytes)) {
      for (MessageObject message : messages) {
        byte[] payload = BinaryHelper.serializeRequestObject(message);
        output.writeInt(payload.length);
        output.write(payload);
      }
    }
    ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
    return new Socket() {
      @Override
      public ByteArrayInputStream getInputStream() {
        return input;
      }
    };
  }
}