package nl.literti.fom;

public interface FomDatatype {

  String getName();

  FomDatatypeEnum getDatatypeEnum();

  FomDatatype createUnlinkedClone();

  // public String getName() {
  //   return name;
  // }

  // public FomDatatypeEnum getDatatypeEnum() {
  //   return datatypeEnum;
  // }

  // public FomDatatypeClass createUnlinkedClone(){
  //   return null;
  // }
}
