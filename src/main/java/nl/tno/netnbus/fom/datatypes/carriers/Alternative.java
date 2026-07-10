package nl.tno.netnbus.fom.datatypes.carriers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.tno.netnbus.fom.FomDatatype;
import nl.tno.netnbus.fom.FomEnumerator;
import nl.tno.netnbus.fom.datatypes.UnresolvedDatatype;
import nl.tno.netnbus.fom.datatypes.UnresolvedEnumerator;
import nl.tno.netnbus.fom.datatypes.VariantRecordType;

/**
 * Represents one particular form that a {@link VariantRecordType} may assume.
 */
public class Alternative implements Serializable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Set<FomEnumerator> enumerators;
	private String name;
	private FomDatatype datatype;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Constructor for an Alternative with specified name, datatype and enumerator collection.
	 * 
	 * @param name The name of the alternative
	 * @param datatype The datatype that the alternative will store
	 * @param enumerators The collection of discriminant enumerators that this type is valid for
	 */
	public Alternative( String name, 
	                    FomDatatype datatype, 
	                    FomEnumerator... enumerators )
	{
		this( name, datatype, Arrays.asList(enumerators) );
	}
	
	/**
	 * Constructor for an Alternative with specified name, datatype and enumerator collection.
	 * 
	 * @param name The name of the alternative
	 * @param datatype The datatype that the alternative will store
	 * @param enumerators The collection of discriminant enumerators that this type is valid for
	 */
	public Alternative( String name, 
	                    FomDatatype datatype, 
	                    Collection<? extends FomEnumerator> enumerators )
	{
		this.name = name;
		this.datatype = datatype;
		this.enumerators = new HashSet<FomEnumerator>( enumerators );
	}

	public String getName()
	{
		return this.name;
	}
	
	public FomDatatype getDatatype()
	{
		return this.datatype;
	}
	
	public void setDatatype( FomDatatype datatype )
	{
		this.datatype = datatype;
	}
	
	public Set<FomEnumerator> getEnumerators()
	{
		return new HashSet<FomEnumerator>( this.enumerators );
	}
	
	public void setEnumerators( Set<FomEnumerator> enumerators )
	{
		this.enumerators = new HashSet<FomEnumerator>( enumerators );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Creates a copy of this alternative with its datatype replaced by a {@link DatatypePlaceholder}
	 * and its enumerators replaced by {@link EnumeratorPlaceholder} references.
	 * <p/>
	 * This method is used by the model merger while it imports extension datatypes into a base 
	 * model. As dependent datatypes may not have been imported at the time this datatype is 
	 * imported into the base model, the placeholders are used as a temporary reference. After all
	 * extension datatypes have been imported into the base model, all placeholder types will
	 * be resolved to their actual representations.
	 * 
	 * @return a copy of this alternative with its datatype replaced by a {@link DatatypePlaceholder}
	 *         and its enumerators replaced by {@link EnumeratorPlaceholder} references.
	 */
	public Alternative createUnlinkedClone()
	{
		Set<UnresolvedEnumerator> placeholderEnumerators = new HashSet<UnresolvedEnumerator>();
		for( FomEnumerator enumerator : this.enumerators )
			placeholderEnumerators.add( new UnresolvedEnumerator(enumerator) );
		
		return new Alternative( this.name, 
		                        new UnresolvedDatatype(this.datatype), 
		                        placeholderEnumerators );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
