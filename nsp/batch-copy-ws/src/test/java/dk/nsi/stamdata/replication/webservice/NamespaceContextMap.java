/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package dk.nsi.stamdata.replication.webservice;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * An implementation of <a
 * href="http://java.sun.com/javase/6/docs/api/javax/xml/namespace/NamespaceContext.html">
 * NamespaceContext </a>. Instances are immutable.
 * 
 * @author McDowell
 */
public final class NamespaceContextMap implements
    NamespaceContext {

  private final Map<String, String> prefixMap;
  private final Map<String, Set<String>> nsMap;

  /**
   * Constructor that takes a map of XML prefix-namespaceURI values. A defensive
   * copy is made of the map. An IllegalArgumentException will be thrown if the
   * map attempts to remap the standard prefixes defined in the NamespaceContext
   * contract.
   * 
   * @param prefixMappings
   *          a map of prefix:namespaceURI values
   */
  public NamespaceContextMap(
      Map<String, String> prefixMappings) {
    prefixMap = createPrefixMap(prefixMappings);
    nsMap = createNamespaceMap(prefixMap);
  }

  /**
   * Convenience constructor.
   * 
   * @param mappingPairs
   *          pairs of prefix-namespaceURI values
   */
  public NamespaceContextMap(String... mappingPairs) {
    this(toMap(mappingPairs));
  }

  private static Map<String, String> toMap(
      String... mappingPairs) {
    Map<String, String> prefixMappings = new HashMap<String, String>(
        mappingPairs.length / 2);
    for (int i = 0; i < mappingPairs.length; i++) {
      prefixMappings
          .put(mappingPairs[i], mappingPairs[++i]);
    }
    return prefixMappings;
  }

  private Map<String, String> createPrefixMap(
      Map<String, String> prefixMappings) {
    Map<String, String> prefixMap = new HashMap<String, String>(
        prefixMappings);
    addConstant(prefixMap, XMLConstants.XML_NS_PREFIX,
        XMLConstants.XML_NS_URI);
    addConstant(prefixMap, XMLConstants.XMLNS_ATTRIBUTE,
        XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    return Collections.unmodifiableMap(prefixMap);
  }

  private void addConstant(Map<String, String> prefixMap,
      String prefix, String nsURI) {
    String previous = prefixMap.put(prefix, nsURI);
    if (previous != null && !previous.equals(nsURI)) {
      throw new IllegalArgumentException(prefix + " -> "
          + previous + "; see NamespaceContext contract");
    }
  }

  private Map<String, Set<String>> createNamespaceMap(
      Map<String, String> prefixMap) {
    Map<String, Set<String>> nsMap = new HashMap<String, Set<String>>();
    for (Map.Entry<String, String> entry : prefixMap
        .entrySet()) {
      String nsURI = entry.getValue();
      Set<String> prefixes = nsMap.get(nsURI);
      if (prefixes == null) {
        prefixes = new HashSet<String>();
        nsMap.put(nsURI, prefixes);
      }
      prefixes.add(entry.getKey());
    }
    for (Map.Entry<String, Set<String>> entry : nsMap
        .entrySet()) {
      Set<String> readOnly = Collections
          .unmodifiableSet(entry.getValue());
      entry.setValue(readOnly);
    }
    return nsMap;
  }

  @Override
  public String getNamespaceURI(String prefix) {
    checkNotNull(prefix);
    String nsURI = prefixMap.get(prefix);
    return nsURI == null ? XMLConstants.DEFAULT_NS_PREFIX : nsURI;
  }

  @Override
  public String getPrefix(String namespaceURI) {
    checkNotNull(namespaceURI);
    Set<String> set = nsMap.get(namespaceURI);
    return set == null ? null : set.iterator().next();
  }

  @Override
  public Iterator<String> getPrefixes(String namespaceURI) {
    checkNotNull(namespaceURI);
    Set<String> set = nsMap.get(namespaceURI);
    return set.iterator();
  }

  private void checkNotNull(String value) {
    if (value == null) {
      throw new IllegalArgumentException("null");
    }
  }

  /**
   * @return an unmodifiable map of the mappings in the form prefix-namespaceURI
   */
  public Map<String, String> getMap() {
    return prefixMap;
  }

}