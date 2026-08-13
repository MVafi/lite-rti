package nl.literti.utils;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleSetFactory;

// Simple implementation that allows the federate to create a new AttributeHandleSet, following the
// 1516e standard
public class AttributeHandleSetFactoryImpl implements AttributeHandleSetFactory {
  public AttributeHandleSet create() {
    return new AttributeHandleSetImpl();
  }
}
