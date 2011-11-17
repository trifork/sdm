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
import com.trifork.stamdata.importer.parsers.ParserScheduler;

public class DataManagerComponentMonitor implements ComponentMonitor
{
	private final ConnectionManager dbChecker;
    private final JobsDecorator jobs;
    private final ParserScheduler scheduler;

    @Inject
	DataManagerComponentMonitor(ConnectionManager dbChecker, JobsDecorator jobs, ParserScheduler scheduler)
	{
		this.dbChecker = dbChecker;
        this.jobs = jobs;
        this.scheduler = scheduler;
    }
	
	@Override
    public boolean isOk()
	{
		return dbChecker.isAvailable()
            && jobs.areAllJobsRunning()
            && !jobs.areAnyJobsOverdue()
            && scheduler.isOk();
	}
}
