package nl.literti.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import nl.literti.impl.ObjectClassHandleImpl;
import nl.literti.messages.requests.RequestRegisterObjectInstance;
import nl.literti.messages.responses.ResponseRegisterObjectInstance;
import nl.literti.utils.AttributeHandleSetImpl;
import nl.literti.utils.BinaryHelper;

public class LiteRtiServerRegistrationTest {

  private LiteRtiServerContext context;
  private final ObjectClassHandleImpl objectClass = new ObjectClassHandleImpl(10);

  @Before
  public void setUp() throws Exception {
    context = new LiteRtiServerContext();
    context.createFederationExecutionWithFOM("world", null);
  }



  @Test
  public void missingFederationReturnsCorrelatedError() throws Exception {
    ResponseRegisterObjectInstance response = register("missing", "publisher", 43);
    assertEquals(43, response.getMsgHandle());
    assertEquals("ERROR|FEDERATION_DOES_NOT_EXIST", response.getMsgString());
  }

  @Test
  public void successfulRegistrationStillReturnsExactlyOneResponse() throws Exception {
    context.getFederation("world").publishObjectClass("publisher", objectClass, new AttributeHandleSetImpl());
    ResponseRegisterObjectInstance response = register("world", "publisher", 44);
    assertEquals(44, response.getMsgHandle());
    assertEquals("OK|OBJECT_INSTANCE_REGISTERED", response.getMsgString());
    assertNotNull(response.getObjectInstanceHandle());
    assertEquals("publisher", context.getFederation("world")
        .getObjectInstanceOwner(response.getObjectInstanceHandle()));
  }

  private ResponseRegisterObjectInstance register(String federation, String federate, int id)
      throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (Socket socket = new Socket() {
      @Override
      public ByteArrayOutputStream getOutputStream() {
        return bytes;
      }
    }) {
      context.handleRegisterObjectInstance(socket,
          new RequestRegisterObjectInstance(objectClass, "instance", id), federation, federate);
    }
    try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      byte[] payload = new byte[input.readInt()];
      input.readFully(payload);
      ResponseRegisterObjectInstance response =
          (ResponseRegisterObjectInstance) BinaryHelper.deserializeBinaryMessage(payload);
      assertEquals("Expected exactly one response", 0, input.available());
      return response;
    }
  }
}