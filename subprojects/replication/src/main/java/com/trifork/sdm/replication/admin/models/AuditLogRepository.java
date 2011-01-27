package com.trifork.sdm.replication.admin.models;


import static java.lang.String.*;
import static org.slf4j.LoggerFactory.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.inject.Provider;
import com.trifork.sdm.replication.db.properties.AdminTransaction;


public class AuditLogRepository
{
	private static Logger LOG = getLogger(AuditLogRepository.class);

	private final Provider<Connection> connectionProvider;


	@Inject
	AuditLogRepository(Provider<Connection> provider)
	{
		this.connectionProvider = provider;
	}


	@AdminTransaction
	public List<LogEntry> findAll() throws SQLException
	{
		final String SQL = "SELECT * FROM auditlog ORDER BY created_at DESC";

		List<LogEntry> logEntries = new ArrayList<LogEntry>();

		Statement statement = null;

		Connection connection = connectionProvider.get();
		statement = connection.createStatement();

		ResultSet row = statement.executeQuery(SQL);

		while (row.next())
		{
			LogEntry entry = serialize(row);
			logEntries.add(entry);
		}

		return logEntries;
	}


	public boolean create(String message, Object... args) throws SQLException
	{
		return create(format(message, args));
	}


	public boolean create(String message) throws SQLException
	{
		final String CREATE_SQL = "INSERT INTO auditlog SET message = ?, created_at = NOW()";

		boolean success = false;

		if (message != null && !message.isEmpty())
		{
			LOG.warn("Trying to log an empty audit message.");
		}
		else
		{
			Connection connection = connectionProvider.get();

			PreparedStatement statement = connection.prepareStatement(CREATE_SQL);
			statement.setString(1, message);

			int created = statement.executeUpdate();

			success = created != -1;
		}

		return success;
	}


	private LogEntry serialize(ResultSet resultSet) throws SQLException
	{
		String id = resultSet.getString("id");
		String message = resultSet.getString("message");
		Timestamp created_at = resultSet.getTimestamp("created_at");

		LogEntry entry = new LogEntry(id, message, created_at);

		return entry;
	}
}
