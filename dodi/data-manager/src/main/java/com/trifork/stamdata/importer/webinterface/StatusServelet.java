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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trifork.stamdata.importer.jobs.JobManager;

/**
 * A REST Servlet that lets you monitor the data-manager with HTTP calls.
 * 
 * @author Thomas Børlum <thb@trofork.com>
 */
@Singleton
public class StatusServelet extends HttpServlet
{
	private static final int HTTP_INTERNAL_SERVER_ERROR = 500;
	
	private final DatabaseStatus dbChecker;
	private final JobManager jobManager;

	@Inject
	StatusServelet(final DatabaseStatus dbChecker, final JobManager jobManager)
	{
		this.dbChecker = dbChecker;
		this.jobManager = jobManager;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if (!dbChecker.isAlive())
		{
			resp.sendError(HTTP_INTERNAL_SERVER_ERROR, "Could not connect to the database. See the log for details.");
		}
		else if (!jobManager.areAllJobsRunning())
		{
			resp.sendError(HTTP_INTERNAL_SERVER_ERROR, "One or more job runners have failed. See stamdata's GUI and log for details. The application must be restarted after correcting the error.");
		}
		else if (jobManager.areAnyJobsOverdue())
		{
			resp.sendError(HTTP_INTERNAL_SERVER_ERROR, "One or more file parsers are overdue. See stamdata's GUI for more information.");
		}
		else
		{
			resp.getWriter().print("200 OK");
		}
	}

	private static final long serialVersionUID = 0L;
}
