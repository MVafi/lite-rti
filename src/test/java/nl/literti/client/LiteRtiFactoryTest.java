package nl.literti.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockConstruction;

import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.RTIinternalError;
import org.junit.Test;
import org.mockito.MockedConstruction;

public class LiteRtiFactoryTest {

  @Test
  public void factoryIsDiscoverableByName() throws Exception {
    RtiFactory factory = RtiFactoryFactory.getRtiFactory("LiteRTI");
    assertTrue(factory instanceof LiteRtiFactory);
    assertEquals("LiteRTI", factory.rtiName());
    assertTrue(!factory.rtiVersion().isBlank());
  }

  @Test
  public void defaultFactoryIsLiteRtiOnThisClasspath() throws Exception {
    assertTrue(RtiFactoryFactory.getRtiFactory() instanceof LiteRtiFactory);
  }

  @Test
  public void factoryCreatesFreshLiteRtiAmbassadors() throws Exception {
    // Avoid real socket connections while exercising actual ambassador construction.
    try (MockedConstruction<LiteRtiClientContext> contexts =
        mockConstruction(LiteRtiClientContext.class)) {
      RtiFactory factory = RtiFactoryFactory.getRtiFactory("LiteRTI");
      var first = factory.getRtiAmbassador();
      var second = factory.getRtiAmbassador();
      assertTrue(first instanceof LiteRtiAmbassador);
      assertTrue(second instanceof LiteRtiAmbassador);
      assertNotSame(first, second);
      assertEquals(2, contexts.constructed().size());
    }
  }

  @Test
  public void encoderFactoryReportsUnsupportedCapability() throws Exception {
    RtiFactory factory = RtiFactoryFactory.getRtiFactory("LiteRTI");
    RTIinternalError error = assertThrows(RTIinternalError.class, factory::getEncoderFactory);
    assertTrue(error.getMessage().contains("EncoderFactory"));
  }
}