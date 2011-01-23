package com.trifork.stamdata.importer.jobs;


import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;


public class JobManager implements Runnable
{
	private final Scheduler scheduler = new Scheduler();


	public JobManager()
	{
	}


	public void run()
	{
		if (scheduler.isStarted()) return;
		
		// We do not want the worker threads to keep us from stopping the
		// JVM, so we run them as daemon threads.

		scheduler.setDaemon(true);

		// Start the scheduler async.

		scheduler.start();
	}


	public void add(final Runnable job, String schedule)
	{
		SchedulingPattern pattern = new SchedulingPattern(schedule);
		
		scheduler.schedule(pattern, new Task()
		{
			@Override
			public void execute(TaskExecutionContext context) throws RuntimeException
			{
				job.run();
			}
		});
	}
}
