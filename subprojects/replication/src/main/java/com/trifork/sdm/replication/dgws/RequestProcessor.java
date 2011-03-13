package com.trifork.sdm.replication.dgws;


import static dk.sosi.seal.model.AuthenticationLevel.*;
import static dk.sosi.seal.model.constants.DGWSConstants.*;
import static dk.sosi.seal.model.constants.FaultCodeValues.*;
import static dk.sosi.seal.model.constants.FlowStatusValues.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.w3c.dom.Document;

import com.google.inject.Inject;
import com.trifork.sdm.replication.admin.models.IAuditLog;
import com.trifork.sdm.replication.admin.models.PermissionDao;
import com.trifork.sdm.replication.util.AuthorizationManager;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;
import dk.sosi.seal.xml.XmlUtil;


public class RequestProcessor {

	private static final Logger logger = getLogger(RequestProcessor.class);

	private static final int SOAP_OK = 200;
	private static final int SOAP_FAULT = 500;

	private static final String SOAP_CONTENT_TYPE = "application/soap+xml; charset=UTF-8";

	private final SOSIFactory factory;

	private String response = null;
	private int responseCode = SOAP_FAULT;

	private final PermissionDao permissionRepository;

	private final IAuditLog audit;

	private final Marshaller marshaller;
	private final Unmarshaller unmarshaller;

	private final AuthorizationManager authorizationManager;


	@Inject
	RequestProcessor(PermissionDao permissionRepository, IAuditLog auditLog, Marshaller marshaller, Unmarshaller unmarshaller, AuthorizationManager authorizationManager) {
		this.permissionRepository = permissionRepository;
		this.marshaller = marshaller;
		this.unmarshaller = unmarshaller;
		this.audit = auditLog;
		this.authorizationManager = authorizationManager;

		Properties encryptionSetting = SignatureUtil.setupCryptoProviderForJVM();

		// TODO: Inject the factory or the test.
		SOSITestFederation federation = new SOSITestFederation(encryptionSetting);
		factory = new SOSIFactory(federation, new EmptyCredentialVault(), encryptionSetting);
	}


	public void process(String xml, String cvrNumber, String method) {
		Reply responseEnvelope;

		try {
			Reply error = null;

			Request requestEnvelope = factory.deserializeRequest(xml);

			// Check that the clients are using the right version of DGWS.

			if (!DGWSConstants.VERSION_1_0_1.equals(requestEnvelope.getDGWSVersion())) {
				logger.warn("Unsupported version of DGWS is being used.");
			}

			AuthorizationRequestStructure request = unmarshaller.unmarshal(requestEnvelope.getBody(), AuthorizationRequestStructure.class).getValue();

			// Validate the parameters.

			if (!method.equals("POST")) {
				responseEnvelope = factory.createNewErrorReply(requestEnvelope, "Client", "Unsupported HTTP method. Use a POST.");
			}
			else if ((error = checkIdentityCard(requestEnvelope)) != null || (error = checkRequestIntegrity(requestEnvelope, request)) != null) {
				responseEnvelope = error;
			}
			else if (cvrNumber == null || !canAccessEntity(request, cvrNumber)) {
				responseEnvelope = factory.createNewErrorReply(requestEnvelope, "Client", "You do not have access to the requested entity.");
			}
			else {
				// Write the request to the audit log.

				String message = String.format("Access request: client_cvr=%s, entity_uri=%s", cvrNumber, request.entityURI);
				audit.log(message);

				// Log some statistics if it is enabled.

				logger.info(message);

				// Authorize the client.

				AuthorizationResponseStructure responseBody = new AuthorizationResponseStructure();
				responseBody.authorization = authorizationManager.create(cvrNumber, request.entityURI, requestEnvelope.getIDCard().getExpiryDate());

				// Construct the URL and return it in SOAP.

				responseEnvelope = factory.createNewReply(requestEnvelope, FLOW_FINALIZED_SUCCESFULLY);

				DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance();
				documentBuilder.setNamespaceAware(true);
				Document doc = documentBuilder.newDocumentBuilder().newDocument();

				marshaller.marshal(responseBody, doc);
				responseEnvelope.setBody(doc.getDocumentElement());
			}
		}
		catch (Exception t) {
			logger.error("Unhandled exception has thrown in the gateway.", t);
			responseEnvelope = factory.createNewErrorReply(VERSION_1_0_1, "", "", "Server", "Server error. The event has been logged. Please contact the support.");
		}

		// Set the correct response code.

		responseCode = responseEnvelope.isFault() ? SOAP_FAULT : SOAP_OK;

		response = XmlUtil.node2String(responseEnvelope.serialize2DOMDocument(), false, false);
	}


	private boolean canAccessEntity(AuthorizationRequestStructure soapBody, String clientCVR) throws SQLException {
		return permissionRepository.canAccessEntity(clientCVR, soapBody.entityURI);
	}


	/**
	 * Ensures that all required parameters are present.
	 */
	private Reply checkRequestIntegrity(Request request, AuthorizationRequestStructure soapBody) {
		Reply error = null;

		if (soapBody.entityURI == null || soapBody.entityURI.isEmpty()) {
			// TODO: All log messages should contain a it_system={}.
			String message = "A request must contain a valid 'entity' parameter.";
			logger.warn(message);
			error = factory.createNewErrorReply(request, "Client", message);
		}

		return error;
	}


	/**
	 * Authenticates the request using its ID card.
	 * 
	 * @return a Reply with a Fault if the user is not authorized.
	 */
	private Reply checkIdentityCard(Request request) throws IOException {
		SystemIDCard card = (SystemIDCard) request.getIDCard();

		Reply error = null;

		// Make sure the client has the right authentication level.

		if (card.getAuthenticationLevel().getLevel() < VOCES_TRUSTED_SYSTEM.getLevel()) {
			error = factory.createNewErrorReply(request, SECURITY_LEVEL_FAILED, "Authentication Level of 3 or above is required.");
		}
		else if (!card.isValidInTime()) {
			error = factory.createNewErrorReply(request, EXPIRED_IDCARD, "The ID card has expired.");
		}

		return error;
	}


	public String getResponse() {
		return response;
	}


	public int getResponseCode() {
		return responseCode;
	}


	public String getContentType() {
		return SOAP_CONTENT_TYPE;
	}
}
