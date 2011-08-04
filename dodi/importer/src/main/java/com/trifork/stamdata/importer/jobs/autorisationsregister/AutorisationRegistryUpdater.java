package com.trifork.stamdata.importer.jobs.autorisationsregister;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.Statement;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.jobs.Updater;


/**
 * This class keeps a table of currently valid authorizations.
 * 
 * The table is replicated using MySQL replication. The table is used by the
 * STS.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
public class AutorisationRegistryUpdater implements Updater
{
	private static final Logger logger = getLogger(AutorisationRegistryUpdater.class);

	@Override
	public void run(Connection connection) throws Exception
	{
		logger.info("Starting update of 'autreg' table.");
		
		Statement statement = connection.createStatement();

		statement.executeUpdate("TRUNCATE TABLE autreg");
		statement.executeUpdate("INSERT INTO autreg (cpr, given_name, surname, aut_id, edu_id) SELECT cpr, Fornavn, Efternavn, Autorisationsnummer, UddannelsesKode FROM Autorisation WHERE ValidFrom <= NOW() AND ValidTo > NOW();");

		statement.close();

		logger.info("Finished update of 'autreg' table.");
	}

	@Override
	public String getIdentifier()
	{
		return "autorisationsupdater";
	}

	@Override
	public String getHumanName()
	{
		return "Ajourføring af autorisationsregisteret for STS'en.";
	}
}
