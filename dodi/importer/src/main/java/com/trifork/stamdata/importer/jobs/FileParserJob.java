package com.trifork.stamdata.importer.jobs;

import java.io.File;
import java.sql.Connection;

import org.joda.time.Period;

import com.trifork.stamdata.importer.persistence.Persister;

public interface FileParserJob extends Job
{
	static final String MAX_TIME_GAP = "max_time_gap";
	
	boolean checkFileSet(File[] input);
	
	void run(File[] input, Persister persister, Connection connection, long changeset) throws Exception;
	
	/**
	 * The maximum time in minutes between file arrivals.
	 */
	Period getMaxTimeGap();
}
