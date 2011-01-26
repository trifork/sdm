package com.trifork.sdm.replication.admin.models;


import static java.lang.String.*;
import static org.slf4j.LoggerFactory.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.inject.Provider;


public class LogEntryRepository
{
	private static Logger LOG = getLogger(LogEntryRepository.class);

	private final Provider<Connection> connectionProvider;


	@Inject
	LogEntryRepository(Provider<Connection> provider)
	{
		this.connectionProvider = provider;
	}


	public List<LogEntry> findAll()
	{
		final String SQL = "SELECT * FROM auditlog ORDER BY created_at DESC";

		List<LogEntry> logEntries = new ArrayList<LogEntry>();

		Statement statement = null;

		try
		{
			Connection connection = connectionProvider.get();
			statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(SQL);

			while (resultSet.next())
			{
				LogEntry entry = extractEntry(resultSet);
				logEntries.add(entry);
			}
		}
		catch (SQLException e)
		{
			LOG.error("Database error file fetching audit log.", e);
		}
		finally
		{
			try
			{
				if (statement != null) statement.close();
			}
			catch (SQLException e)
			{
				LOG.error("Could not close database statement.", e);
			}
		}

		return logEntries;
	}


	public boolean create(String message, Object... args)
	{
		return create(format(message, args));
	}


	public boolean create(String message)
	{
		final String CREATE_SQL = "INSERT INTO auditlog SET message = ?, created_at = NOW()";

		boolean success = false;

		if (message != null && !message.isEmpty())
		{
			LOG.warn("Trying to log an empty audit message.");
		}
		else
		{
			PreparedStatement statement = null;

			try
			{
				Connection connection = connectionProvider.get();

				statement = connection.prepareStatement(CREATE_SQL);
				statement.setString(1, message);

				int created = statement.executeUpdate();

				success = created != -1;
			}
			catch (SQLException e)
			{
				LOG.error("Database error while writing to the audit log.", e);
			}
			finally
			{
				try
				{
					if (statement != null) statement.close();
				}
				catch (SQLException e)
				{
					LOG.error("Could not close database statement.", e);
				}
			}
		}

		return success;
	}


	private LogEntry extractEntry(ResultSet resultSet) throws SQLException
	{
		String id = resultSet.getString("id");
		String message = resultSet.getString("message");
		Timestamp created_at = resultSet.getTimestamp("created_at");

		LogEntry entry = new LogEntry(id, message, created_at);

		return entry;
	}
}
