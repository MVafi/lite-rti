package nl.tno.netnbus.fom.datatypes;

import java.io.Serializable;
import java.util.Objects;

import nl.tno.netnbus.fom.FomDatatype;
import nl.tno.netnbus.fom.FomDatatypeEnum;
import nl.tno.netnbus.fom.datatypes.carriers.Endianness;

/**
 * This class contains metadata about a FOM Basic data type.
 * <p/>
 * Basic data types represent primitive data types in the FOM and are often the building blocks
 * of more complex data types.
 */
public class BasicType implements FomDatatype, Serializable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 3112252018924L;
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String     name;
	private int        size;
	private Endianness endianness;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	/**
	 * Constructor for BasicType with specified name, size and endianness
	 * 
	 * @param name the name of this data type
	 * @param size the size of this data type in bits
	 * @param endianness the byte ordering of this data type
	 */
	public BasicType( String name, int size, Endianness endianness )
	{
		this.name       = name;
		this.size       = size;
		this.endianness = endianness;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int getSize()
	{
		return this.size;
	}
	
	public Endianness getEndianness()
	{
		return this.endianness;
	}
	
	@Override
	public boolean equals( Object other )
	{
		boolean equal = false;
		
		if( other instanceof BasicType )
		{
			BasicType asBasic = (BasicType)other;
			equal = Objects.equals( this.name, asBasic.name ) &&
			        this.size == asBasic.size &&
			        this.endianness == asBasic.endianness;
		}
		
		return equal;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// FomDatatype Interface /////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getName()
	{
		return this.name;
	}
	
    @Override
	public FomDatatypeEnum getDatatypeEnum()
	{
		return FomDatatypeEnum.BASIC;
	}

    @Override
	public BasicType createUnlinkedClone()
	{
		return new BasicType( this.name, this.size, this.endianness );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
