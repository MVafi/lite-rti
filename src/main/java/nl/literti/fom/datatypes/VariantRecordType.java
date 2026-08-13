package nl.literti.fom.datatypes;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import nl.literti.fom.FomDatatype;
import nl.literti.fom.FomDatatypeEnum;
import nl.literti.fom.datatypes.carriers.Alternative;

/**
 * This class contains metadata about a FOM Variant data type.
 *
 * <p>A variant record datatype represents a discriminated union of types.
 */
public class VariantRecordType implements FomDatatype, Serializable {
  // ----------------------------------------------------------
  //                    STATIC VARIABLES
  // ----------------------------------------------------------

  // ----------------------------------------------------------
  //                   INSTANCE VARIABLES
  // ----------------------------------------------------------
  private String name;
  private String discriminantName;
  private FomDatatype discriminantDatatype; // EnumeratedType or DatatypePlaceholder only
  private Set<Alternative> alternatives;

  // ----------------------------------------------------------
  //                      CONSTRUCTORS
  // ----------------------------------------------------------
  public VariantRecordType(
      String name,
      String discriminantName,
      FomDatatype discriminantDatatype,
      Collection<? extends Alternative> alternatives) {
    this.name = name;
    this.discriminantName = discriminantName;
    this.discriminantDatatype = discriminantDatatype;
    this.alternatives = new HashSet<Alternative>(alternatives);
  }

  // ----------------------------------------------------------
  //                    INSTANCE METHODS
  // ----------------------------------------------------------
  public String getDiscriminantName() {
    return this.discriminantName;
  }

  public FomDatatype getDiscriminantDatatype() {
    return this.discriminantDatatype;
  }

  public void setDiscriminantDatatype(FomDatatype datatype) {
    // Discriminants types can only be based on Enumerated types. We also have to allow
    // placeholder references for deferred linking
    if (datatype instanceof EnumeratedType || datatype instanceof UnresolvedDatatype)
      this.discriminantDatatype = datatype;
    else throw new IllegalArgumentException(datatype.getName() + " is not an Enumerated type");
  }

  public Set<Alternative> getAlternatives() {
    return new HashSet<Alternative>(this.alternatives);
  }

  @Override
  public boolean equals(Object other) {
    boolean equal = false;

    if (other instanceof VariantRecordType) {
      VariantRecordType asVariant = (VariantRecordType) other;
      equal =
          Objects.equals(this.name, asVariant.name)
              && Objects.equals(this.discriminantName, asVariant.discriminantName)
              && Objects.equals(this.discriminantDatatype, asVariant.discriminantDatatype)
              && Objects.equals(this.alternatives, asVariant.alternatives);
    }

    return equal;
  }

  @Override
  public String toString() {
    return this.name;
  }

  //////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////// IDatatype Interface ////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public FomDatatypeEnum getDatatypeEnum() {
    return FomDatatypeEnum.VARIANTRECORD;
  }

  @Override
  public VariantRecordType createUnlinkedClone() {
    Set<Alternative> unlinkedAlternatives = new HashSet<Alternative>();
    for (Alternative alternative : this.alternatives)
      unlinkedAlternatives.add(alternative.createUnlinkedClone());

    return new VariantRecordType(
        name, discriminantName, new UnresolvedDatatype(discriminantDatatype), unlinkedAlternatives);
  }

  // ----------------------------------------------------------
  //                     STATIC METHODS
  // ----------------------------------------------------------
}
