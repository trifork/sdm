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
