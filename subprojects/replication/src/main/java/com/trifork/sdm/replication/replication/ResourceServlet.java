package com.trifork.sdm.replication.replication;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.trifork.sdm.replication.db.DatabaseModule.QueryFactory;
import com.trifork.sdm.replication.db.Query;
import com.trifork.sdm.replication.util.URLFactory;
import com.trifork.stamdata.*;


@Singleton
public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	private final QueryFactory queryFactory;

	private final URLFactory urlFactory;


	@Inject
	ResourceServlet(QueryFactory queryFactory, URLFactory urlFactory) {

		this.queryFactory = queryFactory;
		this.urlFactory = urlFactory;
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Fetch the query parameters.

		String sinceParam = request.getParameter(ParameterName.HISTORY_ID);

		Date sinceDate;
		long sinceId;

		if (sinceParam != null) {
			
			// We don't know exactly how long the token string is so we have the
			// PID offset on the strings' lengths.
			
			String sinceDateStr = sinceParam.substring(0, sinceParam.length() - 10);
			sinceDate = new Date(Long.parseLong(sinceDateStr) * 1000);

			String sinceIdStr = sinceParam.substring(sinceParam.length() - 9);
			sinceId = Long.parseLong(sinceIdStr);
		}
		else {
			sinceDate = DateUtils.PAST;
			sinceId = 0;
		}

		// Figure out which resource has been requested.

		String resourceName = request.getParameter("type");
		Class<? extends Record> resourceType = EntityHelper.getResourceByName(resourceName);

		// Determine the output format.

		String formatParam = request.getParameter(ParameterName.FORMAT);
		OutputFormat format = OutputFormat.XML;

		if (formatParam != null) {

			if (formatParam.equals(OutputFormat.XML.name())) {

				format = OutputFormat.XML;
			}
			else if (formatParam.equals(OutputFormat.FastInfoset.name())) {

				format = OutputFormat.FastInfoset;
			}
			else {

				response.sendError(400, "Unsupported output format requested.");
				return;
			}
		}
		
		// Determine the page size.
		
		String pageSizeParam;
		int pageSize;
		
		if ((pageSizeParam = request.getParameter(ParameterName.PAGE_SIZE)) != null) {
			
			try {
				pageSize = Integer.parseInt(pageSizeParam);
				
				if (pageSize <= 0) throw new Exception();
			}
			catch (Throwable t) {
				response.sendError(400, "Invalid pageSize parameter.");
				return;
			}
		}
		else {
			
			pageSize = 2000;
		}

		EntitySerializer writer = new XMLEntitySerializer(resourceType, urlFactory);

		// Construct a query.

		Query query = queryFactory.create(resourceType, sinceId, sinceDate, pageSize);

		// Return the resulting records.

		try {
			writer.output(query, response.getOutputStream(), format, pageSize);
		}
		catch (Exception e) {

			throw new ServletException(e);
		}
	}
}
