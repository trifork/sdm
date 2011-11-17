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

import org.joda.time.DateTime;

@Deprecated
public interface Job extends Runnable
{
	/**
	 * A unique string that identifies the job.
	 * 
	 * This value is used to keep track of when the importer
	 * was last run. The identifier must also be all ascii alphanumeric.
	 * 
	 * @return a non empty string.
	 */
	String identifier();

	/**
	 * A human readable name.
	 * 
	 * This text is displayed in the GUI.
	 *  
	 * E.g. CPR Parser.
	 * 
	 * @return a none empty string.
	 */
	String getHumanName();

	/**
	 * Indicated the whether the job is OK or in an error state.
	 * 
	 * @return false if the job is in an error state.
	 */
	boolean isOK();
	
	/**
	 * Indicated weather a job has been run before its latest deadline.
	 * 
	 * @return true if the job is late.
	 */
	boolean isOverdue();
	
	/**
	 * The timestamp when the import job was last successfully completed.
	 * 
	 * @return null if the parser has never been run before.
	 */
	DateTime getLatestRunTime();
	
	/**
	 * Indicated whether this job has ever previously.
	 * 
	 * @return true if have ever been completed been completed successfully.
	 */
	public boolean hasBeenRun();
	
	/**
	 * Indicates if the job is currently executing.
	 * 
	 * @return true if the job is executing.
	 */
	boolean isExecuting();
}
