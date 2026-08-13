package nl.literti.fom.datatypes.carriers;

import nl.literti.fom.datatypes.BasicType;

/**
 * Describes the byte ordering of {@link BasicType} datatypes.
 */
public enum Endianness
{
	/**
	 * Least significant byte first
	 */
	LITTLE, 
	/**
	 * Most significant byte first
	 */
	BIG;
	
	@Override
	public String toString()
	{
		switch( this )
		{
			case LITTLE:
				return "Little";
			default:
			case BIG:
				return "Big";
		}
	}
	
	/**
	 * If the provided string matches (ignoring case) the name of either
	 * endianness type, that type is returned. Otherwise an exception is thrown
	 */
	public static Endianness fromFomString( String fomString ) throws Exception
	{
		if( fomString.equalsIgnoreCase("little") )
			return LITTLE;
		else if( fomString.equalsIgnoreCase("big") )
			return BIG;
		else
			throw new Exception( "Unsupported Endianness found: "+fomString );
	}
}
