package nl.literti.impl;

import hla.rti1516e.ObjectClassHandle;

/** Implementation of ObjectClassHandle that wraps an integer handle value */
public class ObjectClassHandleImpl implements ObjectClassHandle {
  private static final long serialVersionUID = 1L;
  private final int handle;

  public ObjectClassHandleImpl(int handle) {
    this.handle = handle;
  }

  public int getHandle() {
    return handle;
  }

  // Standard Java equals implementation
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof ObjectClassHandleImpl)) return false;
    ObjectClassHandleImpl other = (ObjectClassHandleImpl) obj;
    return handle == other.handle;
  }

    // Standard Java hashCode implementation, fast lookup integer
  // (I think the only relevant method)
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
    // Done by oorti
    // buffer[offset] = (byte) (handle >> 24);
    // buffer[offset + 1] = (byte) (handle >> 16);
    // buffer[offset + 2] = (byte) (handle >> 8);
    // buffer[offset + 3] = (byte) handle;
  }

  @Override
  public String toString() {
    return "ObjectClassHandle(" + handle + ")";
  }
}
