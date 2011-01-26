package com.trifork.sdm.replication.util;


import java.net.URL;

import javax.inject.Inject;

import com.trifork.sdm.replication.gateway.properties.DefaultPageSize;
import com.trifork.sdm.replication.gateway.properties.TTL;
import com.trifork.stamdata.Entities;
import com.trifork.stamdata.Nullable;


public class URLFactory
{
	private static final String BEGINING_OF_TIME = "00000000000000000000";

	private static final long MILLIS_TO_SECS = 1000;

	private final long ttl;
	private final URL baseURL;
	private final SignatureFactory signatureFactory;
	private final int defaultPageSize;


	@Inject
	URLFactory(URL baseURL, @DefaultPageSize int defaultPageSize, @TTL int ttl, SignatureFactory signatureFactory)
	{
		this.baseURL = baseURL;
		this.ttl = ttl;
		this.signatureFactory = signatureFactory;
		this.defaultPageSize = defaultPageSize;
	}


	public String create(Class<?> resourceType, @Nullable Integer pageSize, @Nullable String historyId)
	{
		// Set the default values for the optional parameters if non are supplied.

		if (pageSize == null)
		{
			pageSize = defaultPageSize;
		}

		if (historyId == null)
		{
			historyId = BEGINING_OF_TIME;
		}

		String type = Entities.getEntityName(resourceType);

		// Calculate the parameter values.

		long expires = System.currentTimeMillis() / MILLIS_TO_SECS + ttl;

		// Calculate the signature.
		
		String signature = signatureFactory.create(type, expires, historyId, pageSize);
		
		// Build the URL String

		StringBuilder resultingURL = new StringBuilder(baseURL.toString());

		resultingURL.append("replicate")
			.append("?entity=").append(type)
			.append("&historyId=").append(historyId)
			.append("&pageSize=").append(pageSize)
			.append("&expires=").append(expires)
			.append("&signature=").append(signature);

		return resultingURL.toString();
	}
}
