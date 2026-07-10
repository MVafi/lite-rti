package nl.tno.netnbus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages handle generation and lookup for HLA interaction classes and parameters. Thread-safe
 * using atomic counters and concurrent maps.
 */
public class HandleRegistry {
  private final AtomicInteger nextInteractionClassHandle = new AtomicInteger(1000);
  private final AtomicInteger nextParameterHandle = new AtomicInteger(2000);

  // Bidirectional maps for interactions
  private final ConcurrentHashMap<String, Integer> interactionNameToHandle =
      new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Integer, String> interactionHandleToName =
      new ConcurrentHashMap<>();

  // Bidirectional maps for parameters
  private final ConcurrentHashMap<String, Integer> parameterNameToHandle =
      new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Integer, String> parameterHandleToName =
      new ConcurrentHashMap<>();

  /**
   * Gets or creates a handle for an interaction class name.
   *
   * @param name the interaction class name
   * @return a unique handle for this interaction class
   */
  public int getInteractionClassHandle(String name) {
    return interactionNameToHandle.computeIfAbsent(
        name,
        key -> {
          int handle = nextInteractionClassHandle.getAndIncrement();
          interactionHandleToName.put(handle, key);
          return handle;
        });
  }

  /**
   * Gets the interaction class name for a handle.
   *
   * @param handle the interaction class handle
   * @return the interaction class name, or null if not found
   */
  public String getInteractionClassName(int handle) {
    return interactionHandleToName.get(handle);
  }

  /**
   * Gets or creates a handle for a parameter name.
   *
   * @param name the parameter name
   * @return a unique handle for this parameter
   */
  public int getParameterHandle(String name) {
    return parameterNameToHandle.computeIfAbsent(
        name,
        key -> {
          int handle = nextParameterHandle.getAndIncrement();
          parameterHandleToName.put(handle, key);
          return handle;
        });
  }

  /**
   * Gets the parameter name for a handle.
   *
   * @param handle the parameter handle
   * @return the parameter name, or null if not found
   */
  public String getParameterName(int handle) {
    return parameterHandleToName.get(handle);
  }
}
