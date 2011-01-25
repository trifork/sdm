package com.trifork.sdm.schema;


import java.io.*;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;

import javax.persistence.*;
import javax.xml.stream.*;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.*;

import com.trifork.stamdata.*;


/**
 * Goal that creates schemas for annotated classes.
 */
public class SchemaGenerator
{

	private static final int DEFAULT_LENGTH = 255;
	private static final String XML_ENCODING = "UTF-8";


	public static void main(String[] args) throws Exception
	{
		SchemaGenerator generator = new SchemaGenerator(args[0], args[1]);
		generator.execute();
	}


	/**
	 * The directory where the schemas are placed (.xsd files).
	 */
	public String target;

	/**
	 * The base package to generate models from.
	 */
	public String packageName;


	public SchemaGenerator(String destination, String packageName)
	{

		this.target = destination;
		this.packageName = packageName;
	}


	@SuppressWarnings("unchecked")
	public void execute() throws IOException, URISyntaxException, XMLStreamException, FactoryConfigurationError
	{
		// Find all entities.

		Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.getUrlsForPackagePrefix(packageName)).filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))).setScanners(new TypeAnnotationsScanner(), new TypeElementsScanner()));

		Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);

		for (Class<?> entity : entities)
		{

			Entity typeAnnotation = entity.getAnnotation(Entity.class);
			Versioned versioned = entity.getAnnotation(Versioned.class);

			// We have to cast each of the entities to Record,
			// there might be a smarter way of doing this.

			if (versioned == null || versioned.value().length == 0)
			{

				generateSchemaVersion((Class<? extends Record>) entity, typeAnnotation, 1);
			}
			else
			{
				for (int i = 0; i < versioned.value().length; i++)
				{

					generateSchemaVersion((Class<? extends Record>) entity, typeAnnotation, versioned.value()[i]);
				}
			}
		}
	}


	private void generateSchemaVersion(Class<? extends Record> type, Entity typeAnnotation, int version) throws IOException, XMLStreamException, FactoryConfigurationError
	{

		final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
		final String SDM_NS = "http://www.trifork.com/Stamdata";
		final String SDM_PREFIX = "sdm";

		// Determine the schema's name.

		String resourceName = NamingConvention.getXMLTypeName(type);

		// Make sure the destination folder exists.

		new File(target).mkdirs();

		String schemaFilePath = String.format("%s/%s_v%d.xsd", target, resourceName, version);

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = (XMLStreamWriter) factory.createXMLStreamWriter(new FileOutputStream(schemaFilePath), XML_ENCODING);

		// <? ... ?>
		writer.writeStartDocument();

		// <xs:schema ...>
		writer.writeStartElement("schema");
		writer.writeAttribute("elementFormDefault", "unqualified");
		writer.writeDefaultNamespace(XML_SCHEMA_NS);
		writer.writeAttribute("targetNamespace", SDM_NS);
		writer.writeNamespace("sdm", SDM_NS);

		//
		// The entity collection.
		//

		// <element name="page" [nextPage="..."] />
		writer.writeStartElement("element");
		writer.writeAttribute("name", "page");

		writer.writeStartElement("complexType");

		writer.writeStartElement("sequence");

		// <complexType ...>
		writer.writeStartElement("element");
		writer.writeAttribute("name", resourceName);
		writer.writeAttribute("type", SDM_PREFIX + ":" + resourceName);
		writer.writeAttribute("minOccurs", "0");
		writer.writeAttribute("maxOccurs", "unbounded");
		writer.writeEndElement();

		writer.writeEndElement();

		writer.writeEmptyElement("attribute");
		writer.writeAttribute("name", "nextPage");
		writer.writeAttribute("type", "anyURI");

		writer.writeEndElement();
		writer.writeEndElement();

		//
		// The actual entity
		//

		// <complexType ...>

		writer.writeStartElement("complexType");
		writer.writeAttribute("name", resourceName);

		// Write the documentation annotation if it exists.

		Documented documentation = type.getAnnotation(Documented.class);

		if (documentation != null && !documentation.value().isEmpty())
		{

			writer.writeStartElement("annotation");
			writer.writeStartElement("documentation");
			writer.writeCData(documentation.value());
			writer.writeEndElement();
			writer.writeEndElement();
		}

		// <xs:all ...>
		writer.writeStartElement("all");

		// We want the elements to appear in alphabetical order.

		SortedMap<String, Method> elements = findAllElementsForVersion(type, version);

		// Output each of the found element properties to the schema.

		for (Map.Entry<String, Method> entry : elements.entrySet())
		{

			String name = entry.getKey();
			Method method = entry.getValue();

			// Create the element in the schema.

			String xmlType = getXMLType(method);

			writer.writeStartElement("element");
			writer.writeAttribute("name", name);

			// If the property has a length restriction we have to restrict the
			// content.

			Column annotation = method.getAnnotation(Column.class);

			if (annotation.length() == DEFAULT_LENGTH)
			{

				writer.writeAttribute("type", xmlType);

				// Take care of the special @Id columns.

				handleColumns(writer, method);
			}
			else
			{
				// Take care of the special @Id columns.

				handleColumns(writer, method);

				writer.writeStartElement("simpleType");
				writer.writeStartElement("restriction");
				writer.writeAttribute("base", xmlType);

				// We can only restrict integrals and string,
				// but they have to be handled differently.

				if (xmlType.equals("string"))
				{

					writer.writeEmptyElement("maxLength");
					writer.writeAttribute("value", Integer.toString(annotation.length()));
				}
				else
				{

					// creates char array with 'length()' elements
					// fill each element of chars array with '9'.
					// TODO: This is not particularly pretty.

					char[] chars = new char[annotation.length()];
					Arrays.fill(chars, '9');

					writer.writeEmptyElement("maxInclusive");
					writer.writeAttribute("value", String.valueOf(chars));
				}

				writer.writeEndElement();
				writer.writeEndElement();
			}

			writer.writeEndElement();
		}

		writer.writeEndElement();

		// Technical Fields

		// <xs:attribute name="rowId" type="xs:unsignedLong" />

		writer.writeEmptyElement("attribute");
		writer.writeAttribute("name", "id");
		writer.writeAttribute("type", "unsignedLong");
		writer.writeAttribute("use", "required");

		// <xs:attribute name="historyId" type="xs:unsignedLong" />

		writer.writeEmptyElement("attribute");
		writer.writeAttribute("name", "historyId");
		writer.writeAttribute("type", "unsignedLong");
		writer.writeAttribute("use", "required");

		// Validity Period

		writer.writeEmptyElement("attribute");
		writer.writeAttribute("name", "effectuationDate");
		writer.writeAttribute("type", "dateTime");
		writer.writeAttribute("use", "required");

		writer.writeEmptyElement("attribute");
		writer.writeAttribute("name", "expirationDate");
		writer.writeAttribute("type", "dateTime");
		writer.writeAttribute("use", "required");

		writer.writeEndDocument();

		writer.flush();
		writer.close();
	}


	private void handleColumns(XMLStreamWriter writer, Method method) throws XMLStreamException
	{

		// If the property is annotated with @ID we set minOccurs="1",
		// and write that the element defined the natural key for this record.

		if (method.isAnnotationPresent(Id.class))
		{

			writer.writeStartElement("annotation");
			writer.writeStartElement("documentation");
			writer.writeCharacters("This element's value can be used as this entity set's natural key.");
			writer.writeEndElement();
			writer.writeEndElement();
		}
		else
		{

			writer.writeAttribute("minOccurs", "0");
		}
	}


	private String getXMLType(Method method) throws InvalidClassException, XMLStreamException
	{

		// Determine the type of the element based on the property's
		// return type.

		Class<?> returnType = method.getReturnType();
		String elementType;

		// At the moment we only support Long, int, boolean, Date and string.
		// Add types here if you need them.

		if (returnType == Long.class || returnType == int.class || returnType == Integer.class || returnType == long.class)
		{
			elementType = "long";
		}
		else if (returnType == double.class || returnType == Double.class || returnType == float.class || returnType == Float.class)
		{
			elementType = "double";
		}
		else if (returnType == Date.class)
		{
			elementType = "dateTime";
		}
		else if (returnType == String.class)
		{
			elementType = "string";
		}
		else if (returnType == boolean.class || returnType == Boolean.class)
		{
			elementType = "boolean";
		}
		else
		{
			throw new InvalidClassException(String.format("The schema generator does not support properties of type '%s'.", returnType.getSimpleName()));
		}

		return elementType;
	}


	private SortedMap<String, Method> findAllElementsForVersion(Class<? extends Record> type, int version) throws InvalidClassException
	{

		SortedMap<String, Method> elements = new TreeMap<String, Method>();

		for (Method method : NamingConvention.getColumns(type, version))
		{

			String elementName = NamingConvention.getXMLElementName(method);

			// Elements cannot have the same name!

			if (elements.containsKey(elementName))
			{

				String message = String.format("The class '%s' contains several output properties that will result in the same element name '%s'.", type.getSimpleName(), elementName);
				throw new InvalidClassException(message);
			}

			elements.put(elementName, method);
		}

		return elements;
	}
}
