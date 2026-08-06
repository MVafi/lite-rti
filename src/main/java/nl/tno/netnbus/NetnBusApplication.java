package nl.tno.netnbus;

import nl.tno.netnbus.server.NetnBusServerContext;
import nl.tno.netnbus.server.NetnBusServerSocket;

public class NetnBusApplication {

  private final NetnBusServerContext serverContext;
  private NetnBusServerSocket socketServer;
  private volatile boolean running = true;

  public NetnBusApplication() {
    this.serverContext = new NetnBusServerContext();
  }

  public void run() throws Exception {
    // Start the socket server with shared context
    this.socketServer = new NetnBusServerSocket(this.serverContext);
    this.socketServer.start();

    int count = 0;
    // TERMINAL STATE PRINTER
    while (running) {
      Thread.sleep(1000);
      String dots = ".".repeat(count % 4);
      StringBuilder status = new StringBuilder();
      status.append("\r| Federations: ").append(this.serverContext.getAllFederationExecutions().size());
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
