package nl.literti.fom;

import java.io.Serializable;

import nl.literti.fom.enums.OrderEnum;
import nl.literti.fom.enums.SharingEnum;
import nl.literti.fom.enums.TransportEnum;

public class FomAttributeClass implements Serializable {

  private String name;
  private FomDatatype datatype;
  private int handle;
  private OrderEnum order;
  private TransportEnum transport;
  private SharingEnum sharing;
  private FomObjectClass container; // the object class that contains this attribute

  // private Space space;

  public FomAttributeClass(String name, FomDatatype datatype, int handle) {
    this.name = name;
    this.datatype = datatype;
    this.handle = handle;
    this.order     = OrderEnum.TIMESTAMP;
    this.transport = TransportEnum.RELIABLE;
    // this.sharing   = Sharing.NEITHER;
    this.container = null;
    // this.space     = null;
  }

  // 	public ACInstance newInstance()
  // {
  // 	return new ACInstance( this );
  // }

  // ==== Properties API =====

  public String getName() {
    return this.name;
  }

  public FomDatatype getDatatype() {
    return this.datatype;
  }

  public void setDatatype(FomDatatype datatype) {
    this.datatype = datatype;
  }

  public int getHandle() {
    return this.handle;
  }

  protected void setHandle(int handle) {
    this.handle = handle;
  }

  public OrderEnum getOrder()
  {
  	return this.order;
  }

  public void setOrder( OrderEnum order )
  {
  	this.order = order;
  }

  // public boolean isRO()
  // {
  // 	return this.order == Order.RECEIVE;
  // }

  // public boolean isTSO()
  // {
  // 	return this.order == Order.TIMESTAMP;
  // }

  public TransportEnum getTransport()
  {
  	return this.transport;
  }

  public void setTransport( TransportEnum transport )
  {
  	this.transport = transport;
  }

  public SharingEnum getSharing()
  {
  	return sharing;
  }

  public void setSharing( SharingEnum sharing )
  {
  	this.sharing = sharing;
  }

  // public Space getSpace()
  // {
  // 	return this.space;
  // }

  // public void setSpace( Space theSpace )
  // {
  // 	this.space = theSpace;
  // }

  public FomObjectClass getContainer() {
    return this.container;
  }

  public void setContainer(FomObjectClass container) {
    this.container = container;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
