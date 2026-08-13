package nl.literti.utils;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import java.util.Collection;
import java.util.HashSet;

/**
 * HLA 1516e-compliant implementation of AttributeHandleSet. Uses HashSet for storage and enforces
 * type validation on add/remove operations.
 */
public class AttributeHandleSetImpl extends HashSet<AttributeHandle> implements AttributeHandleSet {
  private static final long serialVersionUID = 1L;

  @Override
  public boolean add(AttributeHandle handle) {
    if (!(handle instanceof AttributeHandle)) {
      throw new IllegalArgumentException("Argument must be an AttributeHandle");
    }
    return super.add(handle);
  }

  @Override
  public boolean remove(Object o) {
    if (!(o instanceof AttributeHandle)) {
      throw new IllegalArgumentException("Argument must be an AttributeHandle");
    }
    return super.remove(o);
  }

  @Override
  public boolean addAll(Collection<? extends AttributeHandle> c) {
    if (!(c instanceof AttributeHandleSet)) {
      throw new IllegalArgumentException("Argument must be an AttributeHandleSet");
    }
    return super.addAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    if (!(c instanceof AttributeHandleSet)) {
      throw new IllegalArgumentException("Argument must be an AttributeHandleSet");
    }
    return super.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    if (!(c instanceof AttributeHandleSet)) {
      throw new IllegalArgumentException("Argument must be an AttributeHandleSet");
    }
    return super.retainAll(c);
  }

  @Override
  public AttributeHandleSet clone() {
    AttributeHandleSetImpl clone = new AttributeHandleSetImpl();
    clone.addAll(this);
    return clone;
  }
}
