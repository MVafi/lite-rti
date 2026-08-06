package nl.tno.netnbus.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import nl.tno.netnbus.client.NetnBusClientReceiver;
import nl.tno.netnbus.client.NetnBusClientSender;

/** Socket server for the NETN Bus, handles only the TCP transport */
public class NetnBusServerSocket {

  public static final int DEFAULT_PORT = 4567;

  private final int port;
  private ServerSocket serverSocket;
  private final ExecutorService executor;
  private volatile boolean running = false;
  private final NetnBusServerContext serverContext;
  private final AtomicInteger connectionHandleCounter = new AtomicInteger(1); 

  public NetnBusServerSocket(NetnBusServerContext serverContext) {
    this.executor = Executors.newCachedThreadPool();
    this.port = DEFAULT_PORT;
    this.serverContext = serverContext;
  }

  public void start() throws IOException {

    // Assign thread worker with accepting method
    this.serverSocket = new ServerSocket(port);
    executor.submit(this::acceptLoop);

    System.out.println("[NetnBus] Socket server started on port " + port);
    System.out.println("[NetnBus] Ready to accept federate connections");
  }

  private void acceptLoop() {
    // Listen and accept new TCP connections from federates
    this.running = true;
    while (this.running) {
      try {
        // Accept a new connection
        Socket socketToClient = serverSocket.accept();

        // Establish transport channels
        int connectionHandle = connectionHandleCounter.getAndIncrement();
        NetnBusServerReceiver serverReceiver = new NetnBusServerReceiver(socketToClient, this.serverContext, connectionHandle);

        // Add client handling on a separate thread
        executor.submit(() -> serverReceiver.handle());
      } catch (IOException e) {
        if (this.running) {
          System.err.println("[NetnBus] Error accepting connection: " + e.getMessage());
        }
      }
    }
  }

  public void stop() {
    this.running = false;
    try {
      if (this.serverSocket != null && !this.serverSocket.isClosed()) {
        this.serverSocket.close();
      }
    } catch (IOException e) {
      System.err.println("[NetnBus] Error closing server: " + e.getMessage());
    }
    executor.shutdownNow();
  }
}
