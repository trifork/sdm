/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
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
 * 
 * @author Thomas Børlum <thb@trifork.com>
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
