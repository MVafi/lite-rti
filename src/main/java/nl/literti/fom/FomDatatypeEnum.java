package nl.literti.fom;

public enum FomDatatypeEnum {
  /** Underpinning of all OMT datatypes */
  BASIC,
  /** Simple, scalar data items */
  SIMPLE,
  /** Data elements that can take on a finite discrete set of possible values */
  ENUMERATED,
  /** Indexed homogenous collections of datatypes */
  ARRAY,
  /** Heterogeneous collections of types */
  FIXEDRECORD,
  /** Discriminated unions of types */
  VARIANTRECORD,
  /** NA type (supports HLAprivelegeToDelete in 1516) */
  NA;

  public String toString() {
    switch (this) {
      case BASIC:
        return "basicData";
      case SIMPLE:
        return "simpleData";
      case ENUMERATED:
        return "enumeratedData";
      case ARRAY:
        return "arrayData";
      case FIXEDRECORD:
        return "fixedRecordData";
      case VARIANTRECORD:
        return "variantRecordData";
      default:
      case NA:
        return "NA";
    }
  }
}
