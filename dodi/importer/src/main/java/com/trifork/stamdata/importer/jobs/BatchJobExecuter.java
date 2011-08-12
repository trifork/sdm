package com.trifork.stamdata.importer.jobs;

import static com.google.common.base.Preconditions.*;

import java.sql.Connection;

import org.joda.time.DateTime;
import org.slf4j.*;

import com.trifork.stamdata.importer.persistence.ConnectionPool;


/**
 * A job executer for an {@link BatchJob} object.
 * 
 * This class handles errors and connection creation for an {@link BatchJob}.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
public class BatchJobExecuter implements Executer
{
	private static final Logger logger = LoggerFactory.getLogger(BatchJobExecuter.class);

	private final BatchJob job;

	private boolean isOK = true;
	private boolean isRunning = false;

	private final ConnectionPool connectionPool;

	public BatchJobExecuter(BatchJob job, ConnectionPool connectionPool)
	{
		this.connectionPool = checkNotNull(connectionPool);
		this.job = checkNotNull(job);
	}

	@Override
	public void run()
	{
		// If a job has failed it is quite serious.
		// We don't want to attempt to run it until
		// a technician has looked into the error.

		if (!isOK) return;

		Connection connection = null;
		isRunning = true;

		try
		{
			connection = connectionPool.getConnection();

			job.run(connection);

			ImportTimeManager.updateLastRunTime(connection, job);

			connection.commit();
		}
		catch (Exception e)
		{
			isOK = false;

			logger.error("An error occured while doing a database update. job='{}'", getIdentifier(), e);

			try
			{
				connection.rollback();
			}
			catch (Exception ex)
			{
				logger.error("Updater job could not rollback the db connection. job=" + job.getHumanName(), ex);
			}
		}
		finally
		{
			try
			{
				if (connection != null) connection.close();
			}
			catch (Exception e)
			{
				isOK = false;
			}

			// No mater if there is an error or not
			// the state of the job should be set to
			// idle.

			isRunning = false;
		}
	}

	@Override
	public String getIdentifier()
	{
		return job.getIdentifier();
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
		return job.getCronExpression();
	}

	@Override
	public String getHumanName()
	{
		return job.getHumanName();
	}

	@Override
	public DateTime getLatestRunTime()
	{
		return ImportTimeManager.getLastRunTime(connectionPool.getConnection(), job);
	}

	@Override
	public boolean hasBeenRun()
	{
		return getLatestRunTime() != null;
	}
}
