package nl.literti.utils;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.ParameterHandleValueMapFactory;

public class ParameterHandleValueMapFactoryImpl implements ParameterHandleValueMapFactory {
  /**
 * Creates a new ParameterHandleValueMap with the specified capacity. Implements the required
 * interface method.
 */
  @Override
  public ParameterHandleValueMap create(int capacity) {
    return new ParameterHandleValueMapImpl();
  }
}
