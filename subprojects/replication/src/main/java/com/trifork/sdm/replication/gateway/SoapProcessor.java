package com.trifork.sdm.replication.gateway;


import static dk.sosi.seal.model.AuthenticationLevel.*;
import static dk.sosi.seal.model.constants.FaultCodeValues.*;
import static dk.sosi.seal.model.constants.FlowStatusValues.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;

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

	private static final String SOAP_CONTENT_ENCODING = "application/soap+xml; charset=UTF-8";

	private final SOSIFactory factory;

	private String response = null;
	private int responseCode = SOAP_FAULT;
	private final URLFactory urlFactory;


	@Inject
	SoapProcessor(URLFactory urlFactory)
	{
		this.urlFactory = urlFactory;

		Properties encryptionSetting = SignatureUtil.setupCryptoProviderForJVM();

		// TODO: Inject the real federation or the test.
		SOSITestFederation federation = new SOSITestFederation(encryptionSetting);
		factory = new SOSIFactory(federation, new EmptyCredentialVault(), encryptionSetting);
	}


	@Override
	public void process(String xml, String method)
	{
		Reply error = null;

		try
		{
			Reply reply = null;

			Request request = factory.deserializeRequest(xml);

			// Check that the clients are using the right version of DGWS.

			if (!DGWSConstants.VERSION_1_0_1.equals(request.getDGWSVersion()))
			{
				LOG.warn("Unsupported version of DGWS is being used.");
			}

			GatewayRequest soapBody = GatewayRequest.deserialize(request.getBody());

			if (!method.equals("GET"))
			{
				reply = factory.createNewErrorReply(DGWSConstants.VERSION_1_0_1, "", "", ILLEGAL_HTTP_METHOD, "Unsupported HTTP method. Use a POST.");
			}
			else if ((error = checkIdentityCard(request)) != null || (error = checkRequestIntegrity(request, soapBody)) != null)
			{
				reply = error;
			}
			else
			{
				// Construct the URL and return it in SOAP.

				String resourceURL = urlFactory.create(soapBody.getResourceType(), soapBody.rows, soapBody.since);

				GatewayResponse responseBody = new GatewayResponse(resourceURL);

				reply = factory.createNewReply(request, FLOW_FINALIZED_SUCCESFULLY);
				reply.setBody(responseBody.serialize());
			}

			// Set the correct response code.

			responseCode = reply.isFault() ? SOAP_FAULT : SOAP_OK;

			response = XmlUtil.node2String(reply.serialize2DOMDocument());
		}
		catch (Throwable t)
		{
			// FIXME: Make a SOAP error envelope without using the request since
			// it might not have been parsed.

			responseCode = SOAP_FAULT;
		}
	}


	/**
	 * Ensures that all required parameters are present.
	 */
	private Reply checkRequestIntegrity(Request request, GatewayRequest soapBody)
	{
		Reply error = null;

		// TODO: Change faultcode (not medcom:faultcode) to Client (instead of Server),
		// on all these messages, and log them.

		if (soapBody.resource == null || soapBody.resource.isEmpty())
		{
			// TODO: All log messages should contain a it_system={}.
			String message = "A request must contain a valid 'resource' parameter.";
			LOG.warn(message);
			error = factory.createNewErrorReply(request, SYNTAX_ERROR, message);
		}
		else if (soapBody.getResourceType() == null)
		{
			error = factory.createNewErrorReply(request, SYNTAX_ERROR, "An unknown resource was requested.");
		}
		else if (soapBody.version == null)
		{
			error = factory.createNewErrorReply(request, SYNTAX_ERROR, "A request must contain a 'version' parameter.");
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
		return SOAP_CONTENT_ENCODING;
	}
}
