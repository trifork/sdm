// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.commons.scheduler.impl.QuartzScheduler;

import com.google.inject.Inject;


/**
 * FileSpooler. Initiates and monitor file spoolers.
 * 
 * @author Jan Buchholdt
 * @author Thomas Børlum
 */
public class JobManager
{
	private final List<Job> jobs;
	private final Scheduler scheduler;

	@Inject
	JobManager(List<Job> jobs)
	{
		// Clone the set of jobs.

		this.jobs = Collections.synchronizedList(new ArrayList<Job>(jobs));

		// Create a scheduler.

		this.scheduler = new QuartzScheduler();
	}

	public void start() throws Exception
	{
		// To avoid problems with concurrency, we restrict
		// jobs to be run in serial. This is not a problem
		// since performace is not an critical at the moment.

		final boolean RUN_CUNCURRENTLY = false;

		// Schedule all the jobs.

		for (Job job : jobs)
		{
			scheduler.addJob(job.getIdentifier(), job, null, job.getSchedule(), RUN_CUNCURRENTLY);
		}
	}

	public void stop()
	{
		for (Job job : jobs)
		{
			scheduler.removeJob(job.getIdentifier());
		}
	}
}
