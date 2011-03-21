package dk.trifork.sdm.jobspooler;

import dk.trifork.sdm.jobspooler.exceptions.JobRunnerException;

public interface JobExecutor {
	public void run() throws JobRunnerException;

}
