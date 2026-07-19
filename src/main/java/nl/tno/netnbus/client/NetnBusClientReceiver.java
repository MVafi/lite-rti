package nl.tno.netnbus.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import nl.tno.netnbus.client.requests.MessageObject;
import nl.tno.netnbus.client.requests.UpdateAttributeValuesRequest;
import nl.tno.netnbus.utils.BinaryHelper;

/**
 * Transportation layer for handling messages received by the server (client-side), handling communication that is received from the server side
 */

// TODO: clean up this class, and create an abstract message handler
public class NetnBusClientReceiver {

  private final Socket socket;                    // TCP connection to server
  private final NetnBusClientContext context;     // Context to handle client (federate) events and state
  private final NetnBusClientSocket clientSocket; // Reference to socket for completing responses
  private DataInputStream in;  

  // Message type constants (hybrid messaging protocol for easy debugging)
  private static final byte MSG_TEXT = 0;
  private static final byte MSG_BINARY = 1; 


    NetnBusClientReceiver(Socket socket, NetnBusClientContext context, NetnBusClientSocket clientSocket) {
        this.socket = socket;
        this.context = context;
        this.clientSocket = clientSocket;
    }

    
    // Keep handling messages while still receiving them (within separate thread)
    public void handle() {
      System.out.println("[ClientReceiver] New connection, starting handler");
      try {
      // Data stream from server
      this.in = new DataInputStream(socket.getInputStream());

      System.out.println("[ClientReceiver] Waiting for messages...");
      while (true) {
          try {
          byte messageType = in.readByte();

          // Handling string messages (for commands and debugging)
          if (messageType == MSG_TEXT) {
              String message = in.readUTF();
              System.out.println("[ClientReceiver] Received: " + message);
              handleStringMessage(message);

              // Handling binary messages (for HLA)
          } else if (messageType == MSG_BINARY) {
              int length = in.readInt();
              byte[] data = new byte[length];
              in.readFully(data);
              System.out.println("[ClientReceiver] Received binary message, " + length + " bytes");
              handleBinaryMessage(data);

          } else {
              System.err.println("[ClientReceiver] Unknown message type: " + messageType);
          }
          } catch (IOException e) {
          // Connection closed or error reading
          throw e;
          }
      }
      } catch (IOException e) {
      System.out.println("[ClientReceiver] Connection closed: " + e.getMessage());
      } finally {
      // Currently this is sometimes done double
      cleanupClient();
      }
    }

  private void cleanupClient() {
    try {
      socket.close();
    } catch (IOException e) {
      // Ignore
    }
  }

  private void handleStringMessage(String message) {
    try {
      System.out.println("[ClientReceiver] Receiving string message: " + message);
    } catch (Exception e) {
      throw new RuntimeException("[ClientReceiver] Interrupted while queuing response: " + e.getMessage(), e);
      // System.err.println("[ClientReceiver] Interrupted while queuing response: " + e.getMessage());
      // Thread.currentThread().interrupt();
    }
  }

  private void handleBinaryMessage(byte[] data) {
    try {
      Object obj = BinaryHelper.deserializeBinaryMessage(data);
      
      // Handle different message types
      if (obj instanceof UpdateAttributeValuesRequest) {
      } 
      
      // Else
      if (!(obj instanceof MessageObject msgObj)) {
        System.err.println("[ClientReceiver] Received unknown binary message type: " + obj.getClass().getName());
        return;
      }

      if (msgObj.getMsgString().startsWith("OK|FEDERATION_CREATED")) {
        System.out.println("[ClientReceiver] >>>>>>>>>>>>>>>>> Receiving federation created response: " + msgObj.getMsgString());
        // context.handleFederationCreated();
        int msgHandle = msgObj.getMsgHandle();
        clientSocket.completeResponse(msgHandle, msgObj.getMsgString());
      }





    } catch (ClassNotFoundException | IOException e) {
      System.err.println("[ClientReceiver] Error deserializing binary message: " + e.getMessage());
    }
  }
}
