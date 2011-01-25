package com.trifork.sdm.replication.admin.models;


import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.Date;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.trifork.sdm.replication.settings.AdminDB;


public class LogEntryRepository
{

	private final Provider<Connection> provider;


	@Inject
	LogEntryRepository(@AdminDB Provider<Connection> provider)
	{
		this.provider = provider;
	}


	public LogEntry find(BigInteger id)
	{
		LogEntry entry = null;

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("SELECT * FROM auditlog WHERE (id = ?)");
			stm.setObject(1, id);
			ResultSet result = stm.executeQuery();

			result.next();

			entry = extractEntry(result);
		}
		catch (SQLException e)
		{
			new RuntimeException(e);

			// TODO: Log this error.
		}

		return entry;
	}


	private LogEntry extractEntry(ResultSet resultSet) throws SQLException
	{

		String id = resultSet.getString("id");
		String message = resultSet.getString("message");
		Timestamp created_at = resultSet.getTimestamp("created_at");

		LogEntry entry = new LogEntry(id, message, created_at);

		return entry;
	}


	public List<LogEntry> findAll()
	{

		List<LogEntry> entries = new ArrayList<LogEntry>();

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("SELECT * FROM auditlog ORDER BY created_at DESC");

			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{

				LogEntry entry = extractEntry(resultSet);

				entries.add(entry);
			}
		}
		catch (SQLException e)
		{
			new RuntimeException(e);

			// TODO: Log this error.
		}

		return entries;
	}


	public LogEntry create(String message, Object... args)
	{
		return create(String.format(message, args));
	}


	public LogEntry create(String message)
	{

		assert message != null && !message.isEmpty();

		Timestamp currentTime = new Timestamp(new Date().getTime());

		LogEntry entry = null;

		try
		{
			PreparedStatement stm = provider.get().prepareStatement("INSERT INTO auditlog SET message = ?, created_at = ?", Statement.RETURN_GENERATED_KEYS);

			stm.setString(1, message);
			stm.setTimestamp(2, currentTime);

			stm.executeUpdate();
			ResultSet resultSet = stm.getGeneratedKeys();

			if (resultSet != null && resultSet.next())
			{

				String id = resultSet.getString(1);
				entry = new LogEntry(id, message, currentTime);
			}

			stm.close();
		}
		catch (SQLException e)
		{
			new RuntimeException(e);

			// TODO: Log this error.
		}

		return entry;
	}
}
