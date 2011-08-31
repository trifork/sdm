package com.trifork.stamdata.importer.persistence;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static com.trifork.stamdata.Preconditions.checkState;

import java.sql.Connection;
import java.sql.SQLException;

import org.joda.time.Instant;

public class Fetcher
{
	private final Connection connection;

	public Fetcher(Connection connection)
	{
		this.connection = checkNotNull(connection);
	}

	public <T> T fetch(Class<T> type) throws SQLException
	{
		return fetch(Instant.now(), type);
	}

	public <T> T fetch(Instant instant, Class<T> type) throws SQLException
	{
		checkNotNull(instant, "instant");
		checkNotNull("type", "type");
		checkState(!connection.isClosed(), "A fetcher cannnot be used after it's connection has been closed.");
		
		try
		{
			type.newInstance();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
