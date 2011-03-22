package com.trifork.stamdata.replication.replication;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		// TODO: Log any unauthorized attempts.

		if (!securityManager.get().authorize(request)) {
			
			response.setStatus(401);
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
		//
		// Currently we need the newest record for some meta data.
		// HACK: This is quite inefficient. Traverse the list in reverse
		// instead.

		Class<? extends View> entityType = registry.get(viewName);

		List<? extends View> records = recordDao.get().findPage(entityType, offset.getRecordID(), offset.getModifiedDate(), count);

		View newestRecord = (records.isEmpty()) ? null : records.get(records.size() - 1);

		// WRITE STATUS & HEADERS
		//
		// The response contains a content-type header and a Link header that
		// points to the next page. Web linking is described in RFC 5988.
		//
		// If there are no more records, no Link header will be returned.
		// This is how the client knows when there are no more updates.
		
		response.setStatus(200);		
		if (records.size() == count) {
			response.addHeader("Link", WebLinking.createNextLink(viewName, newestRecord.getOffset()));
		}
		response.setContentType(useFastInfoSet ? MIME.ATOM_FASTINFOSET : MIME.ATOM_XML);
		response.flushBuffer();

		// WRITE RESPONSE CONTENT

		writers.get().write(viewName, records, response.getOutputStream(), useFastInfoSet);
	}

	protected String getPath(HttpServletRequest request) {

		return request.getPathInfo().substring(1);
	}
}
