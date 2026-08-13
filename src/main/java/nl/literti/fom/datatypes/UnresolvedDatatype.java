package nl.literti.fom.datatypes;

import java.io.Serializable;
import java.lang.foreign.Linker;
import nl.literti.fom.FomDatatype;
import nl.literti.fom.FomDatatypeEnum;

/**
 * This type is used as a placeholder while parsing the FOM or merging FOM modules. At parse time,
 * the list of available types is incomplete and type declarations may reference other types that
 * have not been parsed in yet.
 *
 * <p>Thus any types that reference another type are provided with a {@link UnresolvedDatatype}
 * named after the type that they reference. Once all datatypes have been parsed, a linking pass
 * takes place and will resolve all {@link UnresolvedDatatype} instances to the actual type that
 * they represent.
 *
 * @see Linker
 */

// was DatatypePlaceholder
public class UnresolvedDatatype implements FomDatatype, Serializable {
  // ----------------------------------------------------------
  //                    STATIC VARIABLES
  // ----------------------------------------------------------
  private static final long serialVersionUID = 3112252018924L;

  // ----------------------------------------------------------
  //                   INSTANCE VARIABLES
  // ----------------------------------------------------------
  private String name;

  // ----------------------------------------------------------
  //                      CONSTRUCTORS
  // ----------------------------------------------------------
  public UnresolvedDatatype(String name) {
    this.name = name;
  }

  public UnresolvedDatatype(FomDatatype type) {
    this.name = type.getName();
  }

  // ----------------------------------------------------------
  //                    INSTANCE METHODS
  // ----------------------------------------------------------
  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public FomDatatypeEnum getDatatypeEnum() {
    return FomDatatypeEnum.NA;
  }

  @Override
  public UnresolvedDatatype createUnlinkedClone() {
    // Clones of placeholder types should never be requested
    throw new IllegalStateException("creating a clone of Placeholder type");
  }

  // ----------------------------------------------------------
  //                     STATIC METHODS
  // ----------------------------------------------------------
}
