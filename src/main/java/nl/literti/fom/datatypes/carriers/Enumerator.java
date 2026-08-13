package nl.literti.fom.datatypes.carriers;

import java.io.Serializable;

import nl.literti.fom.FomEnumerator;

public class Enumerator implements FomEnumerator, Serializable, Comparable<Enumerator>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	/**
	 * Special <code>HLAother</code> value that acts as a wild card for Variant Record alternatives 
	 */
	public static Enumerator HLA_OTHER = new Enumerator( "HLAother", Long.MAX_VALUE );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String name;
	private Number value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Enumerator( String name, Number value )
	{
		this.name = name;
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public String toString()
	{
		return this.name;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// IEnumerator Interface ////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return this.name;
	}
	
	public Number getValue()
	{
		return this.value;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////// Comparable Interface ////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int compareTo( Enumerator other )
	{
		long thisValue = this.value.longValue();
		long otherValue = other.value.longValue();
		
		if( thisValue == otherValue )
			return 0;
		else if( thisValue > otherValue )
			return 1;
		else 
			return -1;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}