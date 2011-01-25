package com.trifork.sdm.replication.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.inject.Inject;

import com.trifork.sdm.replication.settings.*;

public class MySQLConnectionFactory implements JdbcConnectionFactory {

	@Inject
	@DatabaseURL
	private String url;
	
	@Inject
	@MainDB
	private String schema;
	
	@Inject
	@HousekeepingDB
	private String housekeepingSchema;
	
	@Inject
	@DuplicateDB
	private String duplicateSchema;

	@Inject
	@AdminDB
	private String adminSchema;	
	
	@Inject
	@DbUsername
	private String username;
	
	@Inject
	@DbPassword
	private String password;
	
	
	// Cache the connections per request.
	private HashMap<DB, Connection> connections = new HashMap<DB, Connection>();

	
	{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not load the database driver.", e);
		}
	}

	/**
	 * Creates a connection to one of the databases form
	 * {@link DB}.
	 * 
	 * It is 
	 * @param database the db you want to connect to.
	 */
	@Override
	public Connection create(DB database) {

		Connection connection;
		
		// Check if we have a connection cached already.
		
		if (connections.containsKey(database)) {
			return connections.get(database);
		}
		
		// else make a new one.

		String schema;
		
		switch (database) {
		case SdmDB:
			schema = this.schema; break;
		case SdmHousekeepingDB:
			schema = this.housekeepingSchema; break;
		case SdmAdminDB:
			schema = this.adminSchema; break;
		case SdmDuplicateDB:
			schema = this.duplicateSchema; break;
		default:
			// This can never happen. (Stupid compiler)
			throw new RuntimeException();
		}
		
		try {
			connection = DriverManager.getConnection(url + schema, username, password);
			//connection.setAutoCommit(false);
		}
		catch (Exception e) {
			// TODO: Add proper exception handling for the database.
			throw new RuntimeException("Could not open a connection to the database.", e);
		}

		return connection;
	}
}
