package com.trifork.oioxml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OioXmlProduction {
	private static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	// the initial set of schemas we want to build production schemas for
	private Set<URI> initialSchemaLocations = new HashSet<URI>();
	// maps schemaLocation URLs to Documents
	private Map<URI, Document> oioXmlSchemas = new HashMap<URI, Document>();
	// maps namespaces to Documents (the resulting production schema for the namespace)
	private Map<String, Document> productionSchemas = new HashMap<String, Document>();
	// maps namespaces to the namespace prefix used for that namespace in production schemas
	private Map<String, String> productionNamespacePrefixMap = new HashMap<String, String>();
	
	private String outputDirectory;
	
	private void fixElementAttribute(Element elm, String attributeName, Map<String, String> fileNsPrefixMap) {
		if(elm.getAttribute(attributeName) != null) {
			String type = elm.getAttribute(attributeName);
			if(type != null && !type.isEmpty()) {
				int colonIndex = type.indexOf(':');
				if(colonIndex != -1) {
					String oldPrefix = type.substring(0, colonIndex);
					String namespace = fileNsPrefixMap.get(oldPrefix);
					if(namespace != null) {
						String newPrefix = productionNamespacePrefixMap.get(namespace);
						elm.setAttribute(attributeName, newPrefix + type.substring(type.indexOf(":")));
					}
					else {
						System.out.println("prefix " + oldPrefix + " not mapped to any namespace");
					}
				}
			}
		}
	}
	
	private void fixNamespacePrefixesInDeclarations(Document schema) {
		fixPrefixedAttributes(schema, XML_SCHEMA_NS, "element", "type");
		fixPrefixedAttributes(schema, XML_SCHEMA_NS, "element", "ref");
		fixPrefixedAttributes(schema, XML_SCHEMA_NS, "attribute", "type");
		fixPrefixedAttributes(schema, XML_SCHEMA_NS, "attribute", "ref");
		fixPrefixedAttributes(schema, XML_SCHEMA_NS, "extension", "base");
	}
	
	private void fixPrefixedAttributes(Document schema, String elementNs, String elementName, String attrName) {
		Map<String, String> namespacePrefixes = getNamespacePrefixes(schema);
		NodeList elementDecls = schema.getElementsByTagNameNS(elementNs, elementName);
		for(int i = 0;i < elementDecls.getLength(); ++i) {
			Element elm = (Element) elementDecls.item(i);
			fixElementAttribute(elm, attrName, namespacePrefixes);
		}
		
	}

	private List<Element> getChildNodes(Element parent, String namespace, String localName) {
		List<Element> result = new ArrayList<Element>();
		NodeList children = parent.getChildNodes();
		for(int i = 0;i < children.getLength(); ++i) {
			Node item = children.item(i);
			if(item instanceof Element) {
				Element element = (Element) item;
				if(namespace.equals(item.getNamespaceURI()) && localName.equals(item.getLocalName())) {
					result.add(element);
				}
			}
		}
		return result;
	}
	
	private Map<String, String> getNamespacePrefixes(Document schema) {
		Map<String, String> result = new HashMap<String, String>();
		Element rootElement = schema.getDocumentElement();
		NamedNodeMap attributes = rootElement.getAttributes();
		System.out.println("number of attributes: " + attributes.getLength());
		for(int i = 0; i < attributes.getLength(); ++i) {
			Attr attribute = (Attr) attributes.item(i);
			System.out.println("Attribute name: " + attribute.getName());
			System.out.println("Atribute value: " + attribute.getValue());
			if(attribute.getName().startsWith("xmlns:")) {
				String prefix = attribute.getName().substring("xmlns:".length());
				String namespace = attribute.getValue();
				result.put(prefix, namespace);
				System.out.println("Prefix " + prefix + " mapped to " + namespace);
			}
		}
		return result;
	}
	
	private List<Element> getDeclarations(Document schema) {
		List<Element> result = new ArrayList<Element>();
		Element rootElement = schema.getDocumentElement();
		result.addAll(getChildNodes(rootElement, XML_SCHEMA_NS, "element"));
		result.addAll(getChildNodes(rootElement, XML_SCHEMA_NS, "complexType"));
		result.addAll(getChildNodes(rootElement, XML_SCHEMA_NS, "simpleType"));
		return result;
	}

	private String getTargetNamespace(Document schema) {
		return schema.getDocumentElement().getAttribute("targetNamespace");
	}

	private void downloadAndParseAllSchemas() throws Exception {
		Set<URI> unprocessedSchemaLocations = new HashSet<URI>();
		unprocessedSchemaLocations.addAll(initialSchemaLocations);
		while(!unprocessedSchemaLocations.isEmpty()) {
			processAnotherSchema(unprocessedSchemaLocations);
		}
	}
	
	private Set<String> getReferencedSchemaLocations(Document doc) {
		Set<String> result = new HashSet<String>();
		NodeList imports = doc.getElementsByTagNameNS(XML_SCHEMA_NS, "import");
		for(int i = 0; i < imports.getLength(); ++i) {
			Element imp = (Element) imports.item(i);
			result.add(imp.getAttribute("schemaLocation"));
		}
		NodeList includes = doc.getElementsByTagNameNS(XML_SCHEMA_NS, "include");
		for(int i = 0; i < includes.getLength(); ++i) {
			Element imp = (Element) includes.item(i);
			result.add(imp.getAttribute("schemaLocation"));
		}
		return result;
	}
	
	private void processAnotherSchema(Set<URI> unprocessedSchemaLocations) throws Exception {
		Iterator<URI> iterator = unprocessedSchemaLocations.iterator();
		URI schemaLocation = iterator.next();
		iterator.remove();
		System.out.println("Getting schema " + schemaLocation);
		Document schema = getSchema(schemaLocation);
		oioXmlSchemas.put(schemaLocation, schema);
		Set<String> referencedSchemaLocations = getReferencedSchemaLocations(schema);
		referencedSchemaLocations.removeAll(oioXmlSchemas.keySet());
		for(String loc : referencedSchemaLocations) {
			unprocessedSchemaLocations.add(createAbsoluteUri(schemaLocation, loc));
		}
	}
	
	private Document getSchema(URI schemaLocation) throws Exception {
	    DocumentBuilder dBuilder = getDocumentBuilder();
	    return dBuilder.parse(schemaLocation.toURL().toExternalForm());
	}

	private DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    dbFactory.setNamespaceAware(true);
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder;
	}
	
	private void decideNamespacePrefixes() {
		int nsCount = 1;
		for(Document doc : oioXmlSchemas.values()) {
			String targetNamespace = getTargetNamespace(doc);
			if(!productionNamespacePrefixMap.containsKey(targetNamespace)) {
				productionNamespacePrefixMap.put(targetNamespace, "ns" + nsCount++);
			}
		}
	}
	
	private void createInitialProductionSchemas() throws ParserConfigurationException {
	    for(String targetNamespace : productionNamespacePrefixMap.keySet()) {
	    	Document schema = getDocumentBuilder().newDocument();
	    	Element rootElm = schema.createElementNS(XML_SCHEMA_NS, "schema");
	    	schema.appendChild(rootElm);
	    	rootElm.setAttribute("xmlns", XML_SCHEMA_NS);
	    	rootElm.setAttribute("targetNamespace", targetNamespace);
	    	rootElm.setAttribute("elementFormDefault", "qualified");
	    	rootElm.setAttribute("attributeFormDefault", "unqualified");
	    	for(Map.Entry<String, String> nsPrefix : productionNamespacePrefixMap.entrySet()) {
	    		rootElm.setAttribute("xmlns:" + nsPrefix.getValue(), nsPrefix.getKey());
	    	}
	    	this.productionSchemas.put(targetNamespace, schema);
	    }
	}
	
	private void printSchema(Document doc, OutputStream out) throws Exception {
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    Source source = new DOMSource(doc);
	    Result output = new StreamResult(out);
	    transformer.transform(source, output);
		
	}
	
	private void fixNamespacePrefixesInOioXmlSchemas() {
		for(Document schema : oioXmlSchemas.values()) {
			fixNamespacePrefixesInDeclarations(schema);
		}
	}

	private void copyDeclarationsToProductionSchemas() {
		for(Document schema : oioXmlSchemas.values()) {
			String targetNamespace = getTargetNamespace(schema);
			Document productionSchema = productionSchemas.get(targetNamespace);
			List<Element> declarations = getDeclarations(schema);
			for(Element elm : declarations) {
				productionSchema.getDocumentElement().appendChild(productionSchema.importNode(elm, true));
			}
		}
	}

	private void addImportStatements() {
		for(Document schema : productionSchemas.values()) {
			String targetNamespace = getTargetNamespace(schema);
			Element rootNode = schema.getDocumentElement();
			for(String namespace : productionSchemas.keySet()) {
				if(namespace.equals(targetNamespace)) {
					continue;
				}
				Element importElm = schema.createElementNS(XML_SCHEMA_NS, "import");
				rootNode.appendChild(importElm);
				importElm.setAttribute("namespace", namespace);
				importElm.setAttribute("schemaLocation", productionNamespacePrefixMap.get(namespace) + ".xsd");
			}
		}
	}

	private void printSchemasToFiles() throws Exception {
		File outputDirectoryFile = new File(outputDirectory);
		if(!outputDirectoryFile.isDirectory()) {
			throw new RuntimeException("Cannot output schemas to " + outputDirectory + ": not a directory");
		}
		
		for(Map.Entry<String, Document> entry : productionSchemas.entrySet()) {
			Document schema = entry.getValue();
			String namespace = entry.getKey();
			String filename = productionNamespacePrefixMap.get(namespace) + ".xsd";
			File outputFile = new File(outputDirectoryFile, filename);
			FileOutputStream output = new FileOutputStream(outputFile);
			printSchema(schema, output);
			output.close();
		}
	}

	public void buildProductionSchemas() throws Exception {
		downloadAndParseAllSchemas();
		for(URI schemaLocation : oioXmlSchemas.keySet()) {
			System.out.println(schemaLocation);
		}
		decideNamespacePrefixes();
		for(Map.Entry<String, String> entry : productionNamespacePrefixMap.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		createInitialProductionSchemas();
		addImportStatements();
		fixNamespacePrefixesInOioXmlSchemas();
		copyDeclarationsToProductionSchemas();
		printSchemasToFiles();
	}
	
	public OioXmlProduction(Collection<String> initialSchemaUrls, String outputDirectory) throws MalformedURLException, URISyntaxException{
		this.outputDirectory = outputDirectory;
		for(String schemaUrl : initialSchemaUrls) {
			initialSchemaLocations.add(createAbsoluteUri(null, schemaUrl));
		}
	}

	private URI createAbsoluteUri(URI locUri, String path) throws URISyntaxException {
		URI pathUri = new URI(path);
		pathUri = pathUri.normalize();
		if(pathUri.isAbsolute()) {
			return pathUri;
		}
		if(locUri == null) {
			// local file system
			File file = new File(path);
			return file.getAbsoluteFile().toURI().normalize();
		}
		return locUri.resolve(path).normalize();
	}
	
	public static void main(String[] args) throws Exception{
		CommandLineParser parser = new PosixParser();
		Options options = new Options();
		options.addOption("o", "output", true, "The directory where the program will output production schemas");
		CommandLine line = parser.parse( options, args );
		String outputDirectory = line.getOptionValue("o");
		String[] schemaUrls = line.getArgs();
		new OioXmlProduction(Arrays.asList(schemaUrls), outputDirectory).buildProductionSchemas();
	}
}
