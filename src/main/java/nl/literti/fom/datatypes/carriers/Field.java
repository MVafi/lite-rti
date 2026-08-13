package nl.literti.fom.datatypes.carriers;

import java.io.Serializable;
import java.util.Objects;
import nl.literti.fom.FomDatatype;
import nl.literti.fom.datatypes.FixedRecordType;
import nl.literti.fom.datatypes.UnresolvedDatatype;

/** Describes a field of a {@link FixedRecordType} datatype */
public class Field implements Serializable {
  // ----------------------------------------------------------
  //                    STATIC VARIABLES
  // ----------------------------------------------------------
  private static final long serialVersionUID = 3112252018924L;

  // ----------------------------------------------------------
  //                   INSTANCE VARIABLES
  // ----------------------------------------------------------
  private String name;
  private FomDatatype datatype;

  // ----------------------------------------------------------
  //                      CONSTRUCTORS
  // ----------------------------------------------------------
  public Field(String name, FomDatatype datatype) {
    this.name = name;
    this.datatype = datatype;
  }

  // ----------------------------------------------------------
  //                    INSTANCE METHODS
  // ----------------------------------------------------------
  public String getName() {
    return this.name;
  }

  public FomDatatype getDatatype() {
    return this.datatype;
  }

  public void setDatatype(FomDatatype datatype) {
    this.datatype = datatype;
  }

  /**
   * Creates a copy of this field with its datatype replaced by a {@link DatatypePlaceholder}.
   *
   * <p>This method is used by the model merger while it imports extension datatypes into a base
   * model. As dependent datatypes may not have been imported at the time this datatype is imported
   * into the base model, the placeholder is used as a temporary reference. After all extension
   * datatypes have been imported into the base model, all placeholder types will be resolved to
   * their actual representations.
   *
   * @return a copy of this field with its datatype replaced with a {@link DatatypePlaceholder}
   */
  public Field createUnlinkedClone() {
    return new Field(this.name, new UnresolvedDatatype(datatype));
  }

  @Override
  public boolean equals(Object other) {
    boolean equal = false;

    if (other instanceof Field) {
      Field asField = (Field) other;
      equal =
          Objects.equals(this.name, asField.name)
              && Objects.equals(this.datatype, asField.datatype);
    }

    return equal;
  }

  // ----------------------------------------------------------
  //                     STATIC METHODS
  // ----------------------------------------------------------
}
