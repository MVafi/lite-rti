package nl.literti.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;

public class ParameterHandleValueMapImpl implements ParameterHandleValueMap {
    
    
  private static final long serialVersionUID = 1L;

  private final Map<ParameterHandle, byte[]> map = new ConcurrentHashMap<>();

  /**
   * Returns a reference to the value to which this map maps the specified key. Returns null if the
   * map contains no mapping for this key.
   */
  @Override
  public ByteWrapper getValueReference(ParameterHandle key) {
    byte[] value = map.get(key);
    if (value != null) {
      return new ByteWrapper(value);
    }
    return null;
  }

  /**
   * Returns the specified reference updated to the value to which this map maps the specified key.
   * Returns null if the map contains no mapping for this key.
   */
  @Override
  public ByteWrapper getValueReference(ParameterHandle key, ByteWrapper byteWrapper) {
    byte[] value = map.get(key);
    if (value != null) {
      // Create a new ByteWrapper with the value
      return new ByteWrapper(value);
    }
    return null;
  }

  // ===== Map Interface Implementation =====

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public byte[] get(Object key) {
    return map.get(key);
  }

  @Override
  public byte[] put(ParameterHandle key, byte[] value) {
    if (key == null) {
      throw new IllegalArgumentException("ParameterHandle key cannot be null");
    }
    if (value == null) {
      throw new IllegalArgumentException("byte[] value cannot be null");
    }
    return map.put(key, value);
  }

  @Override
  public byte[] remove(Object key) {
    if (!(key instanceof ParameterHandle)) {
      throw new IllegalArgumentException("Key must be an ParameterHandle");
    }
    return map.remove(key);
  }

  @Override
  public void putAll(Map<? extends ParameterHandle, ? extends byte[]> m) {
    if (m == null) {
      throw new IllegalArgumentException("Map cannot be null");
    }
    for (Entry<? extends ParameterHandle, ? extends byte[]> entry : m.entrySet()) {
      if (entry.getKey() == null || entry.getValue() == null) {
        throw new IllegalArgumentException("Null keys and values are not allowed");
      }
      map.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Set<ParameterHandle> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<byte[]> values() {
    return map.values();
  }

  @Override
  public Set<Entry<ParameterHandle, byte[]>> entrySet() {
    return map.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ParameterHandleValueMapImpl)) return false;
    ParameterHandleValueMapImpl that = (ParameterHandleValueMapImpl) o;
    return map.equals(that.map);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    ParameterHandleValueMapImpl cloned = (ParameterHandleValueMapImpl) super.clone();
    // Create a new map with copies of the entries
    cloned.map.putAll(this.map);
    return cloned;
  }

  @Override
  public String toString() {
    return "ParameterHandleValueMapImpl{" + map + '}';
  }
}
