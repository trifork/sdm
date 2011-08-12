package com.trifork.stamdata.importer.jobs;

public interface Job
{
	/**
	 * The identifier is used for two things.
	 * 
	 * 1. To keep track of the time when a job was last run. 2. To display in
	 * the GUI.
	 * 
	 * Once set it is important that it is never changed. Therefore you should
	 * not use the class name, as it is subject to change.
	 */
	String getIdentifier();

	/**
	 * (non-javadoc)
	 * 
	 * @see Executer#getHumanName()
	 */
	String getHumanName();
}
