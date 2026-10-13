package nl.literti;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import nl.literti.server.LiteRtiServerContext;
import nl.literti.server.LiteRtiServerSocket;

public class LiteRtiApplication {

  private final LiteRtiServerContext serverContext;
  private LiteRtiServerSocket socketServer;
  private volatile boolean running = true;
  private final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
  private static final String STATUS_FILE = "rti-status.json";
  private static final long SAVE_INTERVAL_MS = 5000; // Save every 5 seconds
  private long lastSaveTime = 0;

  public LiteRtiApplication() {
    this.serverContext = new LiteRtiServerContext();
  }

  public void run() throws Exception {
    // Start the socket server with shared context
    this.socketServer = new LiteRtiServerSocket(this.serverContext);
    this.socketServer.start();

    int count = 0;
    // TERMINAL STATE PRINTER
    while (running) {
      Thread.sleep(1000);
      String dots = ".".repeat(count % 4);
      StringBuilder status = new StringBuilder();
      status
          .append("\r| Federations: ")
          .append(this.serverContext.getAllFederationExecutions().size());
      status.append(" | Federates: ").append(this.serverContext.getConnectedFederates().size());
      status.append(" | ");

      for (String fedName : this.serverContext.getAllFederationExecutions()) {
        var fed = this.serverContext.getFederation(fedName);
        if (fed != null) {
          status.append(fedName).append("{");
          for (String federateName : fed.getJoinedFederateTypes()) {
            status.append(federateName).append(",");
          }
          status.append("} ");
        }
      }

      status.append("|").append(dots).append("   ");
      System.out.print(status);
      System.out.flush();
      
      // Save status to JSON file periodically
      saveStatusToJson();
      
      count++;
    }

    socketServer.stop();
    System.out.println("LiteRti shutting down.");
  }

  public void shutdown() {
    running = false;
    if (socketServer != null) {
      socketServer.stop();
      System.out.println("LiteRti shutting down.");
    }
  }

  private void saveStatusToJson() {
    try {
      long currentTime = System.currentTimeMillis();
      if (currentTime - lastSaveTime < SAVE_INTERVAL_MS) {
        return; // Not yet time to save
      }
      lastSaveTime = currentTime;

      Map<String, Object> status = new LinkedHashMap<>();
      status.put("timestamp", Instant.now().toString());
      status.put("federations", this.serverContext.getAllFederationExecutions().size());
      status.put("federates", this.serverContext.getConnectedFederates().size());

      Map<String, Object> federationDetails = new LinkedHashMap<>();
      for (String fedName : this.serverContext.getAllFederationExecutions()) {
        var fed = this.serverContext.getFederation(fedName);
        if (fed != null) {
          Map<String, Object> fedInfo = new LinkedHashMap<>();
          for (String federateName : fed.getJoinedFederateTypes()) {
            Map<String, Object> federateData = new LinkedHashMap<>();
            federateData.put("objectInstances", fed.debugGetObjectInstancesForFederate(federateName));
            federateData.put("interactionsSent", fed.debugGetInteractionsSentCount(federateName));
            federateData.put("interactionsReceived", fed.debugGetInteractionsReceivedCount(federateName));
            fedInfo.put(federateName, federateData);
          }
          federationDetails.put(fedName, fedInfo);
        }
      }
      status.put("federationDetails", federationDetails);

      try (FileWriter writer = new FileWriter(STATUS_FILE)) {
        writer.write(jsonb.toJson(status));
      }
    } catch (IOException e) {
      System.err.println("Failed to save RTI status to JSON: " + e.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    LiteRtiApplication application = new LiteRtiApplication();

    // Add a shutdown hook to gracefully stop the application when the JVM is shutting down
    Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));
    application.run();
  }
}
