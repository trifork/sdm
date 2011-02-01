package com.trifork.sdm.replication.replication;

import static com.trifork.sdm.replication.db.properties.Database.*;
import static com.trifork.sdm.replication.replication.OutputFormat.*;
import static com.trifork.stamdata.Entities.*;
import static dk.sosi.seal.xml.XmlUtil.*;
import static java.lang.String.*;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Date;

import javax.persistence.Column;
import javax.xml.stream.*;

import com.google.inject.*;
import com.trifork.sdm.replication.db.properties.Transactional;
import com.trifork.sdm.replication.util.URLFactory;
import com.trifork.stamdata.*;


/**
 * Class that given an entity type, can output instances of that entity to an
 * output stream in XML format.
 * 
 * The class uses the information entity's {@link Column} annotations, and the
 * naming convention to infer names.
 */
public class XMLEntityWriter implements EntityWriter
{
	private static final int PADDING_MAX_LENGTH = 10;
	private static final boolean USE_ZULU_TIME = true;
	private final URLFactory urlFactory;

	private final Provider<Connection> connectionProvider;


	@Inject
	XMLEntityWriter(@Transactional(WAREHOUSE) Provider<Connection> connectionProvider, URLFactory urlFactory)
	{
		this.connectionProvider = connectionProvider;
		this.urlFactory = urlFactory;
	}


	@Override
	public void write(OutputStream outputStream, Class<? extends Record> resourceType, OutputFormat format, int pageSize, Date sinceDate, long sinceId) throws Exception
	{
		XMLOutputFactory factory;

		if (format == XML)
		{
			factory = XMLOutputFactory.newInstance();
		}
		else
		// FastInfoset
		{
			factory = XMLOutputFactory.newInstance();
		}

		XMLStreamWriter writer = factory.createXMLStreamWriter(outputStream);

		// Infer some names.

		String tableName = Entities.getTableName(resourceType);
		String pidName = Entities.getPIDName(resourceType);
		String resourceXMLName = Entities.getXMLTypeName(resourceType);

		// Construct the SQL.

		String query = format("SELECT * FROM %1$s WHERE (%2$s > ? AND ModifiedDate = ?) OR (ModifiedDate > ?) ORDER BY %2$s, ModifiedDate, CreatedDate LIMIT %3$d", tableName, pidName, pageSize);

		// Get db connection.

		Connection connection = connectionProvider.get();

		PreparedStatement statement = connection.prepareStatement(query);
		statement.setLong(1, sinceId);
		statement.setObject(2, sinceDate);
		statement.setObject(3, sinceDate);

		ResultSet records = statement.executeQuery();

		String lastHistoryId = null;

		// <? ... ?>
		writer.writeStartDocument();

		// <page>
		writer.writeStartElement("page");

		// For each record in this page.
		while (records.next())
		{
			// Start the record.
			writer.writeStartElement(resourceXMLName);

			// Row ID (PID)
			String pid = records.getString(pidName);
			writer.writeAttribute("rowId", pid);

			// History ID (A point in time)

			lastHistoryId = generateHistoryID(records, pid);
			writer.writeAttribute("historyId", lastHistoryId);

			// Write out the validity period.

			String effectuationDate = toXMLTimeStamp(records.getTimestamp("ValidFrom"), USE_ZULU_TIME);
			writer.writeAttribute("effectuationDate", effectuationDate);

			String expirationDate = toXMLTimeStamp(records.getTimestamp("ValidTo"), USE_ZULU_TIME);
			writer.writeAttribute("expirationDate", expirationDate);

			// For each output method (column) we write an element.
			// TODO: The element names should be pre-calculated and cached.

			for (Method column : getColumns(resourceType))
			{
				String elementName = getXMLElementName(column);
				String columnName = getColumnName(column);

				Object value = records.getObject(columnName);

				// Only write the element if it is not null.
				if (value != null)
				{
					writer.writeStartElement(elementName);

					// We have to output date time in a XML specific
					// manner.
					if (value instanceof Timestamp)
					{
						String date = toXMLTimeStamp((Date) value, USE_ZULU_TIME);
						writer.writeCharacters(date);
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

		statement.close();

		if (lastHistoryId != null)
		{
			// Point to the next page.
			// The next page might be empty, there is nothing wrong
			// with that, only a little performance hit.
			// Optimally we should just not return a
			// <nextPageURL>...</nextPageURL>
			// when there are no more rows to replicate.

			String nextURL = urlFactory.create(resourceType, pageSize, lastHistoryId, format.toString());

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


	private String generateHistoryID(ResultSet results, String pid) throws SQLException
	{
		// Get the time in modified date and convert it to seconds.
		Timestamp modifiedProperty = results.getTimestamp("ModifiedDate");
		String modifiedDate = Long.toString(modifiedProperty.getTime() / 1000);

		// Pad the PID if needed (which it probably always will).

		int paddingLength = PADDING_MAX_LENGTH - pid.length();

		if (paddingLength > 0)
		{
			for (int i = 0; i < paddingLength; i++)
			{
				pid = "0" + pid; // Sloooow (but it works).
			}
		}
		else if (paddingLength < 0)
		{
			throw new IllegalStateException("The DB has run out of PID space according to the replication protocol. This could be quite bad.");
		}

		// Construct the update token used to request future updates.

		return modifiedDate + pid;
	}
}
