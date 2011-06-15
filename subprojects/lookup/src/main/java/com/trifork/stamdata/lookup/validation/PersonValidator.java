package com.trifork.stamdata.lookup.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import oio.sagdok.person._1_0.PersonType;

import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class PersonValidator {
	private static final String PERSON_XSD = "/ns20.xsd";

	private Map<String, String> schemas = new HashMap<String, String>();
	private LSResourceResolver schemaResolver;
	private JAXBContext jaxbContext;
	private Schema schema;

	public PersonValidator() throws Exception {
		initSchemaMap();
		initSchemaResolver();
		initSchema();
	}
	

	public void validate(JAXBElement<PersonType> jaxbElement, ErrorHandler errorHandler) throws SAXException, JAXBException, IOException {
		Validator schemaValidator = schema.newValidator();
		schemaValidator.setErrorHandler(errorHandler);
		schemaValidator.validate(new JAXBSource(jaxbContext, jaxbElement));

	}

	private void initSchema() throws JAXBException, SAXException {
		jaxbContext = JAXBContext.newInstance(PersonType.class);
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		schemaFactory.setResourceResolver(schemaResolver);
		schema = schemaFactory.newSchema(new StreamSource(PersonValidator.class.getResourceAsStream(PERSON_XSD)));
	}

	private void initSchemaResolver() {
		schemaResolver = new LSResourceResolver() {
			@Override
			public LSInput resolveResource(String type,
                    final String namespaceURI,
                    String publicId,
                    String systemId,
                    String baseURI) {
				return new LSInput() {

					@Override
					public void setSystemId(String systemId) {
					}

					@Override
					public void setStringData(String stringData) {
					}

					@Override
					public void setPublicId(String publicId) {
					}

					@Override
					public void setEncoding(String encoding) {
					}

					@Override
					public void setCharacterStream(Reader characterStream) {
					}

					@Override
					public void setCertifiedText(boolean certifiedText) {
					}

					@Override
					public void setByteStream(InputStream byteStream) {
					}

					@Override
					public void setBaseURI(String baseURI) {
					}

					@Override
					public String getSystemId() {
						return null;
					}

					@Override
					public String getStringData() {
						return null;
					}

					@Override
					public String getPublicId() {
						return null;
					}

					@Override
					public String getEncoding() {
						return null;
					}

					@Override
					public Reader getCharacterStream() {
						return null;
					}

					@Override
					public boolean getCertifiedText() {
						return false;
					}

					@Override
					public InputStream getByteStream() {
						return PersonValidator.class.getResourceAsStream(schemas.get(namespaceURI));
					}

					@Override
					public String getBaseURI() {
						return null;
					}
				};

			}
		};
		
	}

	private void initSchemaMap() throws Exception {
		for(int i= 1; ; ++i) {
			String schemaLocation = "/ns" + i + ".xsd";
			InputStream input = PersonValidator.class.getResourceAsStream(schemaLocation);
			if(input == null) {
				break;
			}
			schemas.put(getTargetNamespace(getSchema(input)), schemaLocation);
		}
	}

	private String getTargetNamespace(Document schema) {
		return schema.getDocumentElement().getAttribute("targetNamespace");
	}

	private Document getSchema(InputStream input) throws Exception {
	    DocumentBuilder dBuilder = getDocumentBuilder();
	    return dBuilder.parse(input);
	}

	private DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    dbFactory.setNamespaceAware(true);
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder;
	}
}
