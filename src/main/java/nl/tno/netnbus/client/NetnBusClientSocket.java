package nl.tno.netnbus.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.tno.netnbus.server.NetnBusServerSocket;

/**
 * Socket client for connecting to the NETN Bus server. This class sets up communication channels for the communication towards the server.
 */
public class NetnBusClientSocket {

  private final String host;
  private final int port;
  private Socket socket;
  private DataOutputStream out;
  private DataInputStream in;
  private boolean connected = false;
  
  private NetnBusClientSender clientSender;
  private NetnBusClientReceiver clientReceiver;

  public NetnBusClientSocket(NetnBusClientContext clientContext) {
    this.host = "localhost";
    this.port = NetnBusServerSocket.DEFAULT_PORT;

    try {
      // Establish connection
      this.socket = new Socket(host, port);

      // Establish transport channels
      this.clientSender = new NetnBusClientSender(this.socket, clientContext);
      this.clientReceiver = new NetnBusClientReceiver(this.socket, clientContext);

      // Start the receiver in a separate thread to listen for incoming messages
      ExecutorService executor = Executors.newCachedThreadPool();
      executor.submit(() -> clientReceiver.handle());
      
    } catch (IOException e) {
      System.err.println("[ClientSocket] Failed to connect to NetnBus at " + host + ":" + port);
      System.err.println("[ClientSocket] Is NetnBusApplication running? Error: " + e.getMessage());
    }
  }

  public NetnBusClientSender getClientSender() {
    return this.clientSender;
  }

  public NetnBusClientReceiver getClientReceiver() {
    return this.clientReceiver;
  }

  public void checkConnection() {
    if (!socket.isConnected() || socket.isClosed()) {
      throw new IllegalStateException("[ClientSocket] Not connected to the NetnBus server");
    }
  }

  public void cleanup() {
    if (socket.isConnected() || !socket.isClosed()){
      try {
        socket.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }

}