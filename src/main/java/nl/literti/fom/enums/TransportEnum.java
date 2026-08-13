package nl.literti.fom.enums;

/** Enumeration representing the transport of the various FOM types (present in FOM) */
public enum TransportEnum {
  RELIABLE,
  BEST_EFFORT;

  public static TransportEnum fromFomString(String fomString) {
    if (fomString.equalsIgnoreCase("HLAreliable")) return TransportEnum.RELIABLE;
    else return TransportEnum.BEST_EFFORT;
  }
};
