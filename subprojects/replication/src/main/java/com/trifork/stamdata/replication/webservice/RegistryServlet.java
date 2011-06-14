
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.replication.webservice;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.ScrollableResults;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.HistoryOffset;
import com.trifork.stamdata.replication.security.SecurityManager;
import com.trifork.stamdata.replication.webservice.annotations.Registry;
import com.trifork.stamdata.views.View;


@Singleton
public class RegistryServlet extends HttpServlet {

	private static final Logger logger = getLogger(RegistryServlet.class);

	private static final long serialVersionUID = -172563300590543180L;
	public static final int DEFAULT_PAGE_SIZE = 5000;

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (isNotAuthorized(request)) {
			setUnauthorizedHeaders(response);
			return;
		}
		
		writeResponse(request, response);
	}

	private void writeResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if (!isLegalOffsetParam(request))
		{
			response.sendError(HTTP_BAD_REQUEST, "The 'offset' parameter must be a non-negative integer.");
			logger.warn("Invalid parameter offset='{}'. ClientId='{}'.", getOffsetParam(request), securityManager.get().getClientId(request));
			return;
		}

		if (!isLegalCountParam(request))
		{
			response.sendError(HTTP_BAD_REQUEST, "The 'count' parameter must be a positive integer.");
			logger.warn("Invalid parameter count='{}'. ClientId='{}'.", getCountParam(request), securityManager.get().getClientId(request));
			return;
		}

		fetchAndWriteRecords(request, response);
	}

	private void fetchAndWriteRecords(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		HistoryOffset offset = createHistoryOffset(request);
		int count = parseCountParam(request);
		Class<? extends View> entityType = registry.get(getViewName(request));

		ScrollableResults records = recordDao.get().findPage(entityType, offset.getRecordID(), offset.getModifiedDate(), count);

		try
		{
			writeRecords(request, response, entityType, records);
		}
		finally
		{
			records.close();
		}
	}

	private int writeRecords(HttpServletRequest request, HttpServletResponse response, Class<? extends View> entityType, ScrollableResults records) throws IOException
	{	
		response.setStatus(HTTP_OK);
		response.setContentType(getContentType(request));
		response.flushBuffer();

		return writers.get().write(entityType, records, response.getOutputStream(), useFastInfoset(request));
	}
	
	private String getContentType(HttpServletRequest request)
	{
		String contentType = useFastInfoset(request) ? MIME.ATOM_FASTINFOSET : MIME.ATOM_XML;
		return contentType + "; charset=utf-8";
	}

	private boolean useFastInfoset(HttpServletRequest request)
	{
		// Determine what content type the client wants.
		//
		// TODO: Use javax.activation.MimeType to check these.

		String accept = request.getHeader("Accept");
		return (MIME.ATOM_FASTINFOSET.equalsIgnoreCase(accept));
	}

	private String getCountParam(HttpServletRequest request)
	{
		return request.getParameter("count");
	}

	private String getOffsetParam(HttpServletRequest request)
	{
		return request.getParameter("offset");
	}

	private boolean isLegalOffsetParam(HttpServletRequest request)
	{
		String offsetParam = getOffsetParam(request);
		return offsetParam == null || offsetParam.matches("[0-9]+");
	}
	
	private boolean isLegalCountParam(HttpServletRequest request)
	{
		String countParam = getCountParam(request);
		return countParam == null || countParam.matches("[1-9][0-9]*");
	}

	private HistoryOffset createHistoryOffset(HttpServletRequest request) {
		return new HistoryOffset(getOffsetParam(request));
	}

	private int parseCountParam(HttpServletRequest request) {
		String countParam = getCountParam(request);
		return countParam != null ? Integer.parseInt(countParam) : DEFAULT_PAGE_SIZE;
	}

	private boolean isNotAuthorized(HttpServletRequest request) {
		return !securityManager.get().isAuthorized(request);
	}

	private void setUnauthorizedHeaders(HttpServletResponse response)
	{
		// The HTTP RFC specifies that if returning a 401 status
		// the server MUST issue a challenge also.

		response.setStatus(HTTP_UNAUTHORIZED);
		response.setHeader("WWW-Authenticate", "STAMDATA");
	}

	protected String getViewName(HttpServletRequest request)
	{
		// The URL's path represents the requested view's name,
		// e.g. dkma/drug/v1 or sor/sygehus/v2.
		return request.getPathInfo().substring(1);
	}
}
