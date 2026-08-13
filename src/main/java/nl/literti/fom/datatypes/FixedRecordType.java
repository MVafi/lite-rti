package nl.literti.fom.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.List;

import nl.literti.fom.FomDatatype;
import nl.literti.fom.FomDatatypeEnum;
import nl.literti.fom.datatypes.carriers.Field;

/**
 * This class contains metadata about a FOM Fixed Record data type.
 * <p/>
 * An fixed record type is a heterogeneous collections of types. Fixed record types contain named
 * fields that are of other types, allowing users to build "structures of data structures". 
 */
public class FixedRecordType implements FomDatatype, Serializable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 3112252018924L;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String      name;
	private List<Field> fields;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Constructor for a fixed record with an arbitrary number of fields
	 * 
	 * @param name the name of the fixed record type
	 * @param fields the ordered list of fields that this fixed record type will contain
	 */
	public FixedRecordType( String name, Field... fields )
	{
		this( name, Arrays.asList(fields) );
	}
	
	/**
	 * Constructor for a fixed record with an arbitrary number of fields
	 * 
	 * @param name the name of the fixed record type
	 * @param fields the ordered list of fields that this fixed record type will contain
	 */
	public FixedRecordType( String name, Collection<? extends Field> fields )
	{
		this.name     = name;
		this.fields   = new ArrayList<Field>( fields );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public List<Field> getFields()
	{
		return new ArrayList<Field>( this.fields );
	}
	
	@Override
	public boolean equals( Object other )
	{
		boolean equal = false;
		if( other instanceof FixedRecordType )
		{
			FixedRecordType asFixed = (FixedRecordType)other;
			equal = Objects.equals( this.name, asFixed.name ) &&
			        this.fields.equals( asFixed.fields );
		}
		
		return equal;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// IDatatype Interface ////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public FomDatatypeEnum getDatatypeEnum()
	{
		return FomDatatypeEnum.FIXEDRECORD;
	}
	
	@Override
	public FixedRecordType createUnlinkedClone()
	{
		List<Field> newFields = new ArrayList<Field>();
		for( Field field : this.fields )
		{
			Field newField = field.createUnlinkedClone();
			newFields.add( newField );
		}
		
		return new FixedRecordType( this.name, newFields );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
