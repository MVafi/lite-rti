package nl.literti.fom.parser;

import java.net.URL;

import nl.literti.fom.FederationObjectModel;

public class FomParser {
    
	private static final FomParser INSTANCE = new FomParser();
	// private Logger logger;

	private FomParser()
	{
		// this.logger = LogManager.getFormatterLogger( "portico.lrc.fom" );
	}

	private FederationObjectModel parseFom( URL fedLocation ) 
		throws Exception
	{
		// if its a 1516-2010 (Evolved) FOM
		return ParserHla1516e.parseFOM( fedLocation );
	}

	// private InputStream openStream( URL location ) throws Exception 
	// {
	// 	// make sure the file exists
	// 	if( location == null )
	// 		throw new Exception( "Fom file doesn't exist: file="+null );

	// 	try
	// 	{
	// 		return location.openStream();
	// 	}
	// 	catch( IOException ioex )
	// 	{
	// 		throw new Exception( "Error opening fom file from ["+location+
	// 		                            "]: "+ioex.getMessage(), ioex );
	// 	}
	// }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * This method will check what format the fed file is in and then will parse it into an
	 * {@link FederationObjectModel} or throw an exception if there is a problem. This will work with
	 * either IEEE1516-2000 XML-based files or IEEE1516e-2010 XML-based files. 
	 * Thus, both types are supported through either interface.
	 * 
	 * @param fedLocation The location of the FED file to parse.
	 * @return An {@link FederationObjectModel} instance containing the parsed contents of the given URL
	 * @throws Exception Could not open or read the FED file at the provided URL
	 */
	public static FederationObjectModel parse( URL fedLocation )
		throws Exception
	{
		return new FomParser().parseFom( fedLocation );
	}
}
