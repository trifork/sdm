package com.trifork.sdm.replication.replication;

import static org.slf4j.LoggerFactory.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;

import com.google.inject.*;
import com.sun.xml.internal.fastinfoset.stax.factory.StAXOutputFactory;
import com.trifork.sdm.replication.gui.models.PermissionDao;
import com.trifork.sdm.replication.replication.annotations.Registry;
import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.*;

@Singleton
public class RegistryServlet extends HttpServlet {

	private static final long serialVersionUID = -172563300590543180L;

	private static final int DEFAULT_PAGE_SIZE = 10000;
	private static final Logger logger = getLogger(RegistryServlet.class);

	private Map<String, Class<? extends Record>> registry;

	private final AuthorizationManager authorizationManager;
	private final PermissionDao permissions;

	private final RecordDao recordDao;

	private final Provider<AtomFeedWriter> writers;


	@Inject
	RegistryServlet(@Registry Map<String, Class<? extends Record>> registry, AuthorizationManager authorizationManager, PermissionDao permissions, RecordDao recordDao, Provider<AtomFeedWriter> writers) {

		this.registry = registry;
		this.authorizationManager = authorizationManager;
		this.permissions = permissions;
		this.recordDao = recordDao;
		this.writers = writers;
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// HANDLE AUTHORIZATION
		//

		// if (!isAuthorized(request, response)) return;


		// HANDLE REQUEST PARAMETERS
		//

		// The URL's path represents the entity's name.
		// E.g. dkma/drug/v1 or sor/sygehus/v2.

		String entityName = getPath(request);

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

		String accept = request.getHeader("Accept");
		boolean useFastInfoSet = (MIME.ATOM_FASTINFOSET.equalsIgnoreCase(accept));


		// FETCH THE RECORDS
		//
		// Currently we need the last (newest) record for some meta data.
		// TODO: This is quite inefficient. Traverse the list in reverse instead.

		Class<? extends Record> entityType = registry.get(entityName);

		List<? extends Record> records = recordDao.find(entityType, offset, count);
		Record newestRecord = records.get(records.size() - 1);


		// WRITE RESPONSE
		//
		// The response contains a content-type header and a Link header that
		// points to the next page. Web linking is described in RFC 5988.

		response.addHeader("Link", WebLinking.createNextLink(entityName, newestRecord.getOffset()));
		response.addHeader("Content-Type", useFastInfoSet ? MIME.ATOM_FASTINFOSET : MIME.ATOM_XML);
		response.flushBuffer();

		try {
			XMLStreamWriter writer;

			if (useFastInfoSet)
				writer = StAXOutputFactory.newInstance().createXMLStreamWriter(response.getOutputStream());
			else
				writer = XMLOutputFactory.newInstance().createXMLStreamWriter(response.getOutputStream());

			writers.get().write(entityName, records, writer);
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}


	protected boolean isAuthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Fetch the raw authorization token from the authorization header.

		Authorization authorization = Authorization.parse(request.getHeader("Authorization"));

		if (authorization == null) {
			// TODO: We should actually issue a HTTP auth challenge here.
			response.sendError(401, "Please use the STAMDATA HTTP authorization scheme with a token that is valid in time.");
			return false;
		}

		String path = getPath(request);

		if (!authorizationManager.validate(path, authorization)) {
			response.sendError(401, "Unauthorized access attempt. The provided authorization was not valid.");
			return false;
		}

		// Make sure the client is authorized to the requested entity.

		try {
			return permissions.canAccessEntity(authorization.getCvrNumber(), path);
		}
		catch (Exception e) {
			logger.error("An error occured while fetching permissions from the database.", e);
		}

		return false;
	}


	protected String getPath(HttpServletRequest request) {

		return request.getPathInfo().substring(1);
	}
}
