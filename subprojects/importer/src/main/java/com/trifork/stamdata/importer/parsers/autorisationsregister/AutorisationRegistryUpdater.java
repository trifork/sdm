package com.trifork.stamdata.importer.parsers.autorisationsregister;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.Statement;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;


/**
 * This class keeps a table of currently valid authorizations.
 * 
 * The table is replicated using MySQL replication. The table is used by the
 * STS.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class AutorisationRegistryUpdater implements Runnable
{
	private static final Logger logger = getLogger(AutorisationRegistryUpdater.class);

	@Override
	public void run()
	{
		logger.info("Starting update of autreg table.");

		Connection connection = null;
		Statement statement = null;

		try
		{
			connection = MySQLConnectionManager.getConnection();
			statement = connection.createStatement();

			statement.executeUpdate("TRUNCATE TABLE autreg");
			statement.executeUpdate("INSERT INTO autreg (cpr, given_name, surname, aut_id, edu_id) SELECT cpr, Fornavn, Efternavn, Autorisationsnummer, UddannelsesKode FROM Autorisation WHERE ValidFrom <= NOW() AND ValidTo > NOW();");

			connection.commit();

			logger.info("Finished update of autreg table.");
		}
		catch (Exception e)
		{
			try
			{
				connection.rollback();
			}
			catch (Exception ex)
			{}

			logger.error("Error in autorisation updating job.", e); // TODO:
																	// Throw the
																	// exception
		}
		finally
		{
			MySQLConnectionManager.close(connection);
		}
	}
}
