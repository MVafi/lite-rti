package nl.literti.fom;

import nl.literti.fom.datatypes.BasicType;
import nl.literti.fom.datatypes.EnumeratedType;

// TODO: Might be better to remove this file somehow

/**
 * Describes a possible value of an {@link EnumeratedType}.
 *
 * <p>According to the specification, the value of an {@link EnumeratedType} can be any {@link
 * BasicType}. As a result, we'll use the {@link Number} class to represent it, as that is large
 * enough to represent all basic types.
 *
 * <p><b>Note</b> An interface is required for enumerators due to the working assumption that
 * datatypes may be imported in an arbitrary order. {@link Alternative} entries reference
 * enumerators and at parse/merge time we must work on the assumption that the {@link
 * EnumeratedType} of the discriminant not been imported yet. The interface allows the parser/merger
 * to insert a placeholder until all datatypes have been imported and the {@link Linker} is able to
 * resolve them to their complete representation
 */
public interface FomEnumerator {
  /** Returns the name of the enumerator constant */
  public String getName();

  /** Returns the value of the enumerator constant */
  public Number getValue();
}
