package com.trifork.stamdata.importer.jobs;

import java.sql.Connection;

/**
 * A job that restructures the database in some way.
 * 
 * These jobs are configured with a cron schedule for when
 * they are to be run.
 * 
 * @author Thomas Børlum <thb@trofork.com>
 */
public interface Updater
{
	/**
	 * Runs the restructoring code making the neccesery changes to the database.
	 * 
	 * You do not have to handle connection errors and exceptions. This is handled
	 * by the calling code.
	 * 
	 * @param connection the open database connection to use. This should not be closed.
	 * @throws Exception if anything goes wrong the code can throw any exception and the calling code will rollback the connection.
	 */
	void run(Connection connection) throws Exception;

	/**
	 * The identifier is used for two things.
	 * 
	 * 1. To keep track of the time when a job was last run.
	 * 2. To display in the GUI.
	 * 
	 * Once set it is important that it is never changed.
	 * Therefor you should not use the class name, as it
	 * is subject to change.
	 */
	String getIdentifier();
	
	/**
	 * (non-javadoc)
	 * @see Job#getHumanName()
	 */
	String getHumanName();
}
