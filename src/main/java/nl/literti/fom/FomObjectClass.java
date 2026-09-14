package nl.literti.fom;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import nl.literti.fom.enums.SharingEnum;

public class FomObjectClass implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name; // local name of the object class, e.g. "vehicle"
  private String
      qualifiedName; // fully qualified name, including parent names, e.g.
                     // "hlaobjectroot.vehicle.tank"

  private int handle;
  private FomObjectClass parent;
  private SharingEnum sharing;
  private Map<Integer, FomAttributeClass> attributes;
  private Set<FomObjectClass> children;
  private FederationObjectModel model;

  // private String                  vsafeQualifiedName; // version-safe name, name difference
  // between 1.3 and 1516e

  public FomObjectClass(String name, int handle) {
    this.name = name;
    this.handle = handle;
    this.parent = null;
    this.sharing = SharingEnum.NEITHER;
    this.attributes = new HashMap<Integer, FomAttributeClass>();
    this.children = new HashSet<FomObjectClass>();
  }

  // ==== Get Set API =====

  // getDeclaredAttributeCount
  public int getDeclaredAttributeCount() {
    return attributes.size();
  }

  public int getHandle() {
    return this.handle;
  }

  protected void setHandle(int handle) {
    this.handle = handle;
  }

  public String getLocalName() {
    return name;
  }

  public String getQualifiedName() {
    // only calculate it if we don't already have it
    if (qualifiedName != null) return qualifiedName;

    if (parent == null) {
      // no parent, our full name is our name
      this.qualifiedName = name;
      return name;
    } else {
      // we have parents, get their name and append ours to the end
      this.qualifiedName = parent.getQualifiedName() + "." + name;
      return this.qualifiedName;
      // return parent.getQualifiedName() + "." + name;
    }
  }

  public String getVersionSafeQualifiedName() {
    throw new IllegalArgumentException("Not implemented 10");
  }

  public FomObjectClass getParent() {
    return this.parent;
  }

  public void setParent(FomObjectClass oc) {
    // remove old parent
    if (this.parent != null) this.parent.children.remove(this);

    // clear the qualified name caches //
    // this.qualifiedName = null; todo <<< Not sure about this
    // this.vsafeQualifiedName = null;

    // registernew parent //
    this.parent = oc;
    if (oc != null) {
      oc.children.add(this);
    }
  }

  public SharingEnum getSharing() {
    return sharing;
  }

  public void setSharing(SharingEnum sharing) {
    this.sharing = sharing;
  }

  public FederationObjectModel getModel() {
    return this.model;
  }

  public void setModel(FederationObjectModel model) {
    this.model = model;
  }

  public String toString() {
    throw new IllegalArgumentException("Not implemented 11");

    // if( PorticoConstants.USE_Q_NAMES )
    // 	return getQualifiedName() + " " + this.getAllAttributes();
    // else
    // 	return "" + this.handle + " " + this.getAllAttributes();
  }

  // ===== Child API =====

  public Set<FomObjectClass> getChildTypes() {
    return this.children;
  }

  public FomObjectClass getChildType(String name) {
    for (FomObjectClass child : this.children) {
      if (child.getLocalName().equals(name)) return child;
    }

    return null;
  }

  public boolean isAssignableTo(FomObjectClass other) {
    FomObjectClass current = this;
    while (current != null) {
      if (current == other) return true;
      else current = current.getParent();
    }

    return false;
  }

  //   public OCInstance REMOVE_newInstance(int creatingFederate) {
  //     throw new IllegalArgumentException("Not implemented");
  //     // return REMOVE_newInstance( creatingFederate, null );
  //   }

  //   public OCInstance REMOVE_newInstance(int creatingFederate, Set<Integer> publishedAttributes)
  // {
  //     throw new IllegalArgumentException("Not implemented");
  //   }

  protected void cleave() {
    throw new IllegalArgumentException("Not implemented 12");
  }

  // ===== Attribute API =====

  public boolean addAttribute(FomAttributeClass attribute) {
    String name = attribute.getName();

    // check to see if we already have an attribute with the same name
    for (FomAttributeClass temp : attributes.values()) {
      if (temp.getName().equals(name)) {
        return false;
      }
    }

    // else
    this.attributes.put(attribute.getHandle(), attribute);

    // assign the container property of the attribute to us
    attribute.setContainer(this); // todo <<< Not sure about this
    return true;
  }

  public FomAttributeClass removeAttribute(int handle) {
    FomAttributeClass attribute = this.attributes.remove(handle);
    if (attribute == null) {
      return null;
    } else {
      attribute.setContainer(null);
      return attribute;
    }
  }

  public Set<FomAttributeClass> getDeclaredAttributes() {
    return new HashSet<FomAttributeClass>(this.attributes.values());
  }

  public Set<FomAttributeClass> getAllAttributes() {
    if (this.parent == null) {
      return this.getDeclaredAttributes();
    } else {
      // Also get inherited attributes from the parent class
      Set<FomAttributeClass> inherited = this.parent.getAllAttributes();
      inherited.addAll(this.attributes.values());
      return inherited;
    }
  }

  public FomAttributeClass getPrivilegeToDelete() {
    throw new IllegalArgumentException("Not implemented 13");
  }

  public Set<String> getAttributeNames(Collection<Integer> attributeHandles) {
    HashSet<String> names = new HashSet<String>();
    for (Integer attributeHandle : attributeHandles) names.add(getAttributeName(attributeHandle));
    return names;
  }

  public Set<FomAttributeClass> getAttributeMetadata(Collection<Integer> attributeHandles) {
    HashSet<FomAttributeClass> metadata = new HashSet<FomAttributeClass>();
    for (Integer attributeHandle : attributeHandles) metadata.add(attributes.get(attributeHandle));
    return metadata;
  }

  

  public Set<Integer> getAllAttributeHandles() {
    if (this.parent == null) {
      return new HashSet<Integer>(attributes.keySet());
    } else {
      // Also get inherited attributes handles from the parent class
      Set<Integer> inherited = this.parent.getAllAttributeHandles();
      inherited.addAll(attributes.keySet());
      return inherited;
    }
  }

  // was getDeclaredAttribute
  public FomAttributeClass getDeclaredAttribute(int handle) {
    return this.attributes.get(handle);
  }

  public FomAttributeClass getDeclaredAttribute(String name) {
    for (FomAttributeClass attribute : attributes.values()) {
      if (attribute.getName().equals(name)) return attribute;
    }
    return null;
  }

  public FomAttributeClass getAttribute(int handle) {
    if (this.attributes.containsKey(handle)) {
      return this.attributes.get(handle);
    } else {
      // Also check the parent class for the attribute
      if (this.parent == null) {
        return null;
      } else {
        return this.parent.getAttribute(handle);
      }
    }
  }

  public int getAttributeHandle(String name) {
    // check locally first
    for (FomAttributeClass temp : this.attributes.values()) {
      if (temp.getName().equals(name)) {
        return temp.getHandle();
      }
    }

    // check parent classes
    if (this.parent == null) {
      System.out.println(
          "Attribute not found in this class, there is no parent for attribute: " + name);
      // there is nothing higher to check, ensure that we're not talking about privToDelete,
      // if we haven't found it yet it might because we've got the wrong HLA version
      if (name != null && name.equals("HLAprivilegeToDeleteObject")) {

        return this.model.getPrivilegeToDelete();
        // throw new IllegalArgumentException("Not implemented 14");
      } else {
        return FederationObjectModel.INVALID_HANDLE;
      }
    } else {
      System.out.println("checking the parent for attribute: " + name);
      return this.parent.getAttributeHandle(name);
    }
  }

  public String getAttributeName(int handle) {
    if (this.attributes.containsKey(handle)) {
      return this.attributes.get(handle).getName();
    }

    // check parent classes
    if (this.parent == null) {
      return null;
    } else {
      return this.parent.getAttributeName(handle);
    }
  }

  public boolean hasAttribute(int handle) {
    if (this.attributes.containsKey(handle)) return true;
    // check parent classes
    else if (parent != null) return parent.hasAttribute(handle);
    else return false;
  }
}
