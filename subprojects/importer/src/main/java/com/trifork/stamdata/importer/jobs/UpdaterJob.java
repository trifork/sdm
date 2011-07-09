package com.trifork.stamdata.importer.jobs;

import java.sql.Connection;
import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;


/**
 * A job wrapper class for an {@link Updater} object.
 * 
 * This class handles errors and connection creation for an {@link Updater}.
 */
public class UpdaterJob implements Job
{
	private static final Logger logger = LoggerFactory.getLogger(UpdaterJob.class);

	private final Updater updater;
	private final String cronExpression;

	private boolean isOK = true;
	private boolean isRunning = false;

	public UpdaterJob(Updater updater, String cronExpression)
	{
		this.updater = updater;
		this.cronExpression = cronExpression;
	}

	@Override
	public void run()
	{
		// If a job has failed it is quite serious.
		// We don't want to attempt to run it until
		// a technician has looked at the error.

		if (!isOK) return;

		Connection connection = null;
		isRunning = true;

		try
		{
			connection = MySQLConnectionManager.getConnection();

			updater.run(connection);
			
			ImportTimeManager.setImportTime(getIdentifier(), new Date());

			connection.commit();
		}
		catch (Exception e)
		{
			isOK = false;

			logger.error("An error occured while doing a database update. updater='{}'", getIdentifier(), e);

			try
			{
				connection.rollback();
			}
			catch (Exception ex)
			{
				logger.error("Updater job could not rollback the db connection.", ex);
			}
		}
		finally
		{
			MySQLConnectionManager.close(connection);
			isRunning = false;
		}
	}

	@Override
	public String getIdentifier()
	{
		return updater.getIdentifier();
	}

	@Override
	public boolean isExecuting()
	{
		return isRunning;
	}

	@Override
	public boolean isOK()
	{
		return isOK;
	}

	@Override
	public boolean isOverdue()
	{
		// TODO: In theory an updater job can be overdue if another job hangs.

		return false;
	}

	@Override
	public String getCronExpression()
	{
		return cronExpression;
	}

	@Override
	public String getHumanName()
	{
		return updater.getHumanName();
	}

	@Override
	public DateTime getLatestRunTime()
	{
		return ImportTimeManager.getLastImportTime(getIdentifier());
	}

	@Override
	public boolean hasBeenRun()
	{
		return getLatestRunTime() != null;
	}
}
