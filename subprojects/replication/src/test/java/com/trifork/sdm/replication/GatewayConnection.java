package com.trifork.sdm.replication;


import java.net.URL;

import com.trifork.sdm.replication.gateway.GatewayRequest;
import com.trifork.sdm.replication.replication.OutputFormat;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.Request;


public class GatewayConnection extends SOAPConnection
{
	private final IDCard idCard;
	private final SOSIFactory factory;
	private final URL gatewayUrl;


	public GatewayConnection(IDCard idCard, SOSIFactory factory, URL gatewayUrl)
	{
		this.idCard = idCard;
		this.factory = factory;
		this.gatewayUrl = gatewayUrl;
	}


	public String request(String resource, int version) throws Exception
	{
		// Set the ID card that we just created on the request.

		Request request = factory.createNewRequest(false, null);
		request.setIDCard(idCard);

		// Construct the initial replication call, that gives
		// us access to the replication service.

		GatewayRequest requestBody = new GatewayRequest();
		requestBody.resource = resource;
		requestBody.version = version;
		requestBody.format = OutputFormat.XML.name();
		requestBody.rows = 1000;

		// Convert the request parameters into XML,
		// and send it as a SOAP request.

		request.setBody(requestBody.serialize());

		// Send the request.

		String responseXml = sendSOAP(gatewayUrl, request.serialize2DOMDocument());
		return responseXml;
	}
}
