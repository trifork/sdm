package com.trifork.stamdata.importer.jobs;


import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.persistence.ConnectionFactory;
import com.trifork.stamdata.importer.persistence.ConnectionFactory.Databases;


public class ImportTimeHelper
{
	private static final Logger LOGGER = getLogger(ImportTimeHelper.class);

	private final String importerName;
	private final Connection connection;
	private final ConnectionFactory factory;


	public ImportTimeHelper(String importerName, Connection connection, ConnectionFactory factory)
	{
		this.importerName = importerName;
		this.connection = connection;
		this.factory = factory;
	}


	public Date getLastImportTime() throws SQLException
	{
		Date importTime = null;
		PreparedStatement statement = null;

		String previousCatalog = connection.getCatalog();
		factory.setCatalog(connection, Databases.HOUSEKEEPING);

		statement = connection.prepareStatement("SELECT MAX(importtime) FROM Import WHERE spoolername = ?");
		statement.setString(1, importerName);
		ResultSet results = statement.executeQuery();

		if (results.next())
		{
			importTime = results.getTimestamp(1);
		}

		connection.setCatalog(previousCatalog);

		return importTime;
	}


	public void setImportTime(Date importTime)
	{
		PreparedStatement statement = null;

		try
		{
			String catalog = connection.getCatalog();
			factory.setCatalog(connection, Databases.HOUSEKEEPING);

			statement = connection.prepareStatement("INSERT INTO Import VALUES (?, ?)");

			statement.setTimestamp(1, new Timestamp(importTime.getTime()));
			statement.setString(2, importerName);

			statement.execute(); // FIXME: Should check that is actually worked.

			statement.close();

			connection.setCatalog(catalog);
		}
		catch (Exception e)
		{
			LOGGER.error("Could not set the import time for importer with name={}.", importerName, e);
			// TODO: Set error.
		}
		finally
		{
			factory.close(statement, connection);
		}
	}
}
