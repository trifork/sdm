package com.trifork.stamdata.importer.parsers;

import java.io.File;
import java.sql.Connection;

public interface FileParser
{
	/**
	 * @see Job#getIdentifier()
	 */
	String getIdentifier();
	
	boolean ensureRequiredFileArePresent(File[] input);
	
	void importFiles(File[] input, Connection connection) throws Exception;
}
