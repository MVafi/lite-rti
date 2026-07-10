package nl.tno.netnbus.impl;

import hla.rti1516e.ParameterHandle;

/** Simple implementation of ParameterHandle wrapping an int handle */
public class ParameterHandleImpl implements ParameterHandle {
  private final int handle;

  public ParameterHandleImpl(int handle) {
    this.handle = handle;
  }

  public int getHandle() {
    return handle;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof ParameterHandleImpl)) return false;
    return this.handle == ((ParameterHandleImpl) other).handle;
  }

  @Override
  public int hashCode() {
    return handle;
  }

  @Override
  public int encodedLength() {
    return 4;
  }

  @Override
  public void encode(byte[] buffer, int offset) {
    buffer[offset] = (byte) ((handle >> 24) & 0xFF);
    buffer[offset + 1] = (byte) ((handle >> 16) & 0xFF);
    buffer[offset + 2] = (byte) ((handle >> 8) & 0xFF);
    buffer[offset + 3] = (byte) (handle & 0xFF);
  }

  @Override
  public String toString() {
    return "ParameterHandle(" + handle + ")";
  }
}
