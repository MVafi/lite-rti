package nl.tno.netnbus.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import nl.tno.netnbus.server.NetnBusSocketServer;

// TODO check this file
/**
 * Socket client for connecting to the NETN Bus server. Used by federates to connect to a running
 * NetnBusApplication.
 */
public class NetnBusSocketClient {

  private final String host;
  private final int port;
  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;
  private boolean connected = false;

  public NetnBusSocketClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public NetnBusSocketClient() {
    this("localhost", NetnBusSocketServer.DEFAULT_PORT);
  }

  /**
   * Connect to the NETN Bus server.
   *
   * @param federateName Unique name for this federate
   * @return true if connection successful
   */
  public boolean connect(String federateName) {
    try {
      System.out.println("[NetnBusClient] Connecting to " + host + ":" + port);
      socket = new Socket(host, port);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Send connect command
      System.out.println("[NetnBusClient] Sending CONNECT|" + federateName);
      out.println("CONNECT|" + federateName);

      // Wait for response
      System.out.println("[NetnBusClient] Waiting for response...");
      String response = in.readLine();
      System.out.println("[NetnBusClient] Got response: " + response);
      if (response != null && response.startsWith("OK|CONNECTED")) {
        connected = true;
        return true;
      }
    } catch (IOException e) {
      System.err.println("Failed to connect to NetnBus at " + host + ":" + port);
      System.err.println("Is NetnBusApplication running? Error: " + e.getMessage());
    }
    return false;
  }

  /** Disconnect from the NETN Bus server. */
  public void disconnect() {
    if (connected) {
      try {
        out.println("DISCONNECT");
        in.readLine(); // Wait for acknowledgment
      } catch (IOException e) {
        // Ignore
      }
      cleanup();
    }
  }

  /** Check if connected to the bus. */
  public boolean isConnected() {
    return connected;
  }

  /** Get federate count from server. */
  public int getFederateCount() {
    if (!connected) return 0;
    try {
      out.println("COUNT");
      String response = in.readLine();
      if (response != null && response.startsWith("OK|")) {
        return Integer.parseInt(response.substring(3));
      }
    } catch (Exception e) {
      // Ignore
    }
    return 0;
  }

  private void cleanup() {
    connected = false;
    try {
      if (socket != null) socket.close();
    } catch (IOException e) {
      // Ignore
    }
  }
}
