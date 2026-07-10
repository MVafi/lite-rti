package nl.tno.netnbus.fom.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import nl.tno.netnbus.fom.FomDatatype;

import java.util.List;

import nl.tno.netnbus.fom.FomDatatypeEnum;
import nl.tno.netnbus.fom.datatypes.carriers.Enumerator;

/**
 * This class contains metadata about a FOM Enumerated data type.
 * <p/>
 * An enumerated type represents a data element that can take on a finite discrete set of possible 
 * values
 */
public class EnumeratedType implements FomDatatype, Serializable
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
	private List<Enumerator> enumerators;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EnumeratedType( String name, FomDatatype representation, String... enumerators )
	{
		this( name, representation, createEnumeratorsFromNames(enumerators) );
	}
	
	public EnumeratedType( String name, FomDatatype representation, Enumerator... enumerators )
	{
		this( name, representation, Arrays.asList(enumerators) );
	}
	
	public EnumeratedType( String name, 
	                       FomDatatype representation, 
	                       Collection<? extends Enumerator> enumerators )
	{
		this.name        = name;
		this.enumerators = new ArrayList<Enumerator>( enumerators );
		this.setRepresentation( representation );
		
		// Order enumerators by value at creation time.
		this.enumerators.sort( null );
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
		// Enumerated types can only be representations of Basic types. We also have to allow 
		// placeholder references for deferred linking
		if( representation instanceof BasicType || representation instanceof UnresolvedDatatype )
			this.representation = representation;
		else
			throw new IllegalArgumentException( representation.getName() + " is not a Basic type" );
	}
	
	public List<Enumerator> getEnumerators()
	{
		return new ArrayList<Enumerator>( this.enumerators );
	}
	
	public Enumerator valueOf( String name )
	{
		Enumerator result = null;
		
		for( Enumerator enumerator : this.enumerators )
		{
			if( enumerator.getName().equals(name.trim()) )
			{
				result = enumerator;
				break;
			}
		}
		
		if( result != null )
			return result;
		else
			throw new IllegalArgumentException( "no enumerator named " + name );
	}
	
	@Override
	public boolean equals( Object other )
	{
		boolean equal = false;
		
		if( other instanceof EnumeratedType )
		{
			EnumeratedType asEnumerated = (EnumeratedType)other;
			equal = Objects.equals( this.name, asEnumerated.name ) &&
			        Objects.equals( this.representation, asEnumerated.representation );
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
		return FomDatatypeEnum.ENUMERATED;
	}
	

	@Override
	public EnumeratedType createUnlinkedClone()
	{
		List<Enumerator> newEnumerators = new ArrayList<Enumerator>( this.enumerators.size() );
		for( Enumerator enumerator : this.enumerators )
		{
			Enumerator copy = new Enumerator( enumerator.getName(), enumerator.getValue() );
			newEnumerators.add( copy );
		}
		
		return new EnumeratedType( this.name, 
		                           new UnresolvedDatatype(this.representation), 
		                           newEnumerators );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	private static List<Enumerator> createEnumeratorsFromNames( String[] constants )
	{
		List<Enumerator> enumerators = new ArrayList<Enumerator>( constants.length );
		for( int i = 0 ; i < constants.length ; ++i )
		{
			if( constants[i] == null )
				throw new NullPointerException();
			
			enumerators.add( new Enumerator(constants[i], i) );
		}
		
		return enumerators;
	}
}
