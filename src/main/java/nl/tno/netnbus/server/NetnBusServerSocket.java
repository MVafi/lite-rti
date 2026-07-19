package nl.tno.netnbus.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Socket server for the NETN Bus, handles only the TCP transport */
public class NetnBusServerSocket {

  public static final int DEFAULT_PORT = 4567;

  private final int port;
  private ServerSocket serverSocket;
  private final ExecutorService executor;
  private volatile boolean running = false;
  private final NetnBusServerContext context;

  public NetnBusServerSocket(NetnBusServerContext context) {
    this.executor = Executors.newCachedThreadPool();
    this.port = DEFAULT_PORT;
    this.context = context;
  }

  // public NetnBusSocketServer() {
  //   this(new NetnBusContext());
  // }

  public void start() throws IOException {
    this.serverSocket = new ServerSocket(port);

    System.out.println("[NetnBus] Socket server started on port " + port);
    System.out.println("[NetnBus] Ready to accept federate connections");

    // Assign thread worker with accepting method
    executor.submit(this::acceptLoop);
  }

  private void acceptLoop() {
    // Listen and accept new TCP connections from federates
    running = true;
    while (running) {
      try {
        // Accept a new connection
        System.out.println("[SocketServer] Waiting for connection...");
        Socket clientSocket = serverSocket.accept();
        System.out.println(
            "[SocketServer] Connection accepted from: " + clientSocket.getRemoteSocketAddress());

        // Allow the client handler to manage the connection
        NetnBusServerReceiver clientReceiver = new NetnBusServerReceiver(clientSocket, this.context);

        // Add client handling on a separate thread
        executor.submit(() -> clientReceiver.handle());
      } catch (IOException e) {
        if (running) {
          System.err.println("[NetnBus] Error accepting connection: " + e.getMessage());
        }
      }
    }
  }

  public void stop() {
    running = false;
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
      }
    } catch (IOException e) {
      System.err.println("[NetnBus] Error closing server: " + e.getMessage());
    }
    executor.shutdownNow();
  }
}
