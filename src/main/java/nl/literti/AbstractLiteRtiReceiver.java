package nl.literti;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Abstract class that handles incoming messages, passing them to the handleBinaryMessage method,
 * which is implemented in the child classes
 */
public abstract class AbstractLiteRtiReceiver {

  protected final DataInputStream in;
  protected final Socket socket;

  public AbstractLiteRtiReceiver(Socket socket) throws IOException {
    this.in = new DataInputStream(socket.getInputStream());
    this.socket = socket;
  }

  public void handle() {
    try {
      while (true) {
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readFully(data);
        handleBinaryMessage(data);
      }
    } catch (IOException e) {
      System.out.println("[...Receiver] Connection closed: " + e.getMessage());
    } finally {
      // Currently this is sometimes done double
      cleanupReceiver();
      try {
        socket.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }

  protected abstract void handleBinaryMessage(byte[] data) throws IOException;

  protected void cleanupReceiver() {}
}
