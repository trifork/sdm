package com.trifork.sdm.replication.replication;


import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

import javax.persistence.Column;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.fastinfoset.stax.factory.StAXOutputFactory;
import com.trifork.sdm.replication.db.Query;
import com.trifork.sdm.replication.util.URLFactory;
import com.trifork.stamdata.NamingConvention;
import com.trifork.stamdata.Record;

import dk.sosi.seal.xml.XmlUtil;


/**
 * Class that given an entity type, can output instances of that entity to an output stream in XML
 * format.
 * 
 * The class uses the information entity's {@link Column} annotations, and the naming convention to
 * infer names.
 */
public class XMLEntitySerializer implements EntitySerializer
{
	private static final boolean USE_ZULU_TIME = true;

	private final List<EntityEntry> elements = new ArrayList<EntityEntry>();
	private String entityXMLName;

	private final URLFactory urlFactory;

	private final Class<? extends Record> entity;


	/**
	 * Helper class that generates the XML.
	 * 
	 * We might as well generate the start- and end-tags at initialization, that way we don't have
	 * to do it on a per instance basis.
	 */
	private class EntityEntry
	{
		public final String name;


		public EntityEntry(Method method)
		{
			name = NamingConvention.getColumnName(method);
		}
	}


	/**
	 * Creates a XML serializer for a given type of entity.
	 * 
	 * The entity's output structure is cached to help optimize output.
	 * 
	 * @param entity the type of entity to be output;
	 */
	public XMLEntitySerializer(Class<? extends Record> entity, URLFactory urlFactory)
	{
		this.entity = entity;
		this.urlFactory = urlFactory;

		// Calculate all tags.

		entityXMLName = NamingConvention.getResourceName(entity);

		for (Method method : entity.getMethods())
		{
			if (method.isAnnotationPresent(Column.class))
			{

				elements.add(new EntityEntry(method));
			}
		}
	}


	@Override
	public void output(Query query, OutputStream outputStream, OutputFormat format, int pageSize) throws Exception
	{
		XMLStreamWriter writer;

		if (format == OutputFormat.XML)
		{
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			writer = factory.createXMLStreamWriter(outputStream);
		}
		else
		{
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			writer = factory.createXMLStreamWriter(outputStream);
		}

		String lastHistoryId = null;

		// <? ... ?>
		writer.writeStartDocument();

		// <page>
		writer.writeStartElement("page");

		// For each record in this page.
		for (Map<String, Object> record : query)
		{

			// Start the record.
			writer.writeStartElement(entityXMLName);

			// Row ID (PID)
			String pid = record.get("__PID").toString();
			writer.writeAttribute("rowId", pid);

			// History ID (A point in time)

			lastHistoryId = generateHistoryID(record);
			writer.writeAttribute("historyId", lastHistoryId);

			// Write the validity period.

			String effectuationDate = XmlUtil.toXMLTimeStamp((Date) record.get("__ValidFrom"), USE_ZULU_TIME);
			writer.writeAttribute("effectuationDate", effectuationDate);

			String expirationDate = XmlUtil.toXMLTimeStamp((Date) record.get("__ValidFrom"), USE_ZULU_TIME);
			writer.writeAttribute("expirationDate", expirationDate);

			// For each output method (column) we write an element.
			// The element names are pre-calculated and cached, in
			// the constructor.
			for (EntityEntry entry : elements)
			{
				Object value = record.get(entry.name);

				// Only write the element if it is not null.
				if (value != null)
				{
					writer.writeStartElement(entry.name);

					// We have to output date time in a XML specific
					// manner.
					if (value instanceof Timestamp)
					{
						writer.writeCharacters(XmlUtil.toXMLTimeStamp((Date) value, USE_ZULU_TIME));
					}

					// All other types SHOULD fit in just fine
					// when converted to string.
					else
					{
						writer.writeCharacters(value.toString());
					}

					writer.writeEndElement();
				}
			}

			// End the record.
			writer.writeEndElement();

			// Write the data to the output stream.
			writer.flush();
		}

		if (lastHistoryId != null)
		{
			// Point to the next page.
			// The next page might be empty, there is nothing wrong
			// with that, only a little performance hit.
			// Optimally we should just not return a
			// <nextPageURL>...</nextPageURL>
			// when there are no more rows to replicate.

			String nextURL = urlFactory.create(entity, pageSize, lastHistoryId);

			writer.writeStartElement("nextPageURL");
			writer.writeCharacters(nextURL);
			writer.writeEndElement();
		}

		// Close the page.
		writer.writeEndDocument();

		// End the data stream.
		writer.flush();
		writer.close();
	}


	private String generateHistoryID(Map<String, Object> record)
	{
		// Get the time in modified date and convert it to seconds.
		Timestamp modifiedProperty = (Timestamp) record.get("__ModifiedDate");
		String modifiedDate = Long.toString(modifiedProperty.getTime() / 1000);

		// Pad the PID if needed (which it probably always will).
		Long pidProperty = (Long) record.get("__PID");
		String pid = Long.toString(pidProperty);

		// TODO: Magic numbers.

		int paddingLength = 10 - pid.length();

		if (paddingLength > 0)
		{

			for (int i = 0; i < paddingLength; i++)
			{
				pid = "0" + pid; // TODO: Slow (but it works)
			}
		}
		else if (paddingLength < 0)
		{
			throw new IllegalStateException("The DB has run out of PID space according to the replication protocol.");
		}

		// Construct the update token used to request future
		// updates.
		return modifiedDate + pid;
	}
}
