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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;


public class JobManager
{
	private final List<Job> jobs;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Inject
	JobManager(List<Job> jobs) throws Exception
	{
		// Clone the set of jobs.

		this.jobs = Collections.synchronizedList(new ArrayList<Job>(jobs));
	}

	public void start() throws Exception
	{
		// To avoid problems with concurrency, we restrict
		// jobs to be run in serial. This is not a problem
		// since performance is not an critical at the moment.

		// Schedule all the jobs.

		for (Job job : jobs)
		{
			executor.scheduleWithFixedDelay(job, 0, 1, TimeUnit.SECONDS);
		}
	}

	public void stop() throws Exception
	{
		executor.shutdownNow();
	}

	public boolean areAllJobsRunning()
	{
		for (Job job : jobs)
		{
			if (!job.isOK()) return false;
		}

		return true;
	}

	public boolean areAnyJobsOverdue()
	{
		for (Job job : jobs)
		{
			if (job.isOverdue()) return true;
		}

		return false;
	}

	public Iterator<Job> getJobIterator()
	{
		return jobs.iterator();
	}
}
