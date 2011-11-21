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

import com.google.inject.Inject;
import com.trifork.stamdata.importer.parsers.annotations.InboxRootPath;

import javax.inject.Named;
import java.io.File;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Deprecated // Use the ParserScheduler instead.
public class JobManager
{
    private final Set<FileParserJob> jobs;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Inject
	JobManager(Set<FileParserJob> jobs) throws Exception
	{
        this.jobs = jobs;
    }

	public void start() throws Exception
	{
		// To avoid problems with concurrency, we restrict
		// jobs to be run in serial. This is not a problem
		// since performance is not an critical at the moment.

		// Schedule all the jobs.

        for (FileParserJob job : jobs)
        {
			executor.scheduleWithFixedDelay(job, 0, 1, TimeUnit.SECONDS);
		}
	}

	public void stop() throws Exception
	{
		executor.shutdownNow();
	}

    public boolean isOk()
    {
        return !executor.isTerminated();
    }
}
