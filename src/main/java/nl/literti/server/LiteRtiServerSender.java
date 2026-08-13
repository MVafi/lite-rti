package nl.literti.server;

import hla.rti1516e.ObjectInstanceHandle;
import java.io.IOException;
import java.net.Socket;
import nl.literti.AbstractLiteRtiSender;
import nl.literti.fom.FederationObjectModel;
import nl.literti.messages.responses.ResponseFederateConnected;
import nl.literti.messages.responses.ResponseFederationCreated;
import nl.literti.messages.responses.ResponseFederationFom;
import nl.literti.messages.responses.ResponseJoinFederation;
import nl.literti.messages.responses.ResponseObjectInstanceName;
import nl.literti.messages.responses.ResponsePublishObject;
import nl.literti.messages.responses.ResponseRegisterObjectInstance;
import nl.literti.messages.responses.ResponseSubscribeObject;

public class LiteRtiServerSender extends AbstractLiteRtiSender {

  public LiteRtiServerSender() {}

  public void sendFederateConnectedResponse(Socket socket, int msgHandle) throws IOException {
    ResponseFederateConnected response = new ResponseFederateConnected(msgHandle);
    response.setMsgString("OK|FEDERATE_CONNECTED");
    serializeSendMsg(socket, response);
  }

  public void sendFederationCreatedResponse(Socket socket, int msgHandle, String result)
      throws IOException {
    ResponseFederationCreated response = new ResponseFederationCreated(msgHandle);
    response.setMsgString(result);
    serializeSendMsg(socket, response);
  }

  public void sendFederationFomResponse(
      Socket socket, int msgHandle, FederationObjectModel fom, String result) throws IOException {
    ResponseFederationFom response = new ResponseFederationFom(fom, msgHandle);
    response.setMsgString(result);
    serializeSendMsg(socket, response);
  }

  public void sendJoinFederationResponse(Socket socket, int msgHandle, String result)
      throws IOException {
    ResponseJoinFederation response = new ResponseJoinFederation(msgHandle);
    response.setMsgString(result);
    serializeSendMsg(socket, response);
  }

  public void sendPublishObjectResponse(Socket socket, int msgHandle, String result)
      throws IOException {
    ResponsePublishObject response = new ResponsePublishObject(msgHandle);
    response.setMsgString(result);
    serializeSendMsg(socket, response);
  }

  public void sendSubscribeObjectResponse(Socket socket, int msgHandle, String result)
      throws IOException {
    ResponseSubscribeObject response = new ResponseSubscribeObject(msgHandle);
    response.setMsgString(result);
    serializeSendMsg(socket, response);
  }

  public void sendRegisterObjectInstanceResponse(
      Socket socket, int msgHandle, ObjectInstanceHandle objectInstanceHandle, String result)
      throws IOException {
    ResponseRegisterObjectInstance response =
        new ResponseRegisterObjectInstance(msgHandle, objectInstanceHandle);
    response.setMsgString(result);
    serializeSendMsg(socket, response);
  }

  public void sendObjectInstanceNameResponse(
      Socket socket, int msgHandle, String objectInstanceName, String result) throws IOException {
    ResponseObjectInstanceName response =
        new ResponseObjectInstanceName(msgHandle, objectInstanceName);
    response.setMsgString(result);
    serializeSendMsg(socket, response);
  }
}
