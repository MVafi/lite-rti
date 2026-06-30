package nl.tno.netnbus;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import hla.rti1516e.FederateAmbassador;

public class NetnBusContext {

  private final Map<String, FederateAmbassador> connectedFederates = new ConcurrentHashMap<>();
  private final Queue<Runnable> eventQueue = new ConcurrentLinkedQueue<>();

  public void addFederate(FederateAmbassador federateReference) {
    connectedFederates.put(federateReference.toString(), federateReference);
    System.out.println("Federate connected: " + federateReference.toString());
    System.out.println("Total federates: " + connectedFederates.size());
  }

  public void removeFederate(FederateAmbassador federateReference) {
    connectedFederates.remove(federateReference.toString());
    System.out.println("Federate disconnected: " + federateReference.toString());
  }

  /**
   * Queue an event to be processed in the main loop.
   * Thread-safe - can be called from any thread.
   */
  public void queueEvent(Runnable event) {
    eventQueue.offer(event);
  }

  /**
   * Process all pending events in the queue.
   * Called from the main event loop.
   */
  public void processEvents() {
    Runnable event;
    while ((event = eventQueue.poll()) != null) {
      try {
        event.run();
      } catch (Exception e) {
        System.err.println("Error processing event: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  /**
   * Get all connected federates for broadcasting callbacks.
   */
  public Map<String, FederateAmbassador> getConnectedFederates() {
    return connectedFederates;
  }

  /**
   * Broadcast a callback to all connected federates.
   */
  public void broadcastToFederates(FederateCallback callback) {
    for (FederateAmbassador federate : connectedFederates.values()) {
      queueEvent(() -> {
        try {
          callback.invoke(federate);
        } catch (Exception e) {
          System.err.println("Error invoking callback on federate: " + e.getMessage());
        }
      });
    }
  }

  @FunctionalInterface
  public interface FederateCallback {
    void invoke(FederateAmbassador federate) throws Exception;
  }
}
