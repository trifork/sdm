package com.trifork.stamdata.importer.parsers;

import java.io.File;
import java.sql.Connection;

public interface FileImporter
{
	/**
	 * A unique string that identifies the importer.
	 * 
	 * This value is used to keep track of when the importer
	 * was last run. The identifier must also be filesystem safe.
	 * 
	 * @return
	 */
	String getIdentifier();
	
	boolean ensureRequiredFileArePresent(File[] input);
	
	void importFiles(File[] input, Connection connection) throws Exception;
}
