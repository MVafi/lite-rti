package nl.tno.netnbus;

import nl.tno.netnbus.client.NetnBusAmbassador;
import nl.tno.netnbus.server.NetnBusContext;
import nl.tno.netnbus.server.NetnBusSocketServer;

public class NetnBusApplication {

  private final NetnBusContext context;
  private NetnBusSocketServer socketServer;
  private volatile boolean running = true;

  public NetnBusApplication() {
    this.context = new NetnBusContext();
  }

  public NetnBusAmbassador createRTIambassador() {
    return new NetnBusAmbassador();
  }

  public void run() throws Exception {
    // Start the socket server
    socketServer = new NetnBusSocketServer();
    socketServer.start();

    System.out.println("NETN Bus started. Waiting for federates...");

    int count = 0;
    while (running) {
        String dots = ".".repeat(count % 4);
        System.out.print("\r" + dots + "   "); // overwrite line + clear leftovers
        count++;
        Thread.sleep(1000);
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
