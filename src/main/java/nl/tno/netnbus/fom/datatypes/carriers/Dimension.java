package nl.tno.netnbus.fom.datatypes.carriers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes a dimension of an {@link ArrayType}.
 */
public class Dimension implements Serializable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID  = 3112252018924L;
	public static final int CARDINALITY_DYNAMIC = -1; 
	public static final Dimension DYNAMIC       = new Dimension( CARDINALITY_DYNAMIC );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int lowerCardinality;
	private int upperCardinality;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Dimension( int cardinality )
	{
		this( cardinality, cardinality );
	}
	
	public Dimension( int lower, int upper )
	{
		if( lower != upper )
		{
			if( lower == CARDINALITY_DYNAMIC )
				throw new IllegalArgumentException( "cannot specify dynamic as a lower cardinality bound" );
			else if( upper == CARDINALITY_DYNAMIC )
				throw new IllegalArgumentException( "cannot specify dynamic as an upper cardinality bound" );
		}
			
		
		this.lowerCardinality = lower;
		this.upperCardinality = upper;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public int getCardinalityLowerBound()
	{
		return this.lowerCardinality;
	}
	
	public int getCardinalityUpperBound()
	{
		return this.upperCardinality;
	}
	
	public boolean isCardinalityDynamic()
	{
		return this.lowerCardinality == CARDINALITY_DYNAMIC;
	}
	
	@Override
	public boolean equals( Object other )
	{
		boolean equal = false;
		if( other instanceof Dimension )
		{
			Dimension asDimension = (Dimension)other;
			equal = this.lowerCardinality == asDimension.lowerCardinality &&
			        this.upperCardinality == asDimension.upperCardinality;
		}
		
		return equal;
	}
	

	@Override
	public String toString()
	{
		String descriptor = "cardinality=";
		
		descriptor += toFomString(this);
		
		return descriptor;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	
	public static String toFomString(Dimension dimension)
	{
		String descriptor = null;
		
		if( dimension.isCardinalityDynamic() )
			descriptor = "Dynamic";
		else if( dimension.lowerCardinality == dimension.upperCardinality )
			descriptor = String.valueOf( dimension.lowerCardinality);
		else
			descriptor = "(" + dimension.lowerCardinality + ".." + dimension.upperCardinality + ")";
		
		return descriptor;			
	}
	
	
	/**
	 * Builds a list of dimensions based on the cardinality value of an array datatype in the
	 * FOM. As per the standard the cardinality value:
	 * 
	 * <quote>
	 * shall contain the number of elements that are contained in the array datatype.
	 * Multidimensional arrays can be specified via a comma-separated list of values, each 
	 * representing one dimension. If the number of elements in the array varies during the use of 
	 * the datatype, a range of values may be provided; if used, this range shall take the form of 
	 * upper and lower bound values, separated by two periods and surrounded by brackets. 
	 * Alternatively, the keyword "Dynamic" may be entered into this column for these types of 
	 * arrays.
	 * </quote>
	 * 
	 * @param cardinality the cardinality value from the FOM file
	 * @return the list of dimensions described by the cardinality string
	 * @throws JConfigurationException if the cardinality value does not strictly follow the syntax
	 *                                 specified in the standard
	 */
	public static List<Dimension> fromFomString( String cardinality ) 
		throws Exception
	{
		List<Dimension> dimensions = new ArrayList<Dimension>();
		String[] tokens = cardinality.split( "," );
		for( String token : tokens )
		{
			String tokenTrimmed = token.trim();
			if( tokenTrimmed.equalsIgnoreCase("Dynamic") )
			{
				// Dynamic cardinality token
				dimensions.add( Dimension.DYNAMIC );
			}
			else if( (tokenTrimmed.startsWith( "(" ) && tokenTrimmed.endsWith(")")) ||
			         (tokenTrimmed.startsWith( "[" ) && tokenTrimmed.endsWith("]")) )
			{
				// Note for above: From my understanding the standard only says that parentheses
				// can be used for a cardinality range. The RPRv2 FOM uses square brackets however
				// so I've added that as an exception
				
				// Cardinality range
				String rangeString = tokenTrimmed.substring( 1, tokenTrimmed.length() - 1 );
				String[] rangeTokens = rangeString.split( "\\.\\." );
				if( rangeTokens.length != 2 )
					throw new Exception( "Cardinality range must contain a lower and an upper bound" );
				
				// Lower Bound
				String lowerBoundString = rangeTokens[0].trim();
				int lowerBound = 0;
				try
				{
					lowerBound = Integer.parseInt( lowerBoundString );
				}
				catch( NumberFormatException nfe )
				{
					// Not a number
					throw new Exception( "Lower cardinality bound contains non-numeric value: " + 
						                               lowerBoundString );
				}
				if( lowerBound < 0 )
				{
					// Negative number provided 
					throw new Exception( "Lower cardinality bound contains negative value: " + 
					                                   lowerBound );
				}
				
				// Upper Bound
				String upperBoundString = rangeTokens[1].trim();
				int upperBound = 0;
				try
				{
					upperBound = Integer.parseInt( upperBoundString );
				}
				catch( NumberFormatException nfe )
				{
					// Not a number
					throw new Exception( "Upper cardinality bound contains non-numeric value: " + 
					                                   upperBoundString );
				}
				
				if( upperBound < 0 )
				{
					// Negative number provided 
					throw new Exception( "Upper cardinality bound contains negative value: " + 
						                               upperBound );
				}
				
				if( lowerBound > upperBound )
				{
					// Lower must be the lowest!
					throw new Exception( "Upper cardinality value must be greater than lower cardinality value" );
				}
				
				dimensions.add( new Dimension(lowerBound, upperBound) );
			}
			else
			{
				try
				{
					int cardinalityValue = Integer.parseInt( tokenTrimmed );
					if( cardinalityValue < 0 )
					{
						// Negative number provided 
						throw new Exception( "Array cardinality contains negative value: " + 
						                                   cardinalityValue );
					}
					
					dimensions.add( new Dimension(cardinalityValue) );
				}
				catch( NumberFormatException nfe )
				{
					throw new Exception( "Array cardinality contains non-numeric value: " + 
					                                   tokenTrimmed );
				}
			}
		}
		
		return dimensions;
	}
}
