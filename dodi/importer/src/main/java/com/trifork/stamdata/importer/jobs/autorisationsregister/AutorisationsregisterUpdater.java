package com.trifork.stamdata.importer.jobs.autorisationsregister;

import static org.slf4j.LoggerFactory.*;

import java.sql.*;

import org.quartz.CronExpression;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.BatchJob;


/**
 * This class keeps a table of currently valid authorizations.
 * 
 * The table is replicated using MySQL replication. The table is used by the
 * STS.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
public class AutorisationsregisterUpdater implements BatchJob
{
	private static final Logger logger = getLogger(AutorisationsregisterUpdater.class);
	private static final String JOB_IDENTIFIER = "autorisationregister_updater";

	private final String cronExpression;

	@Inject
	AutorisationsregisterUpdater(@Named(JOB_IDENTIFIER + "." + CRON_EXPRESSION) String cronExpression)
	{
		Preconditions.checkArgument(CronExpression.isValidExpression(cronExpression), "Cron expression is invalid. expression=" + cronExpression);

		this.cronExpression = cronExpression;
	}

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

	@Override
	public String getCronExpression()
	{
		return cronExpression;
	}
}
