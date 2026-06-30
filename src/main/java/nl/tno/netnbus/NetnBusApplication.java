package nl.tno.netnbus;

import nl.tno.netnbus.socket.NetnBusSocketServer;

public class NetnBusApplication {

  private final NetnBusContext context;
  private NetnBusSocketServer socketServer;
  private volatile boolean running = true;

  public NetnBusApplication() {
    this.context = new NetnBusContext();
  }

  public NetnBusOORTIambassador createRTIambassador() {
    return new NetnBusOORTIambassador(context);
  }

  public void run() throws Exception {
    // Start the socket server
    socketServer = new NetnBusSocketServer();
    socketServer.start();

    System.out.println("NETN Bus started. Waiting for federates...");

    while (running) {
      context.processEvents();
      Thread.sleep(3000);
      System.out.println("sleep...");
    }

    socketServer.stop();
    System.out.println("NETN Bus shutting down.");
  }

  public void shutdown() {
    running = false;
  }

  public static void main(String[] args) throws Exception {
    NetnBusApplication application = new NetnBusApplication();
    Runtime.getRuntime().addShutdownHook(new Thread(application::shutdown));
    application.run();
  }
}
