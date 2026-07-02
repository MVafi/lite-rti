package nl.tno.netnbus;

import nl.tno.netnbus.server.NetnBusContext;
import nl.tno.netnbus.server.NetnBusSocketServer;

public class NetnBusApplication {

  private final NetnBusContext context;
  private NetnBusSocketServer socketServer;
  private volatile boolean running = true;

  public NetnBusApplication() {
    this.context = new NetnBusContext();
  }

  // public NetnBusAmbassador createRTIambassador() {
  //   return new NetnBusAmbassador();
  // }

  public void run() throws Exception {
    // Start the socket server with shared context
    this.socketServer = new NetnBusSocketServer(this.context);
    this.socketServer.start();

    int count = 0;
    // TERMINAL STATE PRINTER
    while (running) {
      Thread.sleep(1000);
      String dots = ".".repeat(count % 4);
      StringBuilder status = new StringBuilder();
      status.append("\r| Federations: ").append(this.context.getAllFederationExecutions().size());
      status.append(" | Federates: ").append(this.context.getConnectedFederates().size());
      status.append(" | ");

      for (String fedName : this.context.getAllFederationExecutions()) {
        var fed = this.context.getFederation(fedName);
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
      count++;
    }

    socketServer.stop();
    System.out.println("NETN Bus shutting down.");
  }

  public void shutdown() {
    running = false;
    if (socketServer != null) {
      socketServer.stop();
      System.out.println("NETN Bus shutting down.");
    }
  }

  public static void main(String[] args) throws Exception {
    NetnBusApplication application = new NetnBusApplication();

    // Add a shutdown hook to gracefully stop the application when the JVM is shutting down
    Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));
    application.run();
  }
}
