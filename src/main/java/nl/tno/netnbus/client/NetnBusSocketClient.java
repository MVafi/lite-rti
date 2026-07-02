package nl.tno.netnbus.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
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
  private DataOutputStream out;
  private DataInputStream in;
  private boolean connected = false;

  // Message type constants
  private static final byte MSG_TEXT = 0;
  private static final byte MSG_BINARY = 1;

  public NetnBusSocketClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public NetnBusSocketClient() {
    this("localhost", NetnBusSocketServer.DEFAULT_PORT);
  }

  // ===== Connection API =====

  public boolean connect(String federateName) {
    try {
      System.out.println("[NetnBusClient] Connecting to " + host + ":" + port);

      // Open TCP connection to host server and get input/output streams
      this.socket = new Socket(host, port);
      this.socket.setSoTimeout(5000);  // 5 second timeout for socket reads
      this.out = new DataOutputStream(socket.getOutputStream());
      this.in = new DataInputStream(socket.getInputStream());

      // Send connect command
      System.out.println("[NetnBusClient] Sending CONNECT|" + federateName);
      sendTextMessage("CONNECT|" + federateName);

      // Wait for response
      System.out.println("[NetnBusClient] Waiting for response...");
      String response = receiveTextMessage();
      System.out.println("[NetnBusClient] Got response: " + response);
      if (response != null && response.startsWith("OK|CONNECTED")) {
        this.connected = true;
        return true;
      }
    } catch (IOException e) {
      System.err.println("Failed to connect to NetnBus at " + host + ":" + port);
      System.err.println("Is NetnBusApplication running? Error: " + e.getMessage());
    }
    return false;
  }

  public void disconnect() {
    if (connected) {
      try {
        sendTextMessage("DISCONNECT");
        receiveTextMessage(); // Wait for acknowledgment
      } catch (IOException e) {
        // Ignore
      }
      cleanup();
    }
  }

  public void checkConnection() {
    if (!this.connected) {
      System.err.println("[NetnBusClient] Not connected to bus, cannot create federation");
    }
  }

  /** Check if connected to the bus. */
  // public boolean isConnected() {
  //   return connected;
  // }

  // ===== Federation API =====

  public void createFederationExecution(String federationExecutionName) throws FederationExecutionAlreadyExists {
    this.checkConnection();
    try {
      sendTextMessage("CREATE_FEDERATION|" + federationExecutionName);

      String response = receiveTextMessage();
      if (response == null){
        throw new RuntimeException("[NetnBusClient] No response received from server when creating federation: " + federationExecutionName);
      } else if (response.startsWith("OK|FEDERATION_CREATED")) {
        System.out.println("[NetnBusClient] Federation created: " + federationExecutionName);
      } else if (response.startsWith("ERROR|FEDERATION_ALREADY_EXISTS")) {
        throw new FederationExecutionAlreadyExists("Federation already exists: " + federationExecutionName);
      } else {
        throw new RuntimeException("[NetnBusClient] Failed to create federation: " + response);
      }

    } catch (IOException e) {
      throw new RuntimeException("[NetnBusClient] Error creating federation: " + e.getMessage(), e);
    }
  }

  public void joinFederationExecution(String federateType, String federationExecutionName) {
    this.checkConnection();
    try {
      sendTextMessage("JOIN_FEDERATION|" + federationExecutionName + "|" + federateType);

      String response = receiveTextMessage();
      if (response == null){
        throw new RuntimeException("[NetnBusClient] No response received from server when joining federation: " + federationExecutionName);
      } else if (response.startsWith("OK|JOINED_FEDERATION")) {
        System.out.println("[NetnBusClient] Joined federation: " + federationExecutionName + " as " + federateType);
      }
      // TODO: more debug statements

      //   System.out.println("[NetnBusClient] Federation created: " + federationExecutionName);
      // } else if (response.startsWith("ERROR|FEDERATION_ALREADY_EXISTS")) {
      //   throw new FederationExecutionAlreadyExists("Federation already exists: " + federationExecutionName);
      // } else {
      //   throw new RuntimeException("[NetnBusClient] Failed to create federation: " + response);
      // }

    } catch (IOException e) {
      throw new RuntimeException("[NetnBusClient] Error joining federation: " + e.getMessage(), e);
    }
  }

  /** Get federate count from server. */
  public int getFederateCount() {
    if (!connected) return 0;
    try {
      sendTextMessage("COUNT");
      String response = receiveTextMessage();
      if (response != null && response.startsWith("OK|")) {
        return Integer.parseInt(response.substring(3));
      }
    } catch (Exception e) {
      // Ignore
    }
    return 0;
  }

  // ===== Hybrid Messaging API =====

  /** Send a text control message */
  private void sendTextMessage(String message) throws IOException {
    out.writeByte(MSG_TEXT);
    out.writeUTF(message);
    out.flush();
  }

  /** Block until receiving a text control message */
  private String receiveTextMessage() throws IOException {
    byte type = in.readByte();
    if (type == MSG_TEXT) {
      return in.readUTF();
    }
    throw new IOException("Expected TEXT message, got type: " + type);
  }

  /** Send binary HLA message (OORTI serialized data) */
  public void sendHLAMessage(byte[] data) throws IOException {
    if (!connected) throw new IOException("Not connected to bus");
    out.writeByte(MSG_BINARY);
    out.writeInt(data.length);
    out.write(data);
    out.flush();
  }

  /** Receive binary HLA message */
  public byte[] receiveHLAMessage() throws IOException {
    byte type = in.readByte();
    if (type == MSG_BINARY) {
      int length = in.readInt();
      byte[] data = new byte[length];
      in.readFully(data);
      return data;
    }
    throw new IOException("Expected BINARY message, got type: " + type);
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
