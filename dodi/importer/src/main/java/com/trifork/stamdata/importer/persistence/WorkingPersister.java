package com.trifork.stamdata.importer.persistence;

import static com.trifork.stamdata.Preconditions.*;
import static java.lang.String.*;
import static java.sql.Statement.*;

import java.lang.reflect.*;
import java.security.*;
import java.sql.*;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;


public class WorkingPersister<T extends Record>
{
	private static final String ENTITY_ID_COLUMN = "EntityID";

	private static final int INITIAL_CHANGESET_VERSION = 0;

	private static final String EVENT_TYPE_COLUMN = "EventType";

	private final static String DELETE_EVENT_TYPE = "DELETE";
	private final static String CREATE_EVENT_TYPE = "CREATE";
	private final static String UPDATE_EVENT_TYPE = "UPDATE";
	private final static String FOUND_EVENT_TYPE = "FOUND";

	private Class<T> type;

	private final long changeset;

	private final PreparedStatement createEvent;
	private final PreparedStatement createEntity;
	private final PreparedStatement findLatestDuplicate;
	private final PreparedStatement fetchLatestEventForEntity;
	private final PreparedStatement closeRecordsNotFound;
	private final PreparedStatement deleteEventWithTypeFOUND;

	private final List<Method> columns;

	private final boolean isCompleteRegister;
	private boolean finishHasBeenCalled = false;

	public WorkingPersister(long changeset, boolean isCompleteRegister, Connection connection, Class<T> type) throws SQLException
	{
		checkArgument(changeset >= INITIAL_CHANGESET_VERSION, "changeset");
		checkNotNull(connection, "connection");
		checkNotNull(type, "type");

		this.changeset = changeset;
		this.isCompleteRegister = isCompleteRegister;
		this.type = type;

		// Initialize the list of output methods.

		columns = Records.getOrderedColumnList(type);

		List<String> columnNames = Lists.newArrayList();

		for (Method column : columns)
		{
			String columnName = Records.getColumnName(column);
			columnNames.add(columnName);
		}

		String columnNameList = StringUtils.join(columnNames, ", ");
		String parameterPlaceholderList = StringUtils.repeat("?, ", columns.size() - 1) + "?";

		String tableName = Records.getTableName(type);
		String idColumnName = Records.getColumnName(AbstractStamdataEntity.getIdMethod(type));

		// Initialize prepared statements.

		createEvent = connection.prepareStatement(format("INSERT INTO VersionEvent (TableName, Changeset, EventType, EntityID, EntitySHA1) VALUES ('%s', ?, ?, ?, ?)", tableName));
		createEntity = connection.prepareStatement(format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnNameList, parameterPlaceholderList), RETURN_GENERATED_KEYS);
		findLatestDuplicate = connection.prepareStatement(format("SELECT EventType, EntityID, EventID FROM VersionEvent WHERE TableName = '%s' AND EntitySHA1 = ? ORDER BY EventID LIMIT 1", tableName));
		fetchLatestEventForEntity = connection.prepareStatement(format("SELECT EventType FROM VersionEvent v, %s e WHERE e.PID = v.EntityID AND e.%s = ? ORDER BY v.EventID DESC LIMIT 1", tableName, idColumnName));
		closeRecordsNotFound = connection.prepareStatement(format("INSERT INTO VersionEvent (EntityID, EventType, EntitySHA1, Changeset, TableName) SELECT * FROM (SELECT EntityID, 'DELETE', EntitySHA1, %s, TableName FROM (SELECT MAX(EventID) AS EventID FROM VersionEvent e JOIN %s p ON e.EntityID = p.PID GROUP BY %s) AS x INNER JOIN VersionEvent e ON x.EventID = e.EventID WHERE Changeset != %s AND EventType != 'DELETE') AS y", changeset, tableName, idColumnName, changeset));
		deleteEventWithTypeFOUND = connection.prepareStatement("DELETE FROM VersionEvent WHERE EventType = 'FOUND'");
	}

	public void persist(T record) throws Exception
	{
		checkNotNull(record, "record");
		checkState(!finishHasBeenCalled, "persist must not be called after the finish method has been called.");

		// Calculate the record's column hash.
		// This is used to ensure we don't insert duplicates.

		MessageDigest digest = MessageDigest.getInstance("SHA1");
		Object[] values = new Object[columns.size()];

		for (int i = 0; i < columns.size(); i++)
		{
			Method column = columns.get(i);
			values[i] = column.invoke(record);

			if (values[i] != null) digest.update(values[i].toString().getBytes());
		}

		// Now we check the previous version of the registry to see
		// if the record already existed in the previous version.

		String columnHash = Hex.encodeHexString(digest.digest());
		findLatestDuplicate.setString(1, columnHash);
		ResultSet latestDuplicate = findLatestDuplicate.executeQuery();

		// Set the common parameters for any case.

		createEvent.setLong(1, changeset);
		createEvent.setString(4, columnHash);

		if (!latestDuplicate.next())
		{
			// No record is identical to this one, so we create a new one.

			for (int i = 0; i < values.length; i++)
			{
				createEntity.setObject(i + 1, values[i]);
			}

			createEntity.executeUpdate();
			ResultSet generatedKeys = createEntity.getGeneratedKeys();

			if (!generatedKeys.next())
			{
				throw new RuntimeException(format("No new record was created. entity=%s, key=%s", type.getName(), record.getKey()));
			}

			String keyColumn = generatedKeys.getMetaData().getColumnName(1);

			long entityID = generatedKeys.getLong(keyColumn);
			generatedKeys.close();

			// Now we have to figure out if the record is new or an update.

			fetchLatestEventForEntity.setObject(1, record.getKey());
			ResultSet latestEvent = fetchLatestEventForEntity.executeQuery();

			// If no previous version exists or it has
			// been deleted (has a DELETE event as the last event).
			// then it is a CREATE else an UPDATE.

			String eventType = (latestEvent.next() && latestEvent.getString(EVENT_TYPE_COLUMN) != DELETE_EVENT_TYPE) ? UPDATE_EVENT_TYPE : CREATE_EVENT_TYPE;

			createEvent.setString(2, eventType);
			createEvent.setLong(3, entityID);
			createEvent.execute();
		}
		else if (latestDuplicate.getString(EVENT_TYPE_COLUMN) == DELETE_EVENT_TYPE)
		{
			// A duplicate exists but the record has been deleted from the
			// registry.
			// We can reuse the existing identical record, simply by creating
			// a CREATE event that refers to it.

			Long entityID = latestDuplicate.getLong(ENTITY_ID_COLUMN);

			createEvent.setString(2, CREATE_EVENT_TYPE);
			createEvent.setLong(3, entityID);
			createEvent.execute();
		}
		else if (isCompleteRegister)
		{
			// We have a duplicate.
			//
			// If we are importing a complete dataset,
			// we have to delete any records that we don't find.
			// Therefore we keep track of those we do find,
			// so we can delete the others (make a DELETE event).

			// We do this by marking the entity with a FOUND event.
			// These events are later deleted to clean up.

			Long entityID = latestDuplicate.getLong(ENTITY_ID_COLUMN);

			createEvent.setString(2, FOUND_EVENT_TYPE);
			createEvent.setLong(3, entityID);
			createEvent.execute();
		}
		else
		{
			// TODO: What has happened here?
		}

		latestDuplicate.close();
	}

	public void finish() throws SQLException
	{
		finishHasBeenCalled = true;

		if (!isCompleteRegister || changeset == INITIAL_CHANGESET_VERSION) return;

		// We need to close (DELETE) any records that where not
		// seen, and where open in the previous changeset.

		closeRecordsNotFound.execute();
		deleteEventWithTypeFOUND.executeUpdate();

		// Clean up. The statements will not be used again.

		createEntity.close();
		findLatestDuplicate.close();
		fetchLatestEventForEntity.close();
		closeRecordsNotFound.close();
		deleteEventWithTypeFOUND.close();
	}
}
