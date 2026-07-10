package nl.tno.netnbus.fom.datatypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tno.netnbus.fom.FomAttributeClass;
import nl.tno.netnbus.fom.FomDatatype;
import nl.tno.netnbus.fom.FomEnumerator;
import nl.tno.netnbus.fom.datatypes.carriers.Alternative;
import nl.tno.netnbus.fom.datatypes.carriers.Enumerator;
import nl.tno.netnbus.fom.datatypes.carriers.Field;


public class DatatypeLinker {
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Map<String,FomDatatype> lookup;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DatatypeLinker()
	{
		this.lookup = new HashMap<String,FomDatatype>(); 
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Returns the complete representation of the specified datatype.
	 * <p/>
	 * If the <code>type</code> parameter is a {@link DatatypePlaceholder} then its complete 
	 * representation is resolved and returned. 
	 * <p/>
	 * In all other cases the <code>type</code> parameter is returned as it already represents
	 * the complete type.
	 *  
	 * @param type the {@link IDatatype} to resolve
	 * @return the complete representation of the specified datatype
	 * @throws LinkerException if the <code>type</code> parameter could not be resolved to a
	 *                         complete datatype
	 */
	private FomDatatype resolve( FomDatatype type ) throws Exception
	{
		FomDatatype actual = null;
		
		if( type instanceof UnresolvedDatatype )
		{
			String name = type.getName().toLowerCase();
			FomDatatype candidate = lookup.get( name );
		
			if( candidate != null )
				actual = candidate;
			else
				throw new Exception( "Undefined datatype: " + type.getName() );
		}
		else
		{
			actual = type;
		}
		
		return actual;
	}
	
	/**
	 * Performs the same function as {@link #resolve(FomDatatype)} but ensures that the resolved
	 * datatype is a {@link BasicType}.
	 * @throws LinkerException if the <code>type</code> parameter could not be resolved to a
	 *                         complete datatype or does not represent a BasicType
	 */
	private BasicType resolveBasic( FomDatatype type ) throws Exception
	{
		FomDatatype actual = resolve( type );
		if( actual instanceof BasicType )
			return (BasicType)actual;
		else
			throw new Exception( type.getName() + " is not a Basic type" );
	}
	
	/**
	 * Performs the same function as {@link #resolve(FomDatatype)} but ensures that the resolved
	 * datatype is a {@link BasicType}.
	 * @throws LinkerException if the <code>type</code> parameter could not be resolved to a
	 *                         complete datatype or does not represent a BasicType
	 */
	private EnumeratedType resolveEnumerated( FomDatatype type ) throws Exception
	{
		FomDatatype actual = resolve( type );
		if( actual instanceof EnumeratedType )
			return (EnumeratedType)actual;
		else
			throw new Exception( type.getName() + " is not an Enumerated type" );
	}
	
	/**
	 * Fetches a named enumerator value from the specified {@link EnumeratedType}.
	 * @throws Exception if the {@link EnumeratedType} contains no enumerator value named
	 *                         <code>enumeratorName</code>
	 */
	private Enumerator getEnumeratorValue( EnumeratedType type, String enumeratorName )
		throws Exception
	{
		// Alternative entries in Variant Records can specify HLA_OTHER as a wildcard
		if( enumeratorName.equals(Enumerator.HLA_OTHER.getName()) )
			return Enumerator.HLA_OTHER;
		
		try
		{
			return type.valueOf( enumeratorName );
		}
		catch( IllegalArgumentException iae )
		{
			throw new Exception( "invalid enumerator value: " + 
			                           enumeratorName );
		}
	}
	
	/**
	 * Adds {@link IDatatype} candidates to the pool of complete representations that a
	 * {@link DatatypePlaceholder} can be resolved to
	 */
	public void addCandidates( Collection<? extends FomDatatype> candidates )
	{
		// Add all candidates from the candidate set
		for( FomDatatype candidate : candidates )
		{
			String name = candidate.getName().toLowerCase();
			if( !this.lookup.containsKey(name) )
				this.lookup.put( name, candidate );
		}
	}
	
	/**
	 * Resolves any {@link DatatypePlaceholder} references within a datatype to their complete
	 * representations.
	 * <p/>
	 * If the <code>type</code> parameter does not contain any {@link DatatypePlaceholder} references
	 * then no action is performed on the type.
	 * 
	 * @param type The type to link
	 * @throws LinkerException if the <code>type</code> contains {@link DatatypePlaceholder} references
	 *                         that can not be resolved
	 */
	public void linkType( FomDatatype type ) throws Exception
	{
		switch( type.getDatatypeEnum() )
		{
			case SIMPLE:
			{
				SimpleType asSimple = (SimpleType)type;
				BasicType representation = resolveBasic( asSimple.getRepresentation() );
				asSimple.setRepresentation( representation );
				break;
			}
			case ENUMERATED:
			{
				EnumeratedType asEnumerated = (EnumeratedType)type;
				BasicType representation = resolveBasic( asEnumerated.getRepresentation() );
				asEnumerated.setRepresentation( representation );
				break;
			}
			case ARRAY:
			{
				ArrayType asArray = (ArrayType)type;
				FomDatatype datatype = resolve( asArray.getDatatype() );
				asArray.setDatatype( datatype );
				break;
			}
			case FIXEDRECORD:
			{
				FixedRecordType asFixedRecordType = (FixedRecordType)type;
				for( Field field : asFixedRecordType.getFields() )
				{
					FomDatatype datatype = resolve( field.getDatatype() );
					field.setDatatype( datatype );
				}
				break;
			}
			case VARIANTRECORD:
			{
				VariantRecordType asVariantType = (VariantRecordType)type;
				EnumeratedType datatype = resolveEnumerated( asVariantType.getDiscriminantDatatype() );
				asVariantType.setDiscriminantDatatype( datatype );
				
				Set<Alternative> alternatives = asVariantType.getAlternatives();
				for( Alternative alternative : alternatives )
				{
					FomDatatype alternativeDatatype = this.resolve( alternative.getDatatype() );
					alternative.setDatatype( alternativeDatatype );
					
					// The placeholder enumerator may contain a range of enumerators. As we didn't 
					// know the full value set at parse time, we have to expand out any ranges we 
					// come across now at link time
					Set<FomEnumerator> incomingEnums = alternative.getEnumerators();
					Set<FomEnumerator> linkedEnums = new HashSet<FomEnumerator>();
					for( FomEnumerator enumerator : incomingEnums )
					{
						String incomingName = enumerator.getName().trim();
						if( incomingName.startsWith("[") && 
							incomingName.endsWith("]") && 
							incomingName.contains("..") )
						{
							// Placeholder is a range of enumerator constants
							String rangeString = incomingName.substring( 1, incomingName.length() - 1 );
							String[] rangeTokens = rangeString.split( "\\.\\.", 2 );
							if( rangeTokens.length != 2 )
								throw new Exception( "Enumerator range must contain a lower and an upper bound" );
							
							Enumerator lower = getEnumeratorValue( datatype, 
							                                       rangeTokens[0].trim() );
							Enumerator upper = getEnumeratorValue( datatype, 
							                                       rangeTokens[1].trim() );
							
							List<Enumerator> enumerators = datatype.getEnumerators();
							linkedEnums.add( lower );
							boolean inRange = false;
							for( int i = 0 ; i < enumerators.size() ; ++i )
							{
								Enumerator enumeratorAtI = enumerators.get( i );
								if( enumeratorAtI == upper )
									inRange = false;
							
								if( inRange )
									linkedEnums.add( enumeratorAtI );
								
								if( enumeratorAtI == lower )
									inRange = true;
							}
							linkedEnums.add( upper );
						}
						else
						{
							// Placeholder is just the one enumerator
							linkedEnums.add( getEnumeratorValue(datatype, incomingName) );
						}
					}
					
					alternative.setEnumerators( linkedEnums );
				}
				
				break;
			}
			case BASIC:
			default:
			{
				// No action required
				break;
			}
		}
	}
	
	public void linkAttribute( FomAttributeClass attribute ) throws Exception
	{
		FomDatatype datatype = attribute.getDatatype(); 
		if( datatype instanceof UnresolvedDatatype )
		{
			try
			{
				FomDatatype resolved = resolve( datatype );
				attribute.setDatatype( resolved );
			}
			catch( Exception e )
			{
				// Rethrow the exception, but prefix the message with the attribute name
				throw new Exception( "Attribute " + attribute.getName() + " " + e.getMessage(), 
				                     e );
			}
		}
	}
	
	// public void linkParameter( PCMetadata parameter ) throws Exception
	// {
	// 	IDatatype datatype = parameter.getDatatype();
	// 	if( datatype instanceof DatatypePlaceholder )
	// 	{
	// 		try
	// 		{
	// 			IDatatype resolved = resolve( datatype );
	// 			parameter.setDatatype( resolved );
	// 		}
	// 		catch( Exception e )
	// 		{
	// 			// Rethrow the exception, but prefix the message with the parameter name
	// 			throw new Exception( "Parameter " + parameter.getName() + " " + e.getMessage(), 
	// 			                     e );
	// 		}
	// 	}
	// }
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}

