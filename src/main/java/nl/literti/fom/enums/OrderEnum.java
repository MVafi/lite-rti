package nl.literti.fom.enums;

/** Enumeration representing the order property of various FOM elements (present in FOM) */
public enum OrderEnum {
  TIMESTAMP,
  RECEIVE;

  public static OrderEnum fromFomString(String fomString) throws Exception {
    if (fomString.equalsIgnoreCase("timestamp")) return TIMESTAMP;
    else if (fomString.equalsIgnoreCase("receive")) return RECEIVE;
    else throw new Exception("Unsupported OrderEnum found: " + fomString);
  }
};
