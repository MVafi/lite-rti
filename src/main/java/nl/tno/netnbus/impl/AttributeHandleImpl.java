package nl.tno.netnbus.impl;

import hla.rti1516e.AttributeHandle;

/** Implementation of AttributeHandle that wraps an integer handle value */
public class AttributeHandleImpl implements AttributeHandle {
  private static final long serialVersionUID = 1L;
  private final int handle;

  public AttributeHandleImpl(int handle) {
    this.handle = handle;
  }

  public int getHandle() {
    return handle;
  }

  // Standard Java equals implementation
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof AttributeHandleImpl)) return false;
    AttributeHandleImpl other = (AttributeHandleImpl) obj;
    return handle == other.handle;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(handle);
  }

  @Override
  public int encodedLength() {
    return Integer.BYTES;
  }

  @Override
  public void encode(byte[] buffer, int offset) {
    // Done by oorti??
    // buffer[offset] = (byte) (handle >> 24);
    // buffer[offset + 1] = (byte) (handle >> 16);
    // buffer[offset + 2] = (byte) (handle >> 8);
    // buffer[offset + 3] = (byte) handle;
  }

  @Override
  public String toString() {
    return "AttributeHandle(" + handle + ")";
  }
}
