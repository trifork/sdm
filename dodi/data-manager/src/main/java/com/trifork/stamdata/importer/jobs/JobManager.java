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

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.Inject;


public class JobManager
{
	private static final String DATA_MAP_OBJECT = "QuartzJobScheduler.Object";

	private final List<Job> jobs;
	private Scheduler scheduler;

	@Inject
	JobManager(List<Job> jobs) throws Exception
	{
		// Clone the set of jobs.

		this.jobs = Collections.synchronizedList(new ArrayList<Job>(jobs));
	}

	public void start() throws Exception
	{
		if (scheduler == null)
		{
			// Create a scheduler.

			scheduler = StdSchedulerFactory.getDefaultScheduler();

			// To avoid problems with concurrency, we restrict
			// jobs to be run in serial. This is not a problem
			// since performace is not an critical at the moment.
			//
			// See quartz.properties.

			// Schedule all the jobs.

			for (Job job : jobs)
			{
				final CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(job.getCronExpression());
				final Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(schedule).build();

				final JobDataMap jobDataMap = this.initDataMap(job.getIdentifier(), job);
				final JobDetail detail = this.createJobDetail(job.getIdentifier(), jobDataMap);

				scheduler.scheduleJob(detail, trigger);
			}
		}

		this.scheduler.start();
	}

	public void stop() throws Exception
	{
		this.scheduler.shutdown(true);
	}

	protected JobDataMap initDataMap(String jobName, Object job)
	{
		final JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put(DATA_MAP_OBJECT, job);

		return jobDataMap;
	}

	protected JobDetail createJobDetail(String name, JobDataMap jobDataMap)
	{
		return JobBuilder.newJob(QuartzJobExecutor.class).usingJobData(jobDataMap).build();
	}


	public static class QuartzJobExecutor implements org.quartz.Job
	{
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException
		{
			Job job = (Job) context.getMergedJobDataMap().get(DATA_MAP_OBJECT);

			job.run();
		}
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
