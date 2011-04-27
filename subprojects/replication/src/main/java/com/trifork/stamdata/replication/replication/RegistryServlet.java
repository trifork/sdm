// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

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
import com.trifork.stamdata.HistoryOffset;
import com.trifork.stamdata.UsageLogged;
import com.trifork.stamdata.replication.replication.annotations.Registry;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.security.SecurityManager;
import com.trifork.stamdata.replication.usagelog.UsageLogger;


@Singleton
public class RegistryServlet extends HttpServlet {

	private static final long serialVersionUID = -172563300590543180L;
	private static final int DEFAULT_PAGE_SIZE = 10000;

	private final Map<String, Class<? extends View>> registry;

	private final Provider<SecurityManager> securityManager;
	private final Provider<RecordDao> recordDao;

	private final Provider<AtomFeedWriter> writers;
	private final Provider<UsageLogger> usageLogger;

	@Inject
	RegistryServlet(@Registry Map<String, Class<? extends View>> registry,
			Provider<UsageLogger> usageLogger,
			Provider<SecurityManager> securityManager,
			Provider<RecordDao> recordDao,
			Provider<AtomFeedWriter> writers) {
		this.usageLogger = usageLogger;
		this.registry = checkNotNull(registry);
		this.recordDao = checkNotNull(recordDao);
		this.writers = checkNotNull(writers);
		this.securityManager = checkNotNull(securityManager);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (isNotAuthorized(request)) {
			setUnauthorizedHeaders(response);
			return;
		}

		String viewName = getViewName(request);
		String clientId = securityManager.get().getClientId(request);

		// Parse the offset query parameters.
		HistoryOffset offset = new HistoryOffset(request.getParameter("offset"));

		int count = getCount(request);

		// Determine what content type the client wants.
		//
		// TODO: Use javax.activation.MimeType to check these.

		String accept = request.getHeader("Accept");
		boolean useFastInfoSet = (MIME.ATOM_FASTINFOSET.equalsIgnoreCase(accept));

		// FETCH THE RECORDS

		Class<? extends View> entityType = registry.get(viewName);

		ScrollableResults records = recordDao.get().findPage(entityType, offset.getRecordID(), offset.getModifiedDate(), clientId, count);

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

		if (records.last()) {
			View newestRecord = (View) records.get(0);
			response.addHeader("Link", WebLinking.createNextLink(viewName, newestRecord.getOffset()));
		}
		records.beforeFirst();

		response.setStatus(HTTP_OK);
		String contentType = useFastInfoSet ? MIME.ATOM_FASTINFOSET : MIME.ATOM_XML;
		contentType += "; charset=utf-8";
		response.setContentType(contentType);
		response.flushBuffer();

		int writtenRecords = writers.get().write(entityType, records, response.getOutputStream(), useFastInfoSet);
		if (shouldBeLogged(entityType)) {
			usageLogger.get().log(clientId, viewName, writtenRecords);
		}

		records.close();
	}

	private boolean shouldBeLogged(Class<? extends View> entityType) {
		UsageLogged usageLogged = entityType.getAnnotation(UsageLogged.class);
		return usageLogged == null || usageLogged.value();
	}

	private boolean isNotAuthorized(HttpServletRequest request) {
		// AUTHORIZE THE REQUEST
		//
		// The HTTP RFC specifies that if returning a 401 status
		// the server MUST issue a challenge also.
		//
		// TODO: Log any unauthorized attempts.

		return !securityManager.get().isAuthorized(request);
	}

	private void setUnauthorizedHeaders(HttpServletResponse response) {
		response.setStatus(HTTP_UNAUTHORIZED);
		response.setHeader("WWW-Authenticate", "STAMDATA");
	}

	private int getCount(HttpServletRequest request) {
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
		return count;
	}

	protected String getViewName(HttpServletRequest request) {
		// The URL's path represents the requested view's name,
		// e.g. dkma/drug/v1 or sor/sygehus/v2.
		return request.getPathInfo().substring(1);
	}
}
