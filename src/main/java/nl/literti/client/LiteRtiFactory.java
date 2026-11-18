package nl.literti.client;

import hla.rti1516e.RtiFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.RTIinternalError;

/** HLA service provider for creating LiteRTI ambassadors. */
public class LiteRtiFactory implements RtiFactory {

  @Override
  public LiteRtiAmbassador getRtiAmbassador() {
    return new LiteRtiAmbassador();
  }

  @Override
  public EncoderFactory getEncoderFactory() throws RTIinternalError {
    throw new RTIinternalError("LiteRTI does not currently provide an HLA EncoderFactory");
  }

  @Override
  public String rtiName() {
    return "LiteRTI";
  }

  @Override
  public String rtiVersion() {
    //todo: update this
    String version = LiteRtiFactory.class.getPackage().getImplementationVersion();
    return version != null ? version : "development";
  }
}