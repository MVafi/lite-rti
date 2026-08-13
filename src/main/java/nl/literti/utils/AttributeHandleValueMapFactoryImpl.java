package nl.literti.utils;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.AttributeHandleValueMapFactory;

public class AttributeHandleValueMapFactoryImpl implements AttributeHandleValueMapFactory {

    /**
     * Creates a new AttributeHandleValueMap with the specified capacity.
     * Implements the required interface method.
     */
    @Override
    public AttributeHandleValueMap create(int capacity) {
        return new AttributeHandleValueMapImpl();
    }
}
