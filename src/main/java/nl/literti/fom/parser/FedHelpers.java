package nl.literti.fom.parser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nl.literti.fom.FomDatatype;
import nl.literti.fom.datatypes.ArrayType;
import nl.literti.fom.datatypes.BasicType;
import nl.literti.fom.datatypes.EnumeratedType;
import nl.literti.fom.datatypes.FixedRecordType;
import nl.literti.fom.datatypes.SimpleType;
import nl.literti.fom.datatypes.UnresolvedDatatype;
import nl.literti.fom.datatypes.UnresolvedEnumerator;
import nl.literti.fom.datatypes.VariantRecordType;
import nl.literti.fom.datatypes.carriers.Alternative;
import nl.literti.fom.datatypes.carriers.Dimension;
import nl.literti.fom.datatypes.carriers.Endianness;
import nl.literti.fom.datatypes.carriers.Enumerator;
import nl.literti.fom.datatypes.carriers.Field;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// XML Helper Methods
public class FedHelpers {

  /**
   * Return a list of child element types of the provided node. If the node has no children or none
   * of the children are element nodes, an empty list is returned.
   */
  public static List<Element> getChildElements(Node node) {
    NodeList list = node.getChildNodes();
    ArrayList<Element> elements = new ArrayList<Element>();
    for (int i = 0; i < list.getLength(); i++) {
      Node temp = list.item(i);
      if (temp.getNodeType() == Node.ELEMENT_NODE) elements.add((Element) temp);
    }

    return elements;
  }

  /**
   * Return the first child element under node that has given given tag name. If there is none that
   * matches, return null.
   */
  public static Element getFirstChildElement(Node node, String tag) {
    NodeList list = node.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      Node temp = list.item(i);
      if (temp.getNodeType() != Node.ELEMENT_NODE) continue;

      Element tempElement = (Element) temp;
      if (tempElement.getTagName().equals(tag)) return tempElement;
    }

    return null;
  }

  /**
   * Return a list of all the children elements of node with the given tag name. If there are no
   * matching elements, an empty list is returned.
   */
  public static List<Element> getAllChildElements(Node node, String tag) {
    NodeList list = node.getChildNodes();
    ArrayList<Element> elements = new ArrayList<Element>();
    for (int i = 0; i < list.getLength(); i++) {
      Node temp = list.item(i);
      if (temp.getNodeType() != Node.ELEMENT_NODE) continue;

      Element tempElement = (Element) temp;
      if (tempElement.getTagName().equals(tag)) elements.add(tempElement);
    }

    return elements;
  }

  public static boolean isElement(Node node) {
    return node.getNodeType() == Node.ELEMENT_NODE;
  }

  /**
   * This method searches the given element for the first child element with the identified
   * tag-name. When it finds it, its text content is returned. If there is no child element with the
   * given tag-name, an exception is thrown.
   */
  public static String getChildValue(Element element, String name) throws Exception {
    return getChildValue(element, name, null);
  }

  /**
   * To provide some better context to errors, this method takes an additional `typeName` parameter
   * that will be used in any exception reporting. This allows pretty generic, difficult to find
   * errors like "Element <interactionClass> missing child <order>" to become a bit more descriptive
   * by including the name value of the interaction class (in this example).
   *
   * @param element Element to look for the child in
   * @param name Name of the child to look for and return its value
   * @param typeName Name of the type we are looking in if known. `null` will cause the name to not
   *     be printed and is still valid.
   * @return The value of the named sub-element inside the given element.
   * @throws JErrorReadingFed if the value cannot be found
   */
  public static String getChildValue(Element element, String name, String typeName)
      throws Exception {
    if (typeName == null) typeName = "unknown";

    String value = null;

    // check for the value of an attribute first
    if (element.hasAttribute(name)) {
      value = element.getAttribute(name);
    } else {
      // if no attribute is present, look for a child element
      Element child = getFirstChildElement(element, name);
      if (child == null) {
        String message =
            String.format(
                "Element <%s name=\"%s\"> missing child <%s> element",
                element.getTagName(), typeName, name);

        throw new Exception(message);
      } else {
        value = child.getTextContent().trim();
      }
    }

    if (value == null || value.isEmpty()) {
      String message =
          String.format(
              "Element <%s name=\"%s\"> empty child <%s> element",
              element.getTagName(), typeName, name);

      throw new Exception(message);
    } else {
      return value;
    }
  }

  /**
   * Same as {@link #getChildValue(Element, String)} except that it returns null if there is no
   * child rather than throwing an exception.
   *
   * @see {@link #getChildValue(Element, String, String)}
   */
  public static String getChildValueForgiving(Element element, String name, String typeName) {
    try {
      return getChildValue(element, name, typeName);
    } catch (Exception error) {
      // let's just ignore this
      return null;
    }
  }

  public static int getChildValueInt(Element element, String name, String typeName)
      throws Exception {
    if (typeName == null) typeName = "unknown";

    String asString = getChildValue(element, name, typeName);
    try {
      return Integer.parseInt(asString);
    } catch (Exception e) {
      String message =
          String.format(
              "Element <%s name=\"%s.%s\"> contains a non-numeric value [%s]",
              element.getTagName(), typeName, name, asString);
      throw new Exception(message);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////// Datatype Helper Methods /////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  public static Set<FomDatatype> extractDatatypes(Element datatypesElement) throws Exception {
    Set<FomDatatype> fedTypes = new HashSet<FomDatatype>();

    for (Element temp : getChildElements(datatypesElement)) {
      String tagName = temp.getTagName();
      if (tagName.equals("basicDataRepresentations")) fedTypes.addAll(extractBasicTypes(temp));
      else if (tagName.equals("simpleDataTypes")) fedTypes.addAll(extractSimpleTypes(temp));
      else if (tagName.equals("enumeratedDataTypes")) fedTypes.addAll(extractEnumeratedTypes(temp));
      else if (tagName.equals("arrayDataTypes")) fedTypes.addAll(extractArrayTypes(temp));
      else if (tagName.equals("fixedRecordDataTypes"))
        fedTypes.addAll(extractFixedRecordTypes(temp));
      else if (tagName.equals("variantRecordDataTypes"))
        fedTypes.addAll(extractVariantRecordTypes(temp));
    }

    return fedTypes;
  }

  private static Set<BasicType> extractBasicTypes(Element basicParent) throws Exception {
    Set<BasicType> importedTypes = new HashSet<BasicType>();

    List<Element> basicElements = getAllChildElements(basicParent, "basicData");
    for (Element basicElement : basicElements) {
      String name = getChildValue(basicElement, "name");
      int bitsize = getChildValueInt(basicElement, "size", name);
      if (bitsize < 0) throw new Exception("negative size");

      String endiannessString = getChildValue(basicElement, "endian", name);
      Endianness endianness = Endianness.fromFomString(endiannessString);

      importedTypes.add(new BasicType(name, bitsize, endianness));
    }

    return importedTypes;
  }

  private static Set<SimpleType> extractSimpleTypes(Element simpleParent) throws Exception {
    Set<SimpleType> importedTypes = new HashSet<SimpleType>();

    List<Element> simpleElements = getAllChildElements(simpleParent, "simpleData");
    for (Element simpleElement : simpleElements) {
      String name = getChildValue(simpleElement, "name");

      // The representation type may not have been imported yet depending on the order of the
      // FOM elements. For the moment we'll create a placeholder for the representation, and
      // resolve it later on using the Linker class
      String representationName = getChildValue(simpleElement, "representation", name);
      FomDatatype placeholder = new UnresolvedDatatype(representationName);

      importedTypes.add(new SimpleType(name, placeholder));
    }

    return importedTypes;
  }

  private static Set<EnumeratedType> extractEnumeratedTypes(Element enumeratedParent)
      throws Exception {
    Set<EnumeratedType> importedTypes = new HashSet<EnumeratedType>();

    List<Element> enumeratedElements = getAllChildElements(enumeratedParent, "enumeratedData");
    for (Element enumeratedElement : enumeratedElements) {
      String name = getChildValue(enumeratedElement, "name");

      // The representation type may not have been imported yet depending on the order of the
      // FOM elements. For the moment we'll create a placeholder for the representation, and
      // resolve it later on using the Linker class
      String representationName = getChildValue(enumeratedElement, "representation", name);
      FomDatatype placeholder = new UnresolvedDatatype(representationName);
      List<Enumerator> enumerators = new ArrayList<Enumerator>();

      List<Element> enumeratorElements = getAllChildElements(enumeratedElement, "enumerator");

      if (enumeratorElements.isEmpty())
        throw new Exception("Enumerator datatype has no values: " + name);

      for (Element enumeratorElement : enumeratorElements) {
        String enumeratorName = getChildValue(enumeratorElement, "name");

        // 1516 == values are a comma delimited string in the "values" attribute
        // NOTE: Some 1516e tests also have values specified this way! So this for
        // both 1516 and 1516e
        String valuesCsv = getChildValueForgiving(enumeratorElement, "values", enumeratorName);
        if (valuesCsv != null) {
          String[] valueTokens = valuesCsv.split(",");
          if (valueTokens.length == 0) {
            String message =
                String.format("Enumerator %s.%s contains no values", name, enumeratorName);
            throw new Exception(message);
          } else if (valueTokens.length > 1) {
            // Log a warning: only one value supported
          }

          try {
            // This may need to be a bit cleverer in order to handle hex formatted
            // numbers or chars
            Number value = NumberFormat.getInstance().parse(valueTokens[0]);
            enumerators.add(new Enumerator(enumeratorName, value));
          } catch (ParseException pe) {
            String message =
                String.format(
                    "Enumerator %s.%s contains a non-numeric value %s",
                    name, enumeratorName, valueTokens[0]);
            throw new Exception(message);
          }
        } else {
          // 1516e == values each have a separate "value" element
          List<Element> valueElements = getAllChildElements(enumeratorElement, "value");
          if (valueElements.size() == 0) {
            String message =
                String.format("Enumerator %s.%s contains no values", name, enumeratorName);
            throw new Exception(message);
          } else if (valueElements.size() > 1) {
            // Log a warning: only one value supported
          }

          // TODO: Support multiple value enumerators if the need arises
          Element valueElement = valueElements.get(0);
          String valueToken = valueElement.getTextContent();
          try {
            Number value = NumberFormat.getInstance().parse(valueToken);
            enumerators.add(new Enumerator(enumeratorName, value));
          } catch (ParseException pe) {
            String message =
                String.format(
                    "Enumerator %s.%s contains a non-numeric value %s",
                    name, enumeratorName, valueToken);
            throw new Exception(message);
          }
        }
      }

      importedTypes.add(new EnumeratedType(name, placeholder, enumerators));
    }

    return importedTypes;
  }

  private static Set<ArrayType> extractArrayTypes(Element arrayParent) throws Exception {
    Set<ArrayType> importedTypes = new HashSet<ArrayType>();

    List<Element> arrayElements = getAllChildElements(arrayParent, "arrayData");
    for (Element arrayElement : arrayElements) {
      String name = getChildValue(arrayElement, "name");

      // The datatype may not have been imported yet depending on the order of the FOM
      // elements. For the moment we'll create a placeholder for the representation, and
      // resolve it later on using the Linker class
      String datatypeName = getChildValue(arrayElement, "dataType", name);
      FomDatatype placeholder = new UnresolvedDatatype(datatypeName);

      String cardinalityValue = getChildValue(arrayElement, "cardinality", name);
      List<Dimension> dimensions = Dimension.fromFomString(cardinalityValue);

      importedTypes.add(new ArrayType(name, placeholder, dimensions));
    }

    return importedTypes;
  }

  private static Set<FixedRecordType> extractFixedRecordTypes(Element fixedRecordParent)
      throws Exception {
    Set<FixedRecordType> importedTypes = new HashSet<FixedRecordType>();

    List<Element> fixedRecordElements = getAllChildElements(fixedRecordParent, "fixedRecordData");
    for (Element fixedRecordElement : fixedRecordElements) {
      String name = getChildValue(fixedRecordElement, "name");

      List<Field> fields = new ArrayList<Field>();
      List<Element> fieldElements = getAllChildElements(fixedRecordElement, "field");
      for (Element fieldElement : fieldElements) {
        String fieldName = getChildValue(fieldElement, "name");

        // The datatype may not have been imported yet depending on the order of the FOM
        // elements. For the moment we'll create a placeholder for the representation, and
        // resolve it later on using the Linker class
        String datatypeName = getChildValue(fieldElement, "dataType", fieldName);
        FomDatatype placeholder = new UnresolvedDatatype(datatypeName);

        fields.add(new Field(fieldName, placeholder));
      }

      importedTypes.add(new FixedRecordType(name, fields));
    }

    return importedTypes;
  }

  private static Set<VariantRecordType> extractVariantRecordTypes(Element variantRecordParent)
      throws Exception {
    Set<VariantRecordType> importedTypes = new HashSet<VariantRecordType>();

    List<Element> variantRecordElements =
        getAllChildElements(variantRecordParent, "variantRecordData");
    for (Element variantRecordElement : variantRecordElements) {
      String name = getChildValue(variantRecordElement, "name");
      String discriminant = getChildValue(variantRecordElement, "discriminant", name);
      String datatypeName = getChildValue(variantRecordElement, "dataType", name);

      List<Element> alternativeElements = getAllChildElements(variantRecordElement, "alternative");

      Set<Alternative> alternatives = new HashSet<Alternative>();
      for (Element alternativeElement : alternativeElements) {
        String alternativeName = getChildValue(alternativeElement, "name");
        String alternativeDatatypeName = getChildValue(alternativeElement, "dataType", name);
        String enumeratorCsv = getChildValue(alternativeElement, "enumerator", alternativeName);
        String[] enumeratorTokens = enumeratorCsv.split(",");
        if (enumeratorTokens.length == 0) {
          String message =
              String.format(
                  "Variant Record alternative %s.%s contains no enumerators",
                  name, alternativeName);
          throw new Exception(message);
        }

        // NOTE: The enumeratorToken may specify a range of enumerators. As we are acting on
        // the assumption that we haven't imported the discriminant Enumerated Type yet,
        // we'll just insert a placeholder with the full range and let the linker expand
        // it for us
        Set<UnresolvedEnumerator> enumerators = new HashSet<UnresolvedEnumerator>();
        for (String enumeratorToken : enumeratorTokens)
          enumerators.add(new UnresolvedEnumerator(enumeratorToken.trim()));

        alternatives.add(
            new Alternative(
                alternativeName, new UnresolvedDatatype(alternativeDatatypeName), enumerators));
      }

      importedTypes.add(
          new VariantRecordType(
              name, discriminant, new UnresolvedDatatype(datatypeName), alternatives));
    }

    return importedTypes;
  }
}
