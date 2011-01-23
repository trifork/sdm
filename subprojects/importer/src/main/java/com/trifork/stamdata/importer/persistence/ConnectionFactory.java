package com.trifork.stamdata.importer.persistence;


import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;


public class ConnectionFactory
{
	private static Logger LOGGER = getLogger(ConnectionFactory.class);


	public static enum Databases
	{
		SDM,
		HOUSEKEEPING
	}


	private static final String DB_CONNECTION_ERROR = "Could not create database connection.";

	private final String dbURI;
	private final String housekeepingSchema;
	private final String username;
	private final String password;
	private final String schema;


	public ConnectionFactory(String dbURI, String schema, String housekeepingSchema, String username,
			String password)
	{
		this.dbURI = dbURI;
		this.schema = schema;
		this.housekeepingSchema = housekeepingSchema;
		this.username = username;
		this.password = password;
	}


	// TODO: Callers should always check for null. This is dumb design, in
	// general.
	public Connection getConnection(boolean autoCommit, Databases database)
	{
		Connection connection = null;

		try
		{
			// Load the database driver.

			Class.forName("com.mysql.jdbc.Driver").newInstance();

			// Connect to the database.

			connection = DriverManager.getConnection(dbURI + "mysql", username, password);

			connection.setAutoCommit(autoCommit);

			setCatalog(connection, database);
		}
		catch (Exception e)
		{
			LOGGER.error(DB_CONNECTION_ERROR, e);
		}

		return connection;
	}


	public void setCatalog(Connection connection, Databases database) throws SQLException
	{
		String selectedSchema;

		if (database == Databases.SDM)
		{
			selectedSchema = schema;
		}
		else
		{
			selectedSchema = housekeepingSchema;
		}

		connection.setCatalog(selectedSchema);
	}


	public void close(Connection connection)
	{
		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			LOGGER.error("Could not close connection.", e);
		}
	}


	public void close(Statement statement, Connection connection)
	{
		try
		{
			if (statement != null)
			{
				statement.close();
			}
			else
			{
				LOGGER.warn("Cannot close stmt, because stement == null.");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Could not close statement.", e);
		}
		finally
		{
			close(connection);
		}
	}
}
