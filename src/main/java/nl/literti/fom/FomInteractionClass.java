package nl.literti.fom;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.literti.fom.enums.OrderEnum;
import nl.literti.fom.enums.SharingEnum;
import nl.literti.fom.enums.TransportEnum;

// todo: revert to the old method names as in portico to prevent confusion
public class FomInteractionClass implements Serializable {

  private String name; // local name of the object class, e.g. "MoveToLocation"
  private String
      qualifiedName; // fully qualified name, including parent names, e.g.
                     // "hlainteractionroot.SMC_EntityControl.Task.MoveToLocation"

	private int                     handle;
	private OrderEnum                   order;
	private TransportEnum               transport;
	private SharingEnum 			          sharing;
	// private Space                   space;
	private FomInteractionClass              parent;
	private Set<FomInteractionClass>         children;
	private Map<Integer,FomParameterClass> parameters;
	private FederationObjectModel             model;

  public FomInteractionClass( String name, int handle )
	{
		this.name        = name;
		this.handle      = handle;
		this.order       = OrderEnum.TIMESTAMP;
		this.transport   = TransportEnum.RELIABLE;
		this.sharing	 = SharingEnum.NEITHER;
		// this.space       = null;
		this.parent      = null;
		// this.model       = null;
		this.children    = new HashSet<FomInteractionClass>();
		this.parameters  = new HashMap<Integer,FomParameterClass>();
	}

  // ==== Get Set API =====

  public int getHandle(){
		return handle;
	}

  protected void setHandle( int handle ){
		this.handle = handle;
	}

  public String getLocalName(){
		return name;
	}

  public String getQualifiedName(){
		// only calculate it if we don't already have it
		if( qualifiedName != null ) return qualifiedName;
		
		if( parent == null ) {
			// no parent, our full name is our name
			this.qualifiedName = name;
			return name;
		} else {
			// we have parents, get their name and append ours to the end
			this.qualifiedName = parent.getQualifiedName() + "." + name;
			return this.qualifiedName;
			//return parent.getQualifiedName() + "." + name;
		}
	}

	public String getVersionSafeQualifiedName() {
		throw new IllegalArgumentException("Not implemented i10");
	}

  	public OrderEnum getOrder(){
		return order;
	}

	public void setOrder( OrderEnum order ){
		this.order = order;
	}

	public boolean isRO(){
		return this.order == OrderEnum.RECEIVE;
	}
	
	public boolean isTSO(){
		return this.order == OrderEnum.TIMESTAMP;
	}

  public FomInteractionClass getParent(){
		return this.parent;
	}

  public void setParent( FomInteractionClass parent ){
		// remove us from our old parent //
		if( this.parent != null ) this.parent.children.remove(this);
		
		// // clear the qualified name caches //
		// this.qualifiedName = null;
		// this.vsafeQualifiedName = null;
		
		// register us in the new parent //
		this.parent = parent;
		if( parent != null ) {
			parent.children.add( this );
		}
	}

  	public TransportEnum getTransport() {
		return transport;
	}

	public void setTransport( TransportEnum transport ) {
		this.transport = transport;
	}

	public SharingEnum getSharing() {
		return sharing;
	}
	
	public void setSharing( SharingEnum sharing ) {
		this.sharing = sharing;
	}
	
	// public Space getSpace() {
	// 	return this.space;
	// }
	
	// public void setSpace( Space space ) {
	// 	this.space = space;
	// }

	public FederationObjectModel getModel() {
		return this.model;
	}
	
	public void setModel( FederationObjectModel model ) {
		this.model = model;
	}

  // ===== Child API =====

  public Set<FomInteractionClass> getChildTypes() {
		return this.children;
	}

  public FomInteractionClass getChildType( String name ) {
		for( FomInteractionClass child : children ) {
			if( child.getLocalName().equals(name) ) return child;
		}
		
		return null;
	}

  protected void cleave(){
    throw new IllegalArgumentException("Not implemented i12");
  }

  // ===== Parameter API =====

  public boolean addParameter( FomParameterClass parameter ) {
		String name = parameter.getName();
		
		// check to see if we already have an parameter with the same name
		for( FomParameterClass temp : parameters.values() ){
			if( temp.getName().equals(name) ){
				return false;
			}
		}
		
		// else 
		this.parameters.put( parameter.getHandle(), parameter );

		// assign the container property
		parameter.setContainer( this ); // todo <<< Not sure about this
		return true;
	}

  public FomParameterClass removeParameter( int handle ){
		FomParameterClass parameter = this.parameters.remove( handle );
		if( parameter == null ){
			return null;
		} else {
			parameter.setContainer( null );
			return parameter;
		}
	}

  public Set<FomParameterClass> getDeclaredParameters(){
		return new HashSet<FomParameterClass>( this.parameters.values() );
	}

  public Set<FomParameterClass> getAllParameters()
	{
		// if we don't have parent, all our parameters are just our local ones
		if( this.parent == null ){
			return this.getDeclaredParameters();
		} else {
			// we have a parent, must combine our parameter with theirs
			Set<FomParameterClass> inherited = this.parent.getAllParameters();
			// add our local parameters
			inherited.addAll( this.parameters.values() );
			// return the complete set
			return inherited;
		}
	}

  public FomParameterClass getDeclaredParameter( int handle ){
		return this.parameters.get( handle );
	}

  public FomParameterClass getDeclaredParameterCount( String name ){
		for( FomParameterClass parameter : parameters.values() ){
			if( parameter.getName().equals(name) )
				return parameter;
		}
		return null;
	}

  public int getParameterCount(){
		return parameters.size();
	}

  public FomParameterClass getParameter( int handle ){
		// check for the parameter locally first
		if( this.parameters.containsKey(handle) ){
			return this.parameters.get( handle );
		} else {
			// Also check the parent class for the parameter
			if( this.parent == null ) {
				return null;
			} else {
				return this.parent.getParameter( handle );
			}
		}
	}

  public int getParameterHandle( String name ){
		// check locally first
		for( FomParameterClass temp : this.parameters.values() ) {
			if( temp.getName().equals(name) ) {
				return temp.getHandle();
			}
		}
		
		// didn't find it, check up the tree
		if( this.parent == null ){
			// no tree to check up
			return FederationObjectModel.INVALID_HANDLE;
		} else {
			return this.parent.getParameterHandle( name );
		}
	}

  public String getParameterName( int handle ){
		if( this.parameters.containsKey(handle) ){
			return this.parameters.get(handle).getName();
		}
		
		// check inherited parameters
		if( this.parent == null ){
			return null;
		} else {
			return this.parent.getParameterName( handle );
		}
	}
}
