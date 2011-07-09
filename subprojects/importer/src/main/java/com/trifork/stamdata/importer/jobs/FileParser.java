package com.trifork.stamdata.importer.jobs;

import java.io.File;

import com.trifork.stamdata.importer.persistence.AuditingPersister;

public interface FileParser
{
	/**
	 * (non-javadoc)
	 * @see Job#getIdentifier()
	 */
	String getIdentifier();
	
	boolean ensureRequiredFileArePresent(File[] input);
	
	void importFiles(File[] input, AuditingPersister persister) throws Exception;
	
	/**
	 * (non-javadoc)
	 * @see Job#getHumanName()
	 */
	String getHumanName();
}
