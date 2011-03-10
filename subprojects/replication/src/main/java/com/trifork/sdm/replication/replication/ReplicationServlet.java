package com.trifork.sdm.replication.replication;


import static com.trifork.sdm.replication.replication.URLParameters.*;
import static java.net.HttpURLConnection.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.Logger;

import com.google.inject.*;
import com.trifork.sdm.replication.replication.properties.Routes;
import com.trifork.stamdata.Record;


@Singleton
public class ReplicationServlet extends HttpServlet
{
	private static final Logger LOG = getLogger(ReplicationServlet.class);

	private static final int HISTORY_ID_SEGMENT_LENGTH = 10;
	private static final long serialVersionUID = -172563300590543180L;
	private static final int SECS_TO_MILLIS = 1000;

	@Inject
	protected Provider<EntityWriter> writerProvider;

	@Inject
	@Routes
	protected Map<String, Class<? extends Record>> routes;


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

			String sinceDateParam = historyIdParam.substring(0, historyIdParam.length() - HISTORY_ID_SEGMENT_LENGTH);
			Date sinceDate = new Date(Long.parseLong(sinceDateParam) * SECS_TO_MILLIS);

			String sinceIdParam = historyIdParam.substring(historyIdParam.length() - HISTORY_ID_SEGMENT_LENGTH);
			long sinceId = Long.parseLong(sinceIdParam);

			// Determine the output format.

			String formatParam = request.getParameter(FORMAT);
			OutputFormat format = OutputFormat.valueOf(formatParam);

			// Determine the page size.

			String pageSizeParam = request.getParameter(PAGE_SIZE);
			int pageSize = Integer.parseInt(pageSizeParam);

			// Determine the resource type.

			String entityName = request.getParameter(ENTITY_TYPE);
			Class<? extends Record> resourceType = routes.get(entityName);

			// Construct a query using the request parameters.

			EntityWriter writer = writerProvider.get();

			writer.write(outputStream, resourceType, format, pageSize, sinceDate, sinceId);
		}
		catch (Exception e)
		{
			String message = "Unhandled exception was thrown during replication.";
			LOG.error(message, e);
			response.sendError(HTTP_INTERNAL_ERROR, message);
		}
	}
}
