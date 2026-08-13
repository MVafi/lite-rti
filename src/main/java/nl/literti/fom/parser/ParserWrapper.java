package nl.literti.fom.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ParserWrapper
{
	private DocumentBuilderFactory factory;
	
	public ParserWrapper()
	{
		this.factory = DocumentBuilderFactory.newInstance();
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * This method takes the given file and attempts to parse it into a DOM document. If
	 * successful, the document is returned. This method uses the standard JAXP API and
	 * configuration of the parser can be maanged through the standard mechanisms.
	 *
	 * @param file The file containing the XML data
	 * @return A DOM document representing the contents of the file
	 * @throws IOException If there is a problem reading from the file
	 * @throws SAXException If there is a problem with the contained XML
	 * @throws ParserConfigurationException If there is a problem configuring the parser
	 */
	public Document parse( File file )
		throws IOException, SAXException, ParserConfigurationException
	{
		// get the builder
		DocumentBuilder builder = factory.newDocumentBuilder();
		// parse the file
		return builder.parse( file );
	}
	
	/**
	 * This method takes the given stream and attempts to parse it into a DOM document. If
	 * successful, the document is returned (and the stream closed). This method uses the standard
	 * JAXP API and configuration of the parser can be managed through the standard mechanisms.
	 *
	 * @param stream The stream containing the XML contents
	 * @return A DOM document representing the contents of the stream
	 * @throws IOException If there is a problem reading from the stream
	 * @throws SAXException If there is a problem with the contained XML
	 * @throws ParserConfigurationException If there is a problem configuring the parser
	 */
	public Document parse( InputStream stream )
		throws IOException, SAXException, ParserConfigurationException
	{
		// get the builder
		DocumentBuilder builder = factory.newDocumentBuilder();
		// parse the stream
		Document document = builder.parse( stream );
		
		// make sure the stream is closed
		try
		{
			stream.close();
		}
		catch( Exception e )
		{
			// ignore
		}
		
		// return the document
		return document;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}
