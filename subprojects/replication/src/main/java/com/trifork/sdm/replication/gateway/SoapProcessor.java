package com.trifork.sdm.replication.gateway;


import static dk.sosi.seal.model.AuthenticationLevel.*;
import static dk.sosi.seal.model.constants.DGWSConstants.*;
import static dk.sosi.seal.model.constants.FaultCodeValues.*;
import static dk.sosi.seal.model.constants.FlowStatusValues.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.trifork.sdm.replication.admin.models.PermissionRepository;
import com.trifork.sdm.replication.util.URLFactory;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;
import dk.sosi.seal.xml.XmlUtil;


public class SoapProcessor implements RequestProcessor
{
	private static final Logger LOG = getLogger(SoapProcessor.class);

	private static final int SOAP_OK = 200;
	private static final int SOAP_FAULT = 500;

	private static final String SOAP_CONTENT_TYPE = "application/soap+xml; charset=UTF-8";

	private final SOSIFactory factory;

	private String response = null;
	private int responseCode = SOAP_FAULT;
	private final URLFactory urlFactory;

	private final PermissionRepository permissionRepository;


	@Inject
	SoapProcessor(URLFactory urlFactory, PermissionRepository permissionRepository)
	{
		this.urlFactory = urlFactory;
		this.permissionRepository = permissionRepository;

		Properties encryptionSetting = SignatureUtil.setupCryptoProviderForJVM();

		// TODO: Inject the factory or the test.
		SOSITestFederation federation = new SOSITestFederation(encryptionSetting);
		factory = new SOSIFactory(federation, new EmptyCredentialVault(), encryptionSetting);
	}


	@Override
	public void process(String xml, String clientCVR, String method)
	{
		Reply reply;

		try
		{
			Reply error = null;

			Request request = factory.deserializeRequest(xml);

			// Check that the clients are using the right version of DGWS.

			if (!DGWSConstants.VERSION_1_0_1.equals(request.getDGWSVersion()))
			{
				LOG.warn("Unsupported version of DGWS is being used.");
			}

			GatewayRequest soapBody = GatewayRequest.deserialize(request.getBody());

			if (!method.equals("POST"))
			{
				reply = factory.createNewErrorReply(request, "Client", "Unsupported HTTP method. Use a POST.");
			}
			else if ((error = checkIdentityCard(request)) != null || (error = checkRequestIntegrity(request, soapBody)) != null)
			{
				reply = error;
			}
			else if (clientCVR == null || !canAccessEntity(soapBody, clientCVR))
			{
				reply = factory.createNewErrorReply(request, "Client", "You do not have access to the requested entity.");
			}
			else
			{
				// Construct the URL and return it in SOAP.

				String resourceURL = urlFactory.create(soapBody.getEntityType(), soapBody.pageSize, soapBody.historyId);

				GatewayResponse responseBody = new GatewayResponse(resourceURL);

				reply = factory.createNewReply(request, FLOW_FINALIZED_SUCCESFULLY);
				reply.setBody(responseBody.serialize());
			}
		}
		catch (Throwable t)
		{
			LOG.error("Unhandled exception has thrown in the gateway.", t);
			reply = factory.createNewErrorReply(VERSION_1_0_1, "", "", "Server", "Server error. The event has been logged. Please contact the support.");
		}
		
		// Set the correct response code.

		responseCode = reply.isFault() ? SOAP_FAULT : SOAP_OK;

		response = XmlUtil.node2String(reply.serialize2DOMDocument(), false, false);
	}


	private boolean canAccessEntity(GatewayRequest soapBody, String clientCVR) throws SQLException
	{
		return permissionRepository.canAccessEntity(clientCVR, soapBody.entity);
	}


	/**
	 * Ensures that all required parameters are present.
	 */
	private Reply checkRequestIntegrity(Request request, GatewayRequest soapBody)
	{
		Reply error = null;

		// TODO: Change faultcode (not medcom:faultcode) to Client (instead of Server),
		// on all these messages, and log them.

		if (soapBody.entity == null || soapBody.entity.isEmpty())
		{
			// TODO: All log messages should contain a it_system={}.
			String message = "A request must contain a valid 'entity' parameter.";
			LOG.warn(message);
			error = factory.createNewErrorReply(request, "Client", message);
		}
		else if (soapBody.getEntityType() == null)
		{
			error = factory.createNewErrorReply(request, "Client", "An unknown entity was requested.");
		}
		else if (soapBody.version == null)
		{
			error = factory.createNewErrorReply(request, "Client", "A request must contain a 'version' parameter.");
		}

		return error;
	}


	/**
	 * Authenticates the request using its ID card.
	 * 
	 * @return a Reply with a Fault if the user is not authorized.
	 */
	private Reply checkIdentityCard(Request request) throws IOException
	{
		SystemIDCard card = (SystemIDCard) request.getIDCard();

		Reply error = null;

		// Make sure the client has the right authentication level.

		if (card.getAuthenticationLevel().getLevel() < VOCES_TRUSTED_SYSTEM.getLevel())
		{
			error = factory.createNewErrorReply(request, SECURITY_LEVEL_FAILED, "Authentication Level of 3 or above is required.");
		}
		else if (!card.isValidInTime())
		{
			LOG.warn("");
			error = factory.createNewErrorReply(request, EXPIRED_IDCARD, "The ID card has expired.");
		}

		return error;
	}


	@Override
	public String getResponse()
	{
		return response;
	}


	@Override
	public int getResponseCode()
	{
		return responseCode;
	}


	@Override
	public String getContentType()
	{
		return SOAP_CONTENT_TYPE;
	}
}
