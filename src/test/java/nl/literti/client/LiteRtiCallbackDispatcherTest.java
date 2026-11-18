package nl.literti.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class LiteRtiCallbackDispatcherTest {

  @Test(timeout = 10000)
  public void readerCanCompleteResponseWhileCallbackWaits() throws Exception {
    try (LiteRtiClientDispatcher dispatcher = new LiteRtiClientDispatcher();
        ExecutorService reader = Executors.newSingleThreadExecutor()) {
      CountDownLatch callbackStarted = new CountDownLatch(1);
      CompletableFuture<String> response = new CompletableFuture<>();
      CompletableFuture<String> callbackResult = new CompletableFuture<>();

      reader.submit(() -> {
        Thread readerThread = Thread.currentThread();
        dispatcher.queueCallback(() -> {
          callbackStarted.countDown();
          try {
            assertNotSame(readerThread, Thread.currentThread());
            // Simulate a synchronous RTI service called from a user callback.
            callbackResult.complete(response.get(3, TimeUnit.SECONDS));
          } catch (Exception | AssertionError e) {
            callbackResult.completeExceptionally(e);
          }
        });
        try {
          assertTrue(callbackStarted.await(3, TimeUnit.SECONDS));
          // The reader must remain free to process the incoming response.
          response.complete("registered");
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          callbackResult.completeExceptionally(e);
        }
      });

      assertEquals("registered", callbackResult.get(5, TimeUnit.SECONDS));
    }
  }

  @Test(timeout = 10000)
  public void disabledCallbacksResumeInFifoOrder() throws Exception {
    try (LiteRtiClientDispatcher dispatcher = new LiteRtiClientDispatcher()) {
      List<Integer> delivered = Collections.synchronizedList(new ArrayList<>());
      CompletableFuture<Void> finished = new CompletableFuture<>();
      dispatcher.disable();
      dispatcher.queueCallback(() -> delivered.add(1));
      dispatcher.queueCallback(() -> delivered.add(2));
      assertTrue(delivered.isEmpty());

      dispatcher.enable();
      dispatcher.queueCallback(() -> delivered.add(3));
      dispatcher.queueCallback(() -> finished.complete(null));
      finished.get(3, TimeUnit.SECONDS);
      assertEquals(List.of(1, 2, 3), delivered);
    }
  }

  @Test(timeout = 10000)
  public void callbackCanDisableDeliveryWithoutLosingQueuedCallbacks() throws Exception {
    try (LiteRtiClientDispatcher dispatcher = new LiteRtiClientDispatcher()) {
      List<Integer> delivered = Collections.synchronizedList(new ArrayList<>());
      CompletableFuture<Void> disabled = new CompletableFuture<>();
      CompletableFuture<Void> finished = new CompletableFuture<>();
      dispatcher.queueCallback(() -> {
        delivered.add(1);
        dispatcher.disable();
        disabled.complete(null);
      });
      disabled.get(3, TimeUnit.SECONDS);
      dispatcher.queueCallback(() -> delivered.add(2));
      dispatcher.queueCallback(() -> delivered.add(3));
      dispatcher.queueCallback(() -> finished.complete(null));
      assertEquals(List.of(1), delivered);

      dispatcher.enable();
      finished.get(3, TimeUnit.SECONDS);
      assertEquals(List.of(1, 2, 3), delivered);
    }
  }

  @Test(timeout = 10000)
  public void callbackExceptionDoesNotStopSubsequentDelivery() throws Exception {
    try (LiteRtiClientDispatcher dispatcher = new LiteRtiClientDispatcher()) {
      CompletableFuture<String> finished = new CompletableFuture<>();
      dispatcher.queueCallback(() -> {
        throw new IllegalStateException("intentional test failure");
      });
      dispatcher.queueCallback(() -> finished.complete("next callback"));
      assertEquals("next callback", finished.get(3, TimeUnit.SECONDS));
    }
  }

  @Test
  public void closingDiscardsQueuedAndFutureCallbacks() {
    try (LiteRtiClientDispatcher dispatcher = new LiteRtiClientDispatcher()) {
      AtomicInteger delivered = new AtomicInteger();
      dispatcher.disable();
      dispatcher.queueCallback(delivered::incrementAndGet);
      dispatcher.close();
      dispatcher.enable();
      dispatcher.queueCallback(delivered::incrementAndGet);
      assertEquals(0, delivered.get());
    }
  }

  @Test(timeout = 10000)
  public void closingInterruptsBlockedCallbackWorker() throws Exception {
    try (LiteRtiClientDispatcher dispatcher = new LiteRtiClientDispatcher()) {
      CountDownLatch callbackStarted = new CountDownLatch(1);
      CountDownLatch blocked = new CountDownLatch(1);
      CompletableFuture<Boolean> interrupted = new CompletableFuture<>();
      dispatcher.queueCallback(() -> {
        callbackStarted.countDown();
        try {
          blocked.await();
          interrupted.complete(false);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          interrupted.complete(true);
        }
      });
      assertTrue(callbackStarted.await(3, TimeUnit.SECONDS));
      dispatcher.close();
      assertTrue(interrupted.get(3, TimeUnit.SECONDS));
    }
  }
}