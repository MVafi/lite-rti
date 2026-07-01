package nl.tno.netnbus.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/** Client handler for managing communication with a federate client and the NETN Bus context */
public class NetnBusClientHandler {

    // Handles communication with federate client
    private final Socket socket;

    // Reference to the context to manage federate events and state
    private final NetnBusContext context;

    private String federateName;
    private PrintWriter out;
    private BufferedReader in;

    NetnBusClientHandler(Socket socket, NetnBusContext context) {
      this.socket = socket;
      this.context = context;
    }

    // Keep handling messages while still receiving them (within separate thread)
    public void handle() {
      System.out.println("[ClientHandler] New connection, starting handler");
      try {
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line;
        System.out.println("[ClientHandler] Waiting for messages...");
        while ((line = in.readLine()) != null) {
          System.out.println("[ClientHandler] Received: " + line);
          try {
            handleMessage(line);
          } catch (Exception e) {
            System.err.println("[ClientHandler] ERROR handling message: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
          }
        }
      } catch (IOException e) {
        System.out.println("[ClientHandler] Connection closed: " + e.getMessage());
      } finally {
        System.out.println("[ClientHandler] Clean up of handler for federate: " + federateName);
        cleanup();
      }
    }

    private void cleanup() {
      if (federateName != null) {
        context.unregisterFederate(federateName);
      }
      try {
        socket.close();
      } catch (IOException e) {
        // Ignore
      }
    }

    private void handleMessage(String message) {
      String[] parts = message.split("\\|", 2);
      String command = parts[0];

      switch (command) {
        case "CONNECT":
          federateName = parts.length > 1 ? parts[1] : "unknown";
          context.registerFederate(federateName);
          System.out.println("[ClientHandler] Sending response: OK|CONNECTED");
          this.out.println("OK|CONNECTED");
          break;

        case "DISCONNECT":
          this.out.println("OK|DISCONNECTED");
          System.out.println("[ClientHandler] Federate disconnected: ");
          context.unregisterFederate(federateName);
          cleanup();
          break;

        // case "PING":
        //   send("OK|PONG");
        //   break;

        // case "COUNT":
        //   send("OK|" + server.getFederateCount());
        //   break;

        // default:
        //   send("ERROR|Unknown command: " + command);
      }
    }


}





// //   /** Unregister a disconnected federate. */
// //   void unregisterFederate(String name) {
// //     // connectedFederates.remove(name);
// //     System.out.println("[NetnBus] Federate disconnected: " + name);
// //     System.out.println("[NetnBus] Remaining federates: " + connectedFederates.size());
// //   }

// //   /** Get count of connected federates. */
// //   public int getFederateCount() {
// //     // return connectedFederates.size();
// //   }

// //   /** Check if server is running. */
// //   public boolean isRunning() {
// //     return running;
// //   }

//   /** Handles communication with a single federate client. */
//   static class ClientHandler implements Runnable {
//     private final Socket socket;
//     private final NetnBusContext context;
//     private String federateName;
//     private PrintWriter out;
//     private BufferedReader in;

//     ClientHandler(Socket socket, NetnBusContext context) {
//       this.socket = socket;
//       this.context = context;
//     }

//     @Override
//     public void run() {
//       try {
//         out = new PrintWriter(socket.getOutputStream(), true);
//         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//         String line;
//         while ((line = in.readLine()) != null) {
//           handleMessage(line);
//         }
//       } catch (IOException e) {
//         // Connection closed
//       } finally {
//         cleanup();
//       }
//     }

    

//     void send(String message) {
//       if (out != null) {
//         out.println(message);
//       }
//     }


//   }