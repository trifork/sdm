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
package com.trifork.stamdata.importer.webinterface;

import com.google.inject.Inject;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.jobs.JobManager;
import com.trifork.stamdata.importer.parsers.ParserScheduler;
import com.trifork.stamdata.importer.parsers.ParserState;

import java.util.Set;

/**
 * @author Thomas Børlum <thb@trifork.com>
 */
public class DataManagerComponentMonitor implements ComponentMonitor
{
	private final ConnectionManager connectionManager;
    private final Set<ParserState> parsers;
    private final ParserScheduler scheduler;
    private final JobManager manager;

    @Inject
	DataManagerComponentMonitor(ConnectionManager connectionManager, Set<ParserState> parsers, ParserScheduler scheduler, JobManager manager)
	{
		this.connectionManager = connectionManager;
        this.parsers = parsers;
        this.scheduler = scheduler;
        this.manager = manager;
    }
	
	@Override
    public boolean isOk()
	{
		return connectionManager.isAvailable()
            && areAllJobsRunning()
            && !areAnyJobsOverdue()
            && scheduler.isOk()
            && manager.isOk();
	}

    public boolean areAnyJobsOverdue()
    {
        for (ParserState job : parsers)
        {
            if (job.isOverdue()) return true;
        }

        return false;
    }

    public boolean areAllJobsRunning()
    {
        for (ParserState job : parsers)
        {
            if (job.isLocked()) return false;
        }

        return true;
    }

    public Iterable<ParserState> getJobs()
    {
        return parsers;
    }

    public boolean isDatabaseAvailable()
    {
        return connectionManager.isAvailable();
    }
}
