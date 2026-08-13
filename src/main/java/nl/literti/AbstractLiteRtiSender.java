package nl.literti;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import nl.literti.messages.MessageObject;
import nl.literti.utils.BinaryHelper;

public class AbstractLiteRtiSender {

  public AbstractLiteRtiSender() {}

  public void serializeSendMsg(Socket socket, MessageObject msg) throws IOException {
    byte[] requestBytes = BinaryHelper.serializeRequestObject(msg);
    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
    out.writeInt(requestBytes.length);
    out.write(requestBytes);
    out.flush();
  }
}
