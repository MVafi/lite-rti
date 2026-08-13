package nl.literti.fom.datatypes;

import java.io.Serializable;
import nl.literti.fom.FomEnumerator;
import nl.literti.fom.datatypes.carriers.Alternative;

/**
 * This type is used as a placeholder while parsing and merging Variant Record {@link Alternative}
 * entries. At parse time, the list of available {@link EnumeratedType} values is incomplete and as
 * such we are unable to tell if the Alternative's enumerator field contains a valid enumerator
 * value.
 */
public class UnresolvedEnumerator implements FomEnumerator, Serializable {
  // ----------------------------------------------------------
  //                    STATIC VARIABLES
  // ----------------------------------------------------------
  private static final long serialVersionUID = 3112252018924L;

  // ----------------------------------------------------------
  //                   INSTANCE VARIABLES
  // ----------------------------------------------------------
  public String name;

  // ----------------------------------------------------------
  //                      CONSTRUCTORS
  // ----------------------------------------------------------
  public UnresolvedEnumerator(String name) {
    this.name = name;
  }

  public UnresolvedEnumerator(FomEnumerator enumerator) {
    this.name = enumerator.getName();
  }

  // ----------------------------------------------------------
  //                    INSTANCE METHODS
  // ----------------------------------------------------------
  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Number getValue() {
    return 0;
  }

  // ----------------------------------------------------------
  //                     STATIC METHODS
  // ----------------------------------------------------------
}
