package nl.tno.netnbus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import nl.tno.netnbus.messages.MessageObject;
import nl.tno.netnbus.utils.BinaryHelper;

public class AbstractNetnBusSender {

  public AbstractNetnBusSender() {
  }

  public void serializeSendMsg(Socket socket, MessageObject msg) throws IOException {
    byte[] requestBytes = BinaryHelper.serializeRequestObject(msg);
    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
    out.writeInt(requestBytes.length);
    out.write(requestBytes);
    out.flush();
  }
}
