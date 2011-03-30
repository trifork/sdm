package com.trifork.stamdata.replication.replication;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.ScrollableResults;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.replication.replication.annotations.Registry;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.security.SecurityManager;


@Singleton
public class RegistryServlet extends HttpServlet {

	private static final long serialVersionUID = -172563300590543180L;
	private static final int DEFAULT_PAGE_SIZE = 10000;

	private final Map<String, Class<? extends View>> registry;

	private final Provider<SecurityManager> securityManager;
	private final Provider<RecordDao> recordDao;

	private final Provider<AtomFeedWriter> writers;

	@Inject
	RegistryServlet(@Registry Map<String, Class<? extends View>> registry, Provider<SecurityManager> securityManager, Provider<RecordDao> recordDao, Provider<AtomFeedWriter> writers) {

		this.registry = checkNotNull(registry);
		this.recordDao = checkNotNull(recordDao);
		this.writers = checkNotNull(writers);
		this.securityManager = checkNotNull(securityManager);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// AUTHORIZE THE REQUEST
		//
		// The HTTP RFC specifies that if returning a 401 status
		// the server MUST issue a challenge also.
		//
		// TODO: Log any unauthorized attempts.

		if (!securityManager.get().authorize(request)) {
			
			response.setStatus(401);
			response.setHeader("WWW-Authenticate", "STAMDATA");
			return;
		}

		// HANDLE REQUEST PARAMETERS
		//
		// The URL's path represents the requested view's name,
		// e.g. dkma/drug/v1 or sor/sygehus/v2.

		String viewName = getPath(request);

		// Parse the offset query parameters.
		HistoryOffset offset = new HistoryOffset(request.getParameter("offset"));

		String countParam = request.getParameter("count");
		int count = DEFAULT_PAGE_SIZE;

		if (countParam != null) {
			try {
				count = Integer.parseInt(countParam);
			}
			catch (NumberFormatException e) {
				// Ignore this. We might decide to log this in the future.
			}
		}

		// Determine what content type the client wants.
		//
		// TODO: Use javax.activation.MimeType to check these.

		String accept = request.getHeader("Accept");
		boolean useFastInfoSet = (MIME.ATOM_FASTINFOSET.equalsIgnoreCase(accept));

		// FETCH THE RECORDS

		Class<? extends View> entityType = registry.get(viewName);

		ScrollableResults records = recordDao.get().findPage(entityType, offset.getRecordID(), offset.getModifiedDate(), count);

		// WRITE STATUS & HEADERS
		//
		// The response contains a content-type header and a Link header that
		// points to the next page. Web linking is described in RFC 5988.
		//
		// If there are no more records, no Link header will be returned.
		// This is how the client knows when there are no more updates.
		//
		// Returns 304 Not modified when there are no more updates.
		//
		// TODO: Check if scrolling to the last record is too inefficient,
		// and maybe an additional query would be faster.
		
		int status;

		if (records.last()) {
			status = 200;
			View newestRecord = (View)records.get(0);
			response.addHeader("Link", WebLinking.createNextLink(viewName, newestRecord.getOffset()));
			records.beforeFirst();
		}
		else {
			status = 304;
		}

		response.setStatus(status);
		String contentType = useFastInfoSet ? MIME.ATOM_FASTINFOSET : MIME.ATOM_XML;
		contentType += "; charset=utf-8";
		response.setContentType(contentType);
		response.flushBuffer();

		// WRITE RESPONSE CONTENT IF ANY

		if (status == 200) {
			writers.get().write(viewName, records, response.getOutputStream(), useFastInfoSet);
		}
	}

	protected String getPath(HttpServletRequest request) {

		return request.getPathInfo().substring(1);
	}
}
