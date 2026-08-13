package nl.literti.fom.datatypes;

import java.io.Serializable;
import java.util.Objects;

import nl.literti.fom.FomDatatype;
import nl.literti.fom.FomDatatypeEnum;

/**
 * This class contains metadata about a FOM Simple data type.
 * 
 * A simple type describes a simple, scalar data item
 */
public class SimpleType implements FomDatatype, Serializable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 3112252018924L;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String    name;
	private FomDatatype representation;	// BasicType or UnresolvedDatatype only

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SimpleType( String name, FomDatatype representation )
	{
		this.name = name;
		this.setRepresentation( representation );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public FomDatatype getRepresentation()
	{
		return this.representation;
	}
	
	public void setRepresentation( FomDatatype representation )
	{
		// Simple types can only be representations of Basic types. We also have to allow 
		// placeholder references for deferred linking
		if( representation instanceof BasicType || representation instanceof UnresolvedDatatype )
			this.representation = representation;
		else
			throw new IllegalArgumentException( representation.getName() + " is not a Basic type" );
	}

	@Override
	public boolean equals( Object other )
	{
		boolean equal = false;
		
		if( other instanceof SimpleType )
		{
			SimpleType asSimple = (SimpleType)other;
			equal = Objects.equals( this.name, asSimple.name ) &&
			        Objects.equals( this.representation, asSimple.representation );
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
		return FomDatatypeEnum.SIMPLE;
	}
	
	@Override
	public SimpleType createUnlinkedClone()
	{
		return new SimpleType( this.name, new UnresolvedDatatype(this.representation) );
	}
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
