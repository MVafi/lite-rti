package nl.tno.netnbus.fom.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import nl.tno.netnbus.fom.FederationObjectModel;
import nl.tno.netnbus.fom.FomAttributeClass;
import nl.tno.netnbus.fom.FomDatatype;
import nl.tno.netnbus.fom.FomObjectClass;
import nl.tno.netnbus.fom.datatypes.UnresolvedDatatype;
import nl.tno.netnbus.fom.enums.OrderEnum;
import nl.tno.netnbus.fom.enums.SharingEnum;
import nl.tno.netnbus.fom.enums.TransportEnum;

public class ParserHla1516e {

	private static final ParserWrapper PARSER = new ParserWrapper();
	private FederationObjectModel fom;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ParserHla1516e()
	{
		this.fom = new FederationObjectModel();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * The main FOM parser processing method. This takes the root element of a FOM document and
	 * parses it into an {@link ObjectModel} that is returned. If the parser is malformatted in
	 * any way, a {@link JConfigurationException} is thrown.
	 * <p/>
	 * <b>Note:</b> Datatypes, Attributes and Parameters in the returned FOM will contain 
	 * placeholder symbols as the datatypes that they refer to may be declared in another FOM 
	 * module. Once all modules have been merged into a combined FOM, call 
	 * {@link ObjectModel#resolveSymbols(ObjectModel)} to resolve all placeholder datatypes to their
	 * concrete representation
	 */
	public FederationObjectModel process( Element element ) throws Exception
	{
		// locate the major elements we are interested in
		Element datatypesElement = null;
		Element objectsElement = null;
		Element interactionsElement = null;
		Element dimensionsElement = null;
		for( Element temp : FedHelpers.getChildElements(element) )
		{
			String tagName = temp.getTagName();
			if( tagName.equals("dataTypes") )
				datatypesElement = temp;
			else if( tagName.equals("objects") )
				objectsElement = temp;
			else if( tagName.equals("interactions") )
				interactionsElement = temp;
			else if( tagName.equals("dimensions") )
				dimensionsElement = temp;
			else
				continue; // ignore
		}
		
		// extract all datatypes (this must be done first so that we can reference the types when we extract objects and interactions)
		if( datatypesElement != null )
			this.extractDatatypes( datatypesElement );
		
		// extract all the object classes
		FomObjectClass objectRoot = null;
		if( objectsElement != null )
		{
			objectRoot = this.extractObjects( objectsElement );
		}
		else
		{
			objectRoot = this.fom.newObject( "HLAobjectRoot" );
			this.fom.addObjectClass( objectRoot );
		}
		this.fom.setObjectRoot( objectRoot);
		
		// extract all the interaction classes
		// TODO : To be implemented
		// ICMetadata interactionRoot = null;
		// if( interactionsElement != null )
		// {
		// 	interactionRoot = this.extractInteractions( interactionsElement );
		// }
		// else
		// {
		// 	interactionRoot = this.fom.newInteraction( "HLAinteractionRoot" );
		// 	this.fom.addInteractionClass( interactionRoot );
		// }
		// this.fom.setInteractionRoot( interactionRoot );

		// return the completed FOM
		return this.fom;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// Datatype Methods /////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void extractDatatypes( Element datatypesElement ) throws Exception
	{
		Set<FomDatatype> fedTypes = null;
		try
		{
			fedTypes = FedHelpers.extractDatatypes( datatypesElement );
		}
		catch( Exception jce )
		{
			// rethrow as JErrorReadingFED
			throw new Exception( jce );
		}
		
		// Add types
		for( FomDatatype fedType : fedTypes )
		{
			// We used to link datatypes here on the assumption that FOM modules were 
			// self-contained. However it appears that is not the reality, and that modules can
			// reference datatypes that are only declared in other modules.
			//
			// As such we datatypes regardless of whether they contain placeholder symbols, and
			// resolve them once all modules have been merged
			fom.addDatatype( fedType );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// Object Class Methods ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loop through all the child objects and generate the metadata hierarchy to represent them.
	 * <p/>
	 * The general format of the object element is as follows:
	 * <pre>
     * <objects>
     *    <objectClass>
     *       <name>HLAobjectRoot</name>
     *       <attribute>
     *          <name>name</name>
     *          <transportation>HLAreliable|HLAbestEffort</transportation>
     *          <order>TimeStamp|Receive</order>
     *       </attribute>
     *       ...
     *       <objectClass>
     *          <name>HLAmanager</name>
     *          <attribute/>...
     *       </objectClass>
     *    </objectClass>
     * </objects>
	 * </pre>
	 */
	private FomObjectClass extractObjects( Element objectsElement ) throws Exception
	{
		Element objectRootElement = FedHelpers.getFirstChildElement( objectsElement, 
		                                                             "objectClass" );
		if( objectRootElement == null )
		{
			// no objects to process... OK, must be an extension module
			return null;
		}
		
		// validate that we have an object root
		String name = FedHelpers.getChildValue( objectRootElement, "name" );
		if( name.equals("HLAobjectRoot") == false )
		{
			throw new Exception( "First <objectClass> must be HLAobjectRoot, found: "+name );
		}
		
		FomObjectClass objectRoot = fom.newObject( "HLAobjectRoot" );
		extractAttributes( objectRoot, objectRootElement );
		fom.addObjectClass( objectRoot );

		// recurse and find all our children
		extractObjects( objectRoot, objectRootElement );
		return objectRoot;
	}

	private void extractObjects( FomObjectClass parent, Element parentElement ) throws Exception
	{
		List<Element> children = FedHelpers.getAllChildElements( parentElement, "objectClass" );
		for( Element current : children )
		{
			String objectClassName = FedHelpers.getChildValue( current, "name" );
			
			FomObjectClass objectClass = fom.newObject( objectClassName );

			// get the sharing policy
			String objectClassSharing = FedHelpers.getChildValueForgiving( current, 
			                                                               "sharing", objectClassName );
			if( objectClassSharing != null )
				objectClass.setSharing( SharingEnum.fromFomString(objectClassSharing) );
			
			// link us to our parent
			objectClass.setParent( parent );
			extractAttributes( objectClass, current );
			
			fom.addObjectClass( objectClass );
			// recurse and find all our children
			extractObjects( objectClass, current );
		}
	}
	
	/**
	 * This method will extract all the relevant object class attributes (not XML attributes)
	 * from the given "objectClass" element. For each attribute, an {@link ACMetadata} will be
	 * created and stored inside the provided {@link FomObjectClass}.
	 */
	private void extractAttributes( FomObjectClass clazz, Element element ) throws Exception
	{
		FederationObjectModel theModel = clazz.getModel();
		List<Element> attributes = FedHelpers.getAllChildElements( element, "attribute" );
		for( Element attributeElement : attributes )
		{
			String attributeName = FedHelpers.getChildValue( attributeElement, "name" );
			
			// All attribute datatypes are initially created as placeholders, and resolved once
			// all FOM modules have been merged and the standard MIM has been inserted
			String datatypeName = FedHelpers.getChildValue( attributeElement, "dataType" );
			FomDatatype datatype = new UnresolvedDatatype( datatypeName );
			
			FomAttributeClass attribute = fom.newAttribute( attributeName, datatype );

			// Order and Transport
			String attributeOrder = FedHelpers.getChildValueForgiving( attributeElement, 
			                                                           "order", 
			                                                           attributeName );
			if( attributeOrder != null )
				attribute.setOrder( OrderEnum.fromFomString(attributeOrder) );

			String attributeTransport = FedHelpers.getChildValueForgiving( attributeElement, 
			                                                               "transportation", 
			                                                               attributeName );
			if( attributeTransport != null )
				attribute.setTransport( TransportEnum.fromFomString(attributeTransport) );

			// get the sharing policy
			String attributeSharing = FedHelpers.getChildValueForgiving( attributeElement, 
			                                                               "sharing",
			                                                               attributeName );
			if( attributeSharing != null )
				attribute.setSharing( SharingEnum.fromFomString(attributeSharing) );
			
			// add the attribute to the containing class
			clazz.addAttribute( attribute );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// Interaction Class Methods ////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loop through all the child objects and generate the metadata hierarchy to represent them.
	 * <p/>
	 * The general format of the object element is as follows:
	 * <pre>
     * <interactions>
     *    <interactionClass>
     *       <name>HLAinteractionRoot</name>
     *       <transportation>HLAreliable|HLAbestEffort</transportation>
     *       <order>TimeStamp|Receive</order>
     *       <parameter>
     *          <name>name</name>
     *       </parameter>
     *       ...
     *       <interactionClass/>...
     *    </interactionClass>
     * </interactions>
	 * </pre>
	 */
	// private ICMetadata extractInteractions( Element element ) throws JErrorReadingFED
	// {
	// 	Element interactionRootElement = FedHelpers.getFirstChildElement( element, 
	// 	                                                                  "interactionClass" );
	// 	if( interactionRootElement == null )
	// 	{
	// 		// no interactions to process... OK, must be an extension module
	// 		return null;
	// 	}

	// 	// validate that we have an interaction root
	// 	String name = FedHelpers.getChildValue( interactionRootElement, "name" );
	// 	if( name.equals("HLAinteractionRoot") == false )
	// 	{
	// 		throw new JErrorReadingFED( "First <interactionClass> must be "+
	// 		                            "HLAinteractionRoot, found: "+name );
	// 	}
		
	// 	// generate some basic information for the Interaction root
	// 	ICMetadata interactionRoot = fom.newInteraction("HLAinteractionRoot" );

	// 	// get the transport and order
	// 	String interactionOrder = FedHelpers.getChildValueForgiving( interactionRootElement, "order", name );
	// 	if( interactionOrder != null )
	// 		interactionRoot.setOrder( Order.fromFomString(interactionOrder) );

	// 	String interactionTransport = FedHelpers.getChildValueForgiving( interactionRootElement, 
	// 	                                                                 "transportation", 
	// 	                                                                 name );
	// 	if( interactionTransport != null )
	// 		interactionRoot.setTransport( Transport.fromFomString(interactionTransport) );

	// 	// get the sharing policy
	// 	String interactionSharing = FedHelpers.getChildValueForgiving( interactionRootElement, "sharing", name );
	// 	if( interactionSharing != null )
	// 		interactionRoot.setSharing( Sharing.fromFomString(interactionSharing) );

	// 	// get the parameters
	// 	extractParameters( interactionRoot, interactionRootElement );
	// 	fom.addInteractionClass( interactionRoot );

	// 	// recurse and find all our children
	// 	extractInteractions( interactionRoot, interactionRootElement );
	// 	return interactionRoot;
	// }

	// private void extractInteractions( ICMetadata parent, Element parentElement )
	// 	throws JErrorReadingFED
	// {
	// 	List<Element> children = FedHelpers.getAllChildElements( parentElement, 
	// 	                                                         "interactionClass" );
	// 	for( Element current : children )
	// 	{
	// 		// create the metadata type
	// 		String interactionClassName = FedHelpers.getChildValue( current, "name" );
	// 		ICMetadata interactionClass = fom.newInteraction( interactionClassName );

	// 		// get the transport and order
	// 		String interactionOrder = FedHelpers.getChildValueForgiving( current, 
	// 		                                                             "order", 
	// 		                                                             interactionClassName );
	// 		if( interactionOrder != null )
	// 			interactionClass.setOrder( Order.fromFomString(interactionOrder) );

	// 		String interactionTransport = FedHelpers.getChildValueForgiving( current, 
	// 		                                                                 "transportation", 
	// 		                                                                 interactionClassName );
	// 		if( interactionTransport != null )
	// 			interactionClass.setTransport( Transport.fromFomString(interactionTransport) );

	// 		// get all the interaction parameters
	// 		extractParameters( interactionClass, current );
			
	// 		// link us to our parent
	// 		interactionClass.setParent( parent );
	// 		fom.addInteractionClass( interactionClass );
			
	// 		// recurse and find all our children
	// 		extractInteractions( interactionClass, current );
	// 	}
	// }
	
	// /**
	//  * This method will extract all the relevant interaction class parameters from the given
	//  * "interactionClass" element. For each parameter, a {@link PCMetadata} will be created
	//  * and stored inside the provided {@link ICMetadata}.
	//  */
	// private void extractParameters( ICMetadata clazz, Element element ) throws JErrorReadingFED
	// {
	// 	ObjectModel theModel = clazz.getModel();
	// 	List<Element> parameters = FedHelpers.getAllChildElements( element, "parameter" );
	// 	for( Element parameterElement : parameters )
	// 	{
	// 		String parameterName = FedHelpers.getChildValue( parameterElement, "name" );
			
	// 		// All parameter datatypes are initially created as placeholders, and resolved once
	// 		// all FOM modules have been merged and the standard MIM has been inserted
	// 		String datatypeName = FedHelpers.getChildValue( parameterElement, "dataType" );
	// 		IDatatype datatype = new UnresolvedDatatype( datatypeName );
			
	// 		PCMetadata parameter = fom.newParameter( parameterName, datatype );
	// 		clazz.addParameter( parameter );
	// 	}
	// }

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// Private Helper Methods //////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * This method will take the given 1516e XML FOM and attempt to process it.
	 * If there is a problem locating the URL or fetching the stream with which to read it, a
	 * {@link CouldNotOpenFDD} exception will be thrown. If there is a problem reading the FOM,
	 * or its structure is not correct, an {@link ErrorReadingFDD} exception will be thrown.
	 * <p/>
	 * If the FOM is correct and can be processed successfully, it will be turned into an
	 * {@link ObjectModel} and returned.
	 * 
	 * @param fed The URL location of the fed file (could be local or remote)
	 * @return An {@link ObjectModel} representing the FOM from the given location
	 * @throws CouldNotOpenFDD If the fed file can not be located or there is an error opening it
	 * @throws ErrorReadingFDD If the fed file is invalid or there is a problem reading it from the
	 * stream.
	 */
	public static FederationObjectModel parseFOM( URL fed ) throws Exception
	{
		////////////////////////////////////////
		// parse the fed file into a DOM-tree //
		////////////////////////////////////////
		if( fed == null )
			throw new Exception( "Can't locate fed file: " + fed );

		// try to open a stream to the given URL
		Element rootElement = null;
		try
		{
			// open the file
			InputStream stream = fed.openStream();
			// parse the thing in
			Document document = PARSER.parse( stream );
			document.normalize();
			rootElement = document.getDocumentElement();
			// close off the stream
			stream.close();
		}
		catch( IOException ioex )
		{
			throw new Exception( "Error opening fed file: "+ioex.getMessage(), ioex );
		}
		catch( Exception e )
		{
			throw new Exception( "Error reading fed file: " + e.getMessage(), e );
		}
		
		/////////////////////
		// process the FOM //
		/////////////////////
		// create the parser
		ParserHla1516e parser = new ParserHla1516e();
		// we'll call the node handler directly because we want to process the root node
		FederationObjectModel model = parser.process( rootElement );
		model.setFileName( fed.toString() );
		return model;
		// catch( NullPointerException npe )
		// {
		// 	throw new Exception( "Problem while reading fed file", npe );
		// }
	}
}
