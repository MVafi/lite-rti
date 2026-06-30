package nl.tno.netnbus.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Socket server for the NETN Bus. Listens for federate connections and manages them. */
public class NetnBusSocketServer {

  public static final int DEFAULT_PORT = 4567;

  private final int port;
  private ServerSocket serverSocket;
  private final ExecutorService executor;
  private final Map<String, ClientHandler> connectedFederates = new ConcurrentHashMap<>();
  private volatile boolean running = false;

  public NetnBusSocketServer(int port) {
    this.port = port;
    this.executor = Executors.newCachedThreadPool();
  }

  public NetnBusSocketServer() {
    this(DEFAULT_PORT);
  }

  /** Start the socket server in a background thread. */
  public void start() throws IOException {
    serverSocket = new ServerSocket(port);
    running = true;

    System.out.println("[NetnBus] Socket server started on port " + port);
    System.out.println("[NetnBus] Ready to accept federate connections");

    // Accept connections in a separate thread
    executor.submit(
        () -> {
          while (running) {
            try {
              Socket clientSocket = serverSocket.accept();
              ClientHandler handler = new ClientHandler(clientSocket, this);
              executor.submit(handler);
            } catch (IOException e) {
              if (running) {
                System.err.println("[NetnBus] Error accepting connection: " + e.getMessage());
              }
            }
          }
        });
  }

  /** Stop the socket server. */
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

  /** Register a connected federate. */
  void registerFederate(String name, ClientHandler handler) {
    connectedFederates.put(name, handler);
    System.out.println("[NetnBus] Federate connected: " + name);
    System.out.println("[NetnBus] Total federates: " + connectedFederates.size());
  }

  /** Unregister a disconnected federate. */
  void unregisterFederate(String name) {
    connectedFederates.remove(name);
    System.out.println("[NetnBus] Federate disconnected: " + name);
    System.out.println("[NetnBus] Remaining federates: " + connectedFederates.size());
  }

  /** Get count of connected federates. */
  public int getFederateCount() {
    return connectedFederates.size();
  }

  /** Check if server is running. */
  public boolean isRunning() {
    return running;
  }

  /** Handles communication with a single federate client. */
  static class ClientHandler implements Runnable {
    private final Socket socket;
    private final NetnBusSocketServer server;
    private String federateName;
    private PrintWriter out;
    private BufferedReader in;

    ClientHandler(Socket socket, NetnBusSocketServer server) {
      this.socket = socket;
      this.server = server;
    }

    @Override
    public void run() {
      try {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line;
        while ((line = in.readLine()) != null) {
          handleMessage(line);
        }
      } catch (IOException e) {
        // Connection closed
      } finally {
        cleanup();
      }
    }

    private void handleMessage(String message) {
      String[] parts = message.split("\\|", 2);
      String command = parts[0];

      switch (command) {
        case "CONNECT":
          federateName = parts.length > 1 ? parts[1] : "unknown";
          server.registerFederate(federateName, this);
          send("OK|CONNECTED");
          break;

        case "DISCONNECT":
          send("OK|DISCONNECTED");
          cleanup();
          break;

        case "PING":
          send("OK|PONG");
          break;

        case "COUNT":
          send("OK|" + server.getFederateCount());
          break;

        default:
          send("ERROR|Unknown command: " + command);
      }
    }

    void send(String message) {
      if (out != null) {
        out.println(message);
      }
    }

    private void cleanup() {
      if (federateName != null) {
        server.unregisterFederate(federateName);
      }
      try {
        socket.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }
}
