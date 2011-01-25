package com.trifork.sdm.replication.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import com.trifork.sdm.replication.gateway.GatewayRequest;
import com.trifork.sdm.replication.gateway.GatewayResponse;
import com.trifork.sdm.replication.replication.OutputFormat;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;


public class GatewayConnection extends SOAPConnection {

	private final SOSIFactory factory;
	private final URL gatewayURL;


	public GatewayConnection(URL gatewayURL) {

		this.gatewayURL = gatewayURL;

		// Determine which crypto provider to use.

		Properties cryptoProviderSettings = SignatureUtil.setupCryptoProviderForJVM();

		Federation federation = new SOSITestFederation(cryptoProviderSettings);
		factory = new SOSIFactory(federation, new EmptyCredentialVault(), cryptoProviderSettings);
	}


	public URL fetchURL(String resourceName, String historyId, int version, int numRows, IDCard idCard) {

		URL result = null;

		try {
			// Set the ID card that we just created on the request.

			Request request = factory.createNewRequest(false, null);
			request.setIDCard(idCard);

			// Construct the initial replication call, that gives
			// us access to the replication service.

			GatewayRequest requestBody = new GatewayRequest();
			requestBody.resource = resourceName;
			requestBody.version = version;
			requestBody.format = OutputFormat.XML.name();
			requestBody.rows = numRows;
			requestBody.since = historyId;

			// Convert the request parameters into XML,
			// and send it as a SOAP request.

			request.setBody(requestBody.serialize());

			// Send the request.

			String responseXML = sendSOAP(gatewayURL, request.serialize2DOMDocument());

			Reply responseEnvelope = factory.deserializeReply(responseXML);

			if (responseEnvelope.isFault()) {
				// TODO: Throw a meaningful exception.
			}

			result = GatewayResponse.deserialize(responseEnvelope.getBody()).buildUrl();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			// TODO: Handle this is some meaningful way.
		}
		catch (MalformedURLException e) {
			// TODO: Handle this is some meaningful way.
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO: Handle this is some meaningful way. (Should not just be
			// exception).
			e.printStackTrace();
		}

		return result;
	}
}
