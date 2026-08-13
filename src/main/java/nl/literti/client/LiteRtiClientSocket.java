package nl.literti.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import nl.literti.server.LiteRtiServerSocket;

/**
 * Socket client for connecting to the LiteRti server. This class sets up communication channels for
 * the communication towards the server.
 */
public class LiteRtiClientSocket {

  private final String host;
  private final int port;
  private Socket socket;
  private DataOutputStream out;
  private DataInputStream in;
  private boolean connected = false;

  private LiteRtiClientSender clientSender;
  private LiteRtiClientReceiver clientReceiver;

  public LiteRtiClientSocket(LiteRtiClientContext clientContext) {
    this.host = "localhost";
    this.port = LiteRtiServerSocket.DEFAULT_PORT;

    try {
      // Establish connection
      this.socket = new Socket(host, port);

      // Establish transport channels
      this.clientSender = new LiteRtiClientSender(this.socket, clientContext);
      this.clientReceiver = new LiteRtiClientReceiver(this.socket, clientContext);

      // Start the receiver in a separate thread to listen for incoming messages
      ExecutorService executor = Executors.newCachedThreadPool();
      executor.submit(() -> clientReceiver.handle());

    } catch (IOException e) {
      System.err.println("[ClientSocket] Failed to connect to LiteRti at " + host + ":" + port);
      System.err.println("[ClientSocket] Is LiteRtiApplication running? Error: " + e.getMessage());
    }
  }

  public LiteRtiClientSender getClientSender() {
    return this.clientSender;
  }

  public LiteRtiClientReceiver getClientReceiver() {
    return this.clientReceiver;
  }

  public void checkConnection() {
    if (!socket.isConnected() || socket.isClosed()) {
      throw new IllegalStateException("[ClientSocket] Not connected to the LiteRti server");
    }
  }

  public void cleanup() {
    if (socket.isConnected() || !socket.isClosed()) {
      try {
        socket.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }
}
