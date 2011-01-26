package com.trifork.sdm.replication.replication;


import static com.trifork.sdm.replication.replication.URLParameters.*;
import static java.net.HttpURLConnection.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.inject.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.Logger;

import com.trifork.stamdata.Record;


@Singleton
public class ResourceServlet extends HttpServlet
{
	private static final long serialVersionUID = -172563300590543180L;

	private static final Logger LOGGER = getLogger(ResourceServlet.class);

	private static final int MILLIS_TO_SECS = 1000;

	private final Provider<EntityWriter> writerProvider;

	private final ResourceResolver resourceResolver;


	@Inject
	ResourceServlet(Provider<EntityWriter> writerProvider, ResourceResolver resourceResolver)
	{
		this.writerProvider = writerProvider;
		this.resourceResolver = resourceResolver;
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// We do not have to validate the request parameters here. That is the
		// gateway's responsibility. The integrity of the URL is checked before,
		// we get this far.

		try
		{
			OutputStream outputStream = response.getOutputStream();

			// Fetch the query parameters.

			String historyIdParam = request.getParameter(HISTORY_ID);

			// We don't know exactly how long the token string is so we have the
			// PID offset on the strings' lengths.

			// FIXME: Magic numbers.

			String sinceDateParam = historyIdParam.substring(0, historyIdParam.length() - 10);
			Date sinceDate = new Date(Long.parseLong(sinceDateParam) * MILLIS_TO_SECS);

			String sinceIdParam = historyIdParam.substring(historyIdParam.length() - 9);
			long sinceId = Long.parseLong(sinceIdParam);

			// Determine the output format.

			String formatParam = request.getParameter(FORMAT);
			OutputFormat format = OutputFormat.valueOf(formatParam);

			// Determine the page size.

			String pageSizeParam = request.getParameter(PAGE_SIZE);
			int pageSize = Integer.parseInt(pageSizeParam);

			// Determine the resource type.

			String resourceName = request.getParameter(ENTITY_TYPE);
			Class<? extends Record> resourceType = resourceResolver.get(resourceName);

			// Construct a query using the request parameters.

			EntityWriter writer = writerProvider.get();

			writer.write(outputStream, resourceType, format, pageSize, sinceDate, sinceId);
		}
		catch (Exception e)
		{
			LOGGER.error("Unhandled exception was thrown during replication.", e);
			response.sendError(HTTP_INTERNAL_ERROR);
		}
	}
}
