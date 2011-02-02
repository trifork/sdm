package com.trifork.sdm.replication.util;


import java.net.URL;

import com.google.inject.Inject;
import com.trifork.sdm.replication.gateway.properties.TTL;
import com.trifork.stamdata.Entities;


public class URLFactory
{
	private static final long MILLIS_TO_SECS = 1000;

	private final long ttl;
	private final URL baseURL;
	private final SignatureFactory signatureFactory;


	@Inject
	URLFactory(URL baseURL, @TTL int ttl, SignatureFactory signatureFactory)
	{
		this.baseURL = baseURL;
		this.ttl = ttl;
		this.signatureFactory = signatureFactory;
	}


	public String create(Class<?> resourceType, int pageSize, String historyId, String format)
	{
		String entity = Entities.getName(resourceType);

		// Calculate the parameter values.

		long expires = System.currentTimeMillis() / MILLIS_TO_SECS + ttl;

		// Calculate the signature.

		String signature = signatureFactory.create(entity, expires, historyId, pageSize);

		// Build the URL String

		StringBuilder resultingURL = new StringBuilder(baseURL.toString());

		resultingURL.append("replicate").append("?entity=").append(entity).append("&historyId=").append(historyId).append("&pageSize=").append(pageSize).append("&expires=").append(expires).append("&format=").append(format).append("&signature=").append(signature);

		return resultingURL.toString();
	}
}
