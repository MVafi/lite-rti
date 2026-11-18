package nl.literti.client;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Separate thread delivering callbacks in FIFO order. */
final class LiteRtiClientDispatcher implements AutoCloseable {

  private final Queue<Runnable> callbacks = new ArrayDeque<>();
  private final ExecutorService executor = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon(true).name("lite-rti-client-callback").factory());
  private boolean enabled = true;
  private boolean draining;
  private boolean closed;

  synchronized void queueCallback(Runnable callback) {
    if (closed) {
      return;
    }
    callbacks.offer(callback);
    scheduleDrain();
  }

  synchronized void disable() {
    // Keep queueing callbacks, but pause draining the queue until re-enabled
    enabled = false;
  }

  synchronized void enable() {
    if (closed) {
      return;
    }
    enabled = true;
    scheduleDrain();
  }

  // Called only while holding this dispatcher's monitor.
  private void scheduleDrain() {
    if (enabled && !draining && !callbacks.isEmpty()) {
      draining = true;
      executor.execute(this::drain);
    }
  }

  private void drain() {
    while (true) {
      Runnable callback;
      synchronized (this) {
        if (closed || !enabled || (callback = callbacks.poll()) == null) {
          draining = false;
          return;
        }
      }
      // Never hold the queue lock while calling user code or waiting for an RTI response.
      try {
        callback.run();
      } catch (RuntimeException e) {
        System.err.println("[ClientCallback] Error delivering callback: " + e.getMessage());
      }
    }
  }

  @Override
  public synchronized void close() {
    closed = true;
    callbacks.clear();
    executor.shutdownNow();
  }
}