package com.trifork.sdm.replication.replication;

import java.net.URL;

import com.trifork.sdm.replication.SOAPConnection;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.SecurityTokenRequest;

public class STSConnection extends SOAPConnection {
	private static final String TEST_STS_URL = "http://pan.certifikat.dk/sts/services/SecurityTokenService";

	private final IDCard idCard;
	private final SOSIFactory factory;

	public STSConnection(IDCard idCard, SOSIFactory factory) {
		this.idCard = idCard;
		this.factory = factory;		
	}
	
	public String request() throws Exception {
		SecurityTokenRequest request = factory.createNewSecurityTokenRequest();
		request.setIDCard(idCard);

		// We do not need to take any further steps like signing the message
		// as we do at authentication level 4.
		
		// Send the request.

		// TODO: Move the STS URL to a parameter.
		String responseXml = sendSOAP(new URL(TEST_STS_URL), request.serialize2DOMDocument());
		
		return responseXml;
	}
}
