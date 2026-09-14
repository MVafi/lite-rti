package nl.literti.fom;

import java.io.Serializable;

// Common interface for all FOM datatypes.
public class FomParameterClass implements Serializable {

    private String          name;
	private FomDatatype     datatype;
	private int             handle;
	private FomInteractionClass container; // the interaction class that contains this attribute

    public FomParameterClass( String name, FomDatatype datatype, int handle )
	{
		this.name      = name;
		this.datatype  = datatype;
		this.handle    = handle;
		this.container = null;
	}

    // ==== Properties API =====
	public String getName(){
		return this.name;
	}
	
	public FomDatatype getDatatype(){
		return this.datatype;
	}
	
	public void setDatatype( FomDatatype datatype ){
		this.datatype = datatype;
	}
	
	public int getHandle(){
		return this.handle;
	}

    protected void setHandle( int handle ){
		this.handle = handle;
	}

	public FomInteractionClass getContainer(){
		return this.container;
	}
	
	public void setContainer( FomInteractionClass container ){
		this.container = container;
	}

	@Override
	public String toString(){
		return this.name;
	}
}
