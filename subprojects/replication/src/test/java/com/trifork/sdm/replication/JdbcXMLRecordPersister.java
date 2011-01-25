package com.trifork.sdm.replication;


import static javax.xml.stream.XMLStreamConstants.*;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Vector;

import javax.xml.stream.*;

import com.trifork.sdm.replication.replication.RecordPersister;
import com.trifork.stamdata.*;

import dk.sosi.seal.xml.XmlUtil;


public class JdbcXMLRecordPersister implements RecordPersister
{
	private final Connection connection;


	public JdbcXMLRecordPersister(Connection connection)
	{
		this.connection = connection;
	}


	@Override
	public URL persist(InputStream inputStream, Class<? extends Record> entitySet) throws Exception
	{

		// Pre-calculate the expected names.

		String entitySetName = NamingConvention.getResourceName(entitySet);
		String tableName = NamingConvention.getTableName(entitySet);

		// Cache the entity's method's.

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

		// String newestHistoryId = null;
		URL nextPageURL = null;

		String columns = null;
		Vector<Object> columnValues = null;

		for (int event = reader.next(); event != END_DOCUMENT; event = reader.next())
		{

			String name = reader.getLocalName();

			if (event == START_ELEMENT)
			{

				// The root element.

				if (name.equals("page"))
				{
					continue;
				}

				// A record.

				else if (name.equals(entitySetName))
				{

					// Reset the column values.

					columnValues = new Vector<Object>();
					String pid = reader.getAttributeValue(null, "rowId");
					columnValues.add(pid);

					// Handle the PID and HistoryID.

					columns = String.format("%sPID = ?", name);

					// newestHistoryId = reader.getAttributeValue(null, "historyId");
				}

				// If the document contains an element 'nextPageURL'
				// that is where we can find the next page of the URL.

				else if (name.equals("nextPageURL"))
				{

					String text = reader.getElementText();
					nextPageURL = new URL(text);
				}

				// We need to handle ValidFrom and ValidTo
				// aka. effectuationDate and expirationDate.

				else if (name.equals("effectuationDate"))
				{

					String text = reader.getElementText();
					Date date = XmlUtil.fromXMLTimeStamp(text);
					columns = String.format("%s, ValidFrom = ?", columns, name);
					columnValues.add(date);
				}
				else if (name.equals("expirationDate"))
				{

					String text = reader.getElementText();
					Date date = XmlUtil.fromXMLTimeStamp(text);
					columns = String.format("%s, ValidTo = ?", columns, name);
					columnValues.add(date);
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

				if (name.equals(entitySetName))
				{

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

		// connection.commit();
		connection.close();

		return nextPageURL;
	}
}
