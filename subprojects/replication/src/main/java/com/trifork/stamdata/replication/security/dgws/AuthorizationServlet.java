
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

package com.trifork.stamdata.replication.security.dgws;

import static com.google.common.base.Preconditions.checkNotNull;
import static dk.sosi.seal.model.constants.DGWSConstants.VERSION_1_0_1;
import static dk.sosi.seal.model.constants.FaultCodeValues.EXPIRED_IDCARD;
import static dk.sosi.seal.model.constants.FaultCodeValues.NOT_AUTHORIZED;
import static dk.sosi.seal.model.constants.FaultCodeValues.PROCESSING_PROBLEM;
import static dk.sosi.seal.model.constants.FaultCodeValues.SECURITY_LEVEL_FAILED;
import static dk.sosi.seal.model.constants.FaultCodeValues.SYNTAX_ERROR;
import static dk.sosi.seal.model.constants.FlowStatusValues.FLOW_FINALIZED_SUCCESFULLY;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.replication.logging.AuditLogger;
import com.trifork.stamdata.replication.webservice.annotations.Registry;
import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.Views;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.FaultCodeValues;
import dk.sosi.seal.xml.XmlUtil;


/**
 * A servlet that handles authorization requests with DGWS.
 * 
 * Responsibilities:
 * 
 * <ul>
 * <li>Validate SOAP requests.</li>
 * <li>Handles all aspects of deserialization and error handling with respect to DGWS.</li>
 * <li>Issue authorization tokens to authorized clients using the security manager.</li>
 * </ul>
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
@Singleton
public class AuthorizationServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationServlet.class);

	private static final long serialVersionUID = 8476350912985820545L;

	private static final int SOAP_OK = 200;
	private static final int SOAP_FAULT = 500;
	private static final String SOAP_CONTENT_TYPE = "application/soap+xml; charset=UTF-8";

	private final Provider<SOSIFactory> factories;

	private final Provider<RequestProcessor> processors;

	@Inject
	AuthorizationServlet(Provider<RequestProcessor> processors, Provider<SOSIFactory> factories) throws JAXBException {

		// ALL DEPENDENCIES MUST BE THREAD SAFE
		//
		// I doubt that the SOSI factory is thread safe to it is added
		// as a provider, and each thread constructs its own.

		this.processors = checkNotNull(processors);
		this.factories = checkNotNull(factories);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// READ THE REQUEST
		
		String content = IOUtils.toString(request.getInputStream(), "UTF-8");

		SOSIFactory factory = factories.get();

		Reply reply;

		try {
			Request soap = factory.deserializeRequest(content);
			
			// PROCESS IT
			
			reply = processors.get().process(soap);
		}
		catch (RuntimeException e) {
			logger.error("Processing failed", e);
			reply = factory.createNewErrorReply(VERSION_1_0_1, null, null, SYNTAX_ERROR, "Unable to parse the request.");
		}
		
		// RESPOND TO IT

		response.setContentType(SOAP_CONTENT_TYPE);
		response.setStatus(reply.isFault() ? SOAP_FAULT : SOAP_OK);
		response.flushBuffer();

		response.getWriter().write(XmlUtil.node2String(reply.serialize2DOMDocument(), false, false));
	}


	/**
	 * This is a convenience class to make the servlet easier to test. Servlets
	 * are notoriously difficult to test.
	 */
	static class RequestProcessor {

		private static final String SERVER_SIDE_ERROR = "Server";
		private static final String CLIENT_SIDE_ERROR = "Client";

		private final Map<String, Class<? extends View>> registry;
		private final DGWSSecurityManager securityManager;
		private final AuditLogger audit;
		private final SOSIFactory factory;
		private final Marshaller marshaller;
		private final Unmarshaller unmarshaller;

		@Inject
		RequestProcessor(SOSIFactory factory, AuditLogger audit, DGWSSecurityManager securityManager, @Registry Map<String, Class<? extends View>> registry, Marshaller marshaller, Unmarshaller unmarshaller) throws JAXBException {

			this.marshaller = checkNotNull(marshaller);
			this.unmarshaller = checkNotNull(unmarshaller);
			this.factory = checkNotNull(factory);
			this.audit = checkNotNull(audit);
			this.securityManager = checkNotNull(securityManager);
			this.registry = checkNotNull(registry);
		}

		public Reply process(Request request) {

			try {
				// CLIENTS MUST USE DGWS 1.0.1
				//
				// We are not going to stop them from using other versions
				// but it is nice to have it logged if they do.

				if (!VERSION_1_0_1.equals(request.getDGWSVersion())) {

					return factory.createNewErrorReply(request, PROCESSING_PROBLEM, "Must use DGWS version 1.0.1.");
				}

				// AUTHENTICATE THE CLIENT
				//
				// Currently the service only supports VOCES access
				// and requires that the client identity themselves
				// with a cvr-number.

				IDCard card = request.getIDCard();

				if (!(card instanceof SystemIDCard)) {

					return factory.createNewErrorReply(request, SECURITY_LEVEL_FAILED, "Authentication Level of 3 or above is required.");
				}

				if (!card.isValidInTime()) {

					return factory.createNewErrorReply(request, EXPIRED_IDCARD, "The ID card has expired.");
				}

				SystemIDCard systemCard = (SystemIDCard) card;

				String cvr;

				if (systemCard.getSystemInfo().getCareProvider().getType().equals(CVR_NUMBER)) {
					cvr = systemCard.getSystemInfo().getCareProvider().getID();
				}
				else {
					return factory.createNewErrorReply(request, NOT_AUTHORIZED, "You have to provide a CVR as identification.");
				}

				// PARSE THE SOAP BODY
				//
				// The body contains the name of the requested view.
				// This will be used to authorize the client.

				AuthorizationRequestStructure requestBody = unmarshaller.unmarshal(request.getBody(), AuthorizationRequestStructure.class).getValue();

				if (requestBody == null || requestBody.getView() == null) {

					return factory.createNewErrorReply(request, FaultCodeValues.PROCESSING_PROBLEM, "A request must contain a valid 'entity' parameter.");
				}

				// LOG REQUEST
				//
				// TODO: Somehow we should consolidate the audit log and
				// the error log.

				audit.log("Access request: clientCvr=%s, entityUri=%s", cvr, requestBody.getView());

				// CHECK THAT THE REQUESTED VIEW EXISTS
				//
				// This is strictly not needed since any subsequent replication
				// requests will fail. But we might as well fail early.

				String viewName = Views.convertStamdataUriToViewName(requestBody.getView());
				Class<? extends View> viewClass = registry.get(viewName);

				if (viewClass == null) {
					return factory.createNewErrorReply(request, CLIENT_SIDE_ERROR, "The requested view does not exist.");
				}

				// AUTHORIZE THE CLIENT
				//
				// Check if the client has the required privileges
				// to access the requested view.
				//
				// The authorization itself is checked by the security manager.

				AuthorizationResponseStructure responseBody = new AuthorizationResponseStructure();
				responseBody.authorization = securityManager.issueAuthenticationToken(cvr, viewClass, card.getExpiryDate());

				if (responseBody.authorization != null) {
					audit.log("cvr=%s, request_view=%s, expires_at=%s", cvr, viewName, card.getExpiryDate());
				}

				// RESPOND TO THE REQUEST

				Reply reply = factory.createNewReply(request, FLOW_FINALIZED_SUCCESFULLY);
				Document replyXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				marshaller.marshal(responseBody, replyXML);
				reply.setBody(replyXML.getDocumentElement());

				return reply;

			}
			catch (Exception e) {
				logger.warn("Could not process request.", e);
				return factory.createNewErrorReply(request, SERVER_SIDE_ERROR, "An unexpected error occured on the server. Please contact support.");
			}
		}
	}
}
