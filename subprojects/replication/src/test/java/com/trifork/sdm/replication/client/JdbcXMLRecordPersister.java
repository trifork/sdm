package com.trifork.sdm.replication.client;

import static javax.xml.stream.XMLStreamConstants.*;
import static org.slf4j.LoggerFactory.*;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.util.Vector;

import javax.xml.stream.*;

import org.slf4j.Logger;

import com.google.inject.*;
import com.trifork.sdm.replication.replication.RecordPersister;
import com.trifork.stamdata.*;

import dk.sosi.seal.xml.XmlUtil;


public class JdbcXMLRecordPersister implements RecordPersister
{
	private static final Logger LOG = getLogger(JdbcXMLRecordPersister.class);

	protected Provider<Connection> connectionProvider;


	@Inject
	JdbcXMLRecordPersister(Provider<Connection> connectionProvider)
	{
		this.connectionProvider = connectionProvider;
	}


	@Override
	public URL persist(InputStream inputStream, Class<? extends Record> entitySet) throws Exception
	{
		URL nextPageURL = null;

		try
		{
			// Pre-calculate the expected names.

			String entityName = Entities.getName(entitySet);

			// Cache the entity's method's.

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

			Connection connection = connectionProvider.get();

			String columns = null;
			Vector<Object> columnValues = null;

			for (int event = reader.next(); event != END_DOCUMENT; event = reader.next())
			{
				String name = reader.getLocalName();

				if (event == START_ELEMENT)
				{
					// A record.

					if (name.equals(entityName))
					{

						// Reset the column values.

						columnValues = new Vector<Object>();

						// Handle the PID and HistoryID.

						String text = reader.getAttributeValue(null, "rowId");
						columnValues.add(text);
						columns = String.format("%sPID = ?", name);

						text = reader.getAttributeValue(null, "effectuationDate");
						columnValues.add(XmlUtil.fromXMLTimeStamp(text));
						columns = String.format("%s, ValidFrom = ?", columns, name);

						text = reader.getAttributeValue(null, "expirationDate");
						columnValues.add(XmlUtil.fromXMLTimeStamp(text));
						columns = String.format("%s, ValidTo = ?", columns, name);

						text = reader.getAttributeValue(null, "historyId");
						columnValues.add(new BigInteger(text));
						columns = String.format("%s, HistoryId = ?", columns, name);
					}

					// If the document contains an element 'nextPageURL'
					// that is where we can find the next page of the URL.

					else if (name.equals("nextPageURL"))
					{

						String text = reader.getElementText();
						nextPageURL = new URL(text);
					}

					// For any other elements we assume that it is a
					// column in the table.

					else
					{
						// Add the column name to the list of columns.

						columns = String.format("%s, %s = ?", columns, name);

						String text = reader.getElementText();

						// Convert the text values into the types we expect.

						Object value = null;

						if (EntityHelper.isDateColumn(entitySet, name))
						{

							value = XmlUtil.fromXMLTimeStamp(text);
						}
						else if (EntityHelper.isLongColumn(entitySet, name))
						{

							value = Long.parseLong(text);
						}
						else if (EntityHelper.isBooleanColumn(entitySet, name))
						{

							value = Boolean.parseBoolean(text);
						}
						else if (EntityHelper.isIntegerColumn(entitySet, name))
						{

							value = Integer.parseInt(text);
						}
						else if (EntityHelper.isStringColumn(entitySet, name))
						{

							value = text;
						}
						else
						{
							throw new RuntimeException("Tried to persist unsupported column type.");
						}

						columnValues.add(value);
					}
				}
				else if (event == XMLStreamConstants.END_ELEMENT)
				{
					// Once we reach the end write the entity to the db.

					if (name.equals(entityName))
					{
						String tableName = Entities.getTableName(entitySet);

						// Once we have parsed a complete record, we can
						// put it in the database with the column order we
						// parsed them in.

						PreparedStatement statement = connection.prepareStatement(String.format("REPLACE INTO %s SET %s", tableName, columns));

						for (int i = 1; i < columnValues.size() + 1; i++)
						{
							statement.setObject(i, columnValues.get(i - 1));
						}

						statement.executeUpdate();

						statement.close();
					}
				}
			}
		}
		catch (Throwable t)
		{
			LOG.error("Error while persisting entity.", t);
		}

		return nextPageURL;
	}
}
