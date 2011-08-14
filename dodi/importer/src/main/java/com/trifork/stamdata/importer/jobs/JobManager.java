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

package com.trifork.stamdata.importer.jobs;

import java.util.*;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import com.google.common.collect.Sets;
import com.google.inject.*;
import com.trifork.stamdata.importer.persistence.ConnectionPool;


public class JobManager
{
	private static final boolean WAIT_FOR_JOBS_TO_COMPLETE = true;
	private static final String DATA_MAP_OBJECT = "QuartzJobScheduler.Object";

	private Scheduler scheduler;

	private final Set<FileParserJob> parsers;
	private Set<Executer> executers;
	private final ConnectionPool connectionPool;

	@Inject
	JobManager(Set<FileParserJob> parsers, ConnectionPool connectionPool)
	{
		this.parsers = parsers;
		this.connectionPool = connectionPool;
	}

	public void start() throws Exception
	{
		if (scheduler == null)
		{
			// Create a scheduler.

			scheduler = StdSchedulerFactory.getDefaultScheduler();

			// To avoid problems with concurrency, we restrict
			// jobs to be run in serial. This is not a problem
			// since performance is not an critical at the moment.
			//
			// See quartz.properties.

			// Schedule all the jobs.

			executers = Sets.newHashSet();

			CronScheduleBuilder everyFiveSeconds = CronScheduleBuilder.cronSchedule("0/5 * * * * ?");

			for (FileParserJob job : parsers)
			{
				Executer executer = new FileParserJobExecuter(job, connectionPool);

				Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(everyFiveSeconds).build();
				JobDataMap jobDataMap = initDataMap(job.getIdentifier(), executer);
				JobDetail jobDetail = createJobDetail(job.getIdentifier(), jobDataMap);
				scheduler.scheduleJob(jobDetail, trigger);

				executers.add(executer);
			}
		}

		scheduler.start();
	}

	public void stop() throws Exception
	{
		executers.clear();
		scheduler.shutdown(WAIT_FOR_JOBS_TO_COMPLETE);
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
			Executer job = (Executer) context.getMergedJobDataMap().get(DATA_MAP_OBJECT);
			job.run();
		}
	}

	public boolean areAllJobsRunning()
	{
		for (Executer job : executers)
		{
			if (!job.isOK())
			{
				return false;
			}
		}

		return true;
	}

	public boolean areAnyJobsOverdue()
	{
		for (Executer job : executers)
		{
			if (job.isOverdue())
			{
				return true;
			}
		}

		return false;
	}

	public Iterator<Executer> getJobIterator()
	{
		return executers.iterator();
	}
}
