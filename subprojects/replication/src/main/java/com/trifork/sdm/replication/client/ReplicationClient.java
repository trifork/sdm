package com.trifork.sdm.replication.client;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dk.sosi.seal.model.IDCard;


public class ReplicationClient {

	private final STSConnection stsConnection;
	private final GatewayConnection gatewayConnection;


	public ReplicationClient(STSConnection stsConnection, GatewayConnection gatewayConnection) {

		this.stsConnection = stsConnection;
		this.gatewayConnection = gatewayConnection;
	}


	/**
	 * Fetches an URL for an
	 * 
	 * This method should only be used if you have not previously replicated any
	 * data from the specified resource. You will get data since
	 * 
	 * This will return an URL that is valid for a short period of time. If you
	 * do not use the URL before it expires you will have to fetch a new URL,
	 * which can be slow down the replication process.
	 * 
	 * @param resourceURI
	 *            The URI of the resource you want to fetch.
	 * @param version
	 *            The version of the resource you want to fetch.
	 * @return
	 * @throws Exception
	 */
	public URL fetchURL(String resourceURI, int version) throws Exception {

		return fetchURL(resourceURI, null, version);
	}


	public URL fetchURL(String resourceName, String historyId, int version) throws Exception {

		IDCard idCard = stsConnection.getIDCard();
		
		return gatewayConnection.fetchURL(resourceName, historyId, 1, 10, idCard);
	}


	public InputStream replicate(URL resourceURL) throws Exception {

		return sendREST(resourceURL);
	}


	// TODO: Don't just throw a plain Exception.
	private static InputStream sendREST(URL resourceURL) throws Exception {

		HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();

		connection.connect();

		InputStream inputStream;

		if (connection.getResponseCode() < 400) {
			inputStream = connection.getInputStream();
		}
		else {
			inputStream = connection.getErrorStream();
		}

		return inputStream;
	}
}
