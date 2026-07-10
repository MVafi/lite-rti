package nl.tno.netnbus.fom;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Object containing the FOM in deserialized form Repo supports static FOM only, meaning fixed at
 * federation creation and afterwards immutable TODO : Add Extension FOM support, meaning that the
 * FOM can be extended at runtime
 * Can be extended to support older hla version by looking for actions that involve "hlaobjectroot" or "HLAprivilegeToDeleteObject"
 */
public class FederationObjectModel implements Serializable {
  private static final long serialVersionUID = 98121116105109L;

  public static final int INVALID_HANDLE = -1;
  public static final int MAX_MOM_HANDLE = 1000; // The maximum handle value for MOM data

  private String filename;
  private int handle = MAX_MOM_HANDLE;
  // private HLAVersion version;
  // private boolean locked;
  private Map<String, FomDatatype> datatypes;
  private Map<Integer, FomObjectClass> oclasses;
  // private Map<Integer,ICMetadata> iclasses;
  // private Map<Integer,Space> spaces;
  private FomObjectClass ocroot;
  // private ICMetadata icroot;

  private int privilegeToDelete; // handle of the HLAprivilegeToDeleteObject attribute in the object root class

  // private Map<Integer,ICMetadata> iclasses;

  public FederationObjectModel() {
    this.datatypes = new HashMap<String, FomDatatype>();
    this.oclasses = new HashMap<Integer, FomObjectClass>();
    // this.iclasses = new HashMap<Integer,ICMetadata>();
    // this.spaces   = new HashMap<Integer,Space>();
    // this.locked   = false;
    this.ocroot = null; // Object root class
    // this.icroot   = null;
    // this.version  = HLAVersion.HLA13;

    this.privilegeToDelete = INVALID_HANDLE;
    // DatatypeHelpers.injectStandardDatatypes( this );
  }

  public void setFileName( String name )
	{
		this.filename = name;
	}
	
	public String getFileName()
	{
		return this.filename;
	}
	
	// public HLAVersion getHlaVersion()
	// {
	// 	return this.version;
	// }

  // ===== Datatype API =====

  public void addDatatype(FomDatatype datatype) {
    // Placeholder types are not allowed to be inserted directly into the ObjectModel

    // todo: this becomes an issue when merging FOM's during runtime
    // if (datatype instanceof UnresolvedDatatype) {
    //   throw new IllegalArgumentException("datatype is a placeholder");
    // }
    String name = datatype.getName().toLowerCase();
    this.datatypes.put(name, datatype);
  }

  public boolean containsDatatype(String name) {
    String nameLower = name.toLowerCase();
    return this.datatypes.containsKey(nameLower);
  }

  public FomDatatype getDatatype(String name) {
    String nameLower = name.toLowerCase();
    return this.datatypes.get(nameLower);
  }

  // getDatatypes()
  public Set<FomDatatype> getAllDatatypes() {
    return new HashSet<FomDatatype>(this.datatypes.values());
  }

  // ===== Spaces API =====

  // public Space getSpace(int spaceHandle) {
  //   return this.spaces.get(spaceHandle);
  // }

  // public Space getSpace( String name )
  // {
  // 	for( Space temp : this.spaces.values() )
  // 	{
  // 		if( temp.getName().equalsIgnoreCase(name) )
  // 			return temp;
  // 	}

  // 	// not found
  // 	return null;
  // }

  // public Collection<Space> getAllSpaces()
  // {
  // 	return this.spaces.values();
  // }

  // public void addSpace( Space space )
  // {
  // 	// make sure we're not locked
  // 	if( space == null || this.locked )
  // 	{
  // 		return;
  // 	}

  // 	// add it
  // 	this.spaces.put( space.getHandle(), space );
  // 	space.setModel( this );
  // }

  // ===== ObjectClass API =====

  public FomObjectClass getObjectClass(int handle) {
    return this.oclasses.get(handle);
  }

  public FomObjectClass getObjectClass(String name) {
    name = name.toLowerCase();                // names are meant to be case-insensitive
    name = name.substring(14);    // cut off any hla 1516e specific notations on the front //"hlaobjectroot.<name>"

    // Search with name in the local object class map
    for (FomObjectClass oc : this.oclasses.values()) {
      if (oc.getLocalName().equalsIgnoreCase(name)) {
        return oc;
      }
    }

    // Check for object root
    if (name.equalsIgnoreCase("hlaobjectroot"))
      return this.getObjectRoot();

    // Check for MOM object classes
    throw new IllegalArgumentException("Not implemented 2");
    // return this.getObjectClass(Mom.getMomObjectClassHandle(version, name));
  }

  public FomAttributeClass getAttributeClass(int classHandle, String attributeName) {

    FomObjectClass oc = this.oclasses.get(classHandle);
    if (oc == null) {
      return null;
    }

    // Check for mom attribute
    if (classHandle < MAX_MOM_HANDLE) {
      // make sure we aren't talking privilegeToDelete
      if (attributeName.equals("HLAprivilegeToDeleteObject")) {
        return this.ocroot.getAttribute(this.privilegeToDelete);
      }

      // it sure is, do a special lookup because of the requirement to map names
      // depending on the HLA version involved

      throw new IllegalArgumentException("Not implemented 3");
      // int aHandle = Mom.getMomAttributeHandle(version, classHandle, attributeName);
      // return oc.getAttribute(aHandle);
    } 
    
    // Check normal attributes
    int aHandle = oc.getAttributeHandle(attributeName);
    return oc.getAttribute(aHandle);
  }

  public FomObjectClass getObjectRoot() {
    return this.ocroot;
  }

  public void setObjectRoot(FomObjectClass root) {

    // 1516 and 1516e implementation
    if (root.getQualifiedName().equals("HLAobjectRoot")) {
      this.ocroot = root;
      // set the handle for privilegeToDelete
      this.privilegeToDelete = root.getAttributeHandle("HLAprivilegeToDeleteObject");
    }

    // If the new ocroot has no privilegeToDelete, then make sure it is added
    this.addPrivilegeToDeleteIfNotPresent();
  }

  public void addPrivilegeToDeleteIfNotPresent() {
    String name = "HLAprivilegeToDeleteObject";
    FomAttributeClass temp = ocroot.getAttribute(name);
    if (temp == null) {
      // 1516e implementation (this HLAprivilegeToDelete is NA in 1516 but HLAtoken in 1516e. This
      // method looks to only ever called for 1516e foms, so hardcoded for HLAtoken)
      temp = this.newAttribute(name, getDatatype("HLAtoken"));
      ocroot.addAttribute(temp);
      this.privilegeToDelete = temp.getHandle();
    }
  }

  public Set<FomObjectClass> getAllObjectClasses() {
    return new HashSet<FomObjectClass>(this.oclasses.values());
  }

  public void addObjectClass(FomObjectClass oc) {
    // // make sure we're not locked
    // if( oc == null || this.locked )
    // {
    // 	return;
    // }

    // add it
    this.oclasses.put(oc.getHandle(), oc);
    oc.setModel(this);
  }

  public FomObjectClass removeObjectClass(int handle) {
    FomObjectClass removed = this.oclasses.remove(handle);
    // set the model to null
    if (removed != null) {
      removed.setModel(null);
    }

    return removed;
  }

  public int getObjectClassHandle(String name) {
    FomObjectClass metadata = this.getObjectClass(name);
    if (metadata == null) {
      // unable to find
      return INVALID_HANDLE;
    } else {
      return metadata.getHandle();
    }
  }

  public String getObjectClassName(int handle) {
    if (this.oclasses.containsKey(handle)) {
      return this.oclasses.get(handle).getQualifiedName();
    } else {
      return null;
    }
  }

  public String findAttributeName(int attributeHandle) {
    // Go over all object classes
    for (FomObjectClass objectClass : this.oclasses.values()) {
      FomAttributeClass attributeClass = objectClass.getAttribute(attributeHandle);
      if (attributeClass != null) return attributeClass.getName();
    }

    return attributeHandle + " <unknown>";
  }

  public int getPrivilegeToDelete() {
    return this.privilegeToDelete;
  }

  // was getPrivileteToDeleteMetaClass
  public FomAttributeClass getPrivileteToDeleteAttributeClass() {
    return ocroot.getAttribute( this.privilegeToDelete );
  }

  // ===== InteractionClass API =====

  // ===== Dynamic FOM API =====

  // ===== Creation/Handle FOM API =====

  public FomObjectClass newObject(String name) {
    FomObjectClass object = new FomObjectClass(name, generateHandle());
    object.setModel(this);

    return object;
  }

  public FomAttributeClass newAttribute(String name, FomDatatype datatype) {
    return new FomAttributeClass(name, datatype, generateHandle());
  }

  // 	public ICMetadata newInteraction( String name )
  // {
  // 	ICMetadata interaction = new ICMetadata( name, generateHandle() );
  // 	interaction.setModel( this );

  // 	return interaction;
  // }

  // 	public PCMetadata newParameter( String name, IDatatype datatype )
  // {
  // 	return new PCMetadata( name, datatype, generateHandle() );
  // }

  // public Space newSpace( String name )
  // {
  // 	return new Space( name, generateHandle() );
  // }

  // public Dimension newDimension( String name )
  // {
  // 	return new Dimension( name, generateHandle() );
  // }

  protected synchronized int generateHandle() {
    return ++this.handle;
  }

  public String toString() {
    throw new IllegalArgumentException("Not implemented 4");
    // return new StringRenderer().renderFOM( this );
  }

  public String toXmlDocument() {
    throw new IllegalArgumentException("Not implemented 5");
  }

  public static void mommify(FederationObjectModel model) {
    throw new IllegalArgumentException("Not implemented 6 ");
  }

  public static void resolveSymbols(FederationObjectModel model) {
    throw new IllegalArgumentException("Not implemented 7");
  }
}
