package com.trifork.stamdata.importer.jobs;

import java.io.File;

import com.trifork.stamdata.importer.persistence.Persister;

public interface FileParser
{
	/**
	 * (non-javadoc)
	 * @see Job#getIdentifier()
	 */
	String getIdentifier();
	
	boolean ensureRequiredFileArePresent(File[] input);
	
	void importFiles(File[] input, Persister persister) throws Exception;
	
	/**
	 * (non-javadoc)
	 * @see Job#getHumanName()
	 */
	String getHumanName();
}
