package com.trifork.stamdata.importer.jobs;

import java.sql.Connection;


/**
 * A job that restructures the database in some way.
 * 
 * These jobs are configured with a cron schedule for when they are to be run.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
public interface BatchJob extends Job
{
	static final String CRON_EXPRESSION = "run_cron_expression";

	/**
	 * Runs the restructuring code making the necessary changes to the
	 * database.
	 * 
	 * You do not have to handle connection errors and exceptions. This is
	 * handled by the calling code.
	 * 
	 * @param connection
	 *            the open database connection to use. This should not be
	 *            closed.
	 * @throws Exception
	 *             if anything goes wrong the code can throw any exception and
	 *             the calling code will rollback the connection.
	 */
	void run(Connection connection) throws Exception;

	/**
	 * The schedule for when the batch job should be run.
	 */
	String getCronExpression();
}
