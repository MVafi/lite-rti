package nl.tno.netnbus.impl;

import hla.rti1516e.InteractionClassHandle;

/**
 * Simple implementation of InteractionClassHandle, that is being expected by the RTIambassador,
 * wrapping an integer handle.
 */
public class InteractionClassHandleImpl implements InteractionClassHandle {
  private final int handle; // Always 32 bits

  public InteractionClassHandleImpl(int handle) {
    this.handle = handle;
  }

  public int getHandle() {
    return handle;
  }

  // Standard Java equals implementation
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof InteractionClassHandleImpl)) return false;
    return this.handle == ((InteractionClassHandleImpl) other).handle;
  }

  // Standard Java hashCode implementation, fast lookup integer
  // (I think the only relevant method)
  @Override
  public int hashCode() {
    return handle;
  }

  // 32-bit integer = 4 bytes
  @Override
  public int encodedLength() {
    return 4;
  }

  @Override
  public void encode(byte[] buffer, int offset) {
    // Done by oorti
    // buffer[offset] = (byte) ((handle >> 24) & 0xFF);
    // buffer[offset + 1] = (byte) ((handle >> 16) & 0xFF);
    // buffer[offset + 2] = (byte) ((handle >> 8) & 0xFF);
    // buffer[offset + 3] = (byte) (handle & 0xFF);
  }

  @Override
  public String toString() {
    return "InteractionClassHandle(" + handle + ")";
  }
}
