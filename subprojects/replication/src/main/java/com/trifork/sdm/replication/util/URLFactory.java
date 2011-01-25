package com.trifork.sdm.replication.util;


import java.net.URL;

import javax.inject.Inject;

import com.trifork.sdm.replication.settings.TTL;
import com.trifork.stamdata.NamingConvention;
import com.trifork.stamdata.Nullable;


public class URLFactory
{
	private static final long SECONDS = 1000l;

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


	public String create(Class<?> resourceType, @Nullable Integer pageSize, @Nullable String historyId)
	{
		// Calculate the parameter values.

		String type = NamingConvention.getResourceName(resourceType);

		long expires = System.currentTimeMillis() / SECONDS + ttl;

		// Build the URL String

		StringBuilder resource = new StringBuilder(baseURL.toString());

		resource.append("replicate");

		resource.append("?type=").append(type);

		if (historyId != null)
		{

			resource.append("&historyId=").append(historyId);
		}

		if (pageSize == null)
		{
			pageSize = 1000; // TODO: Inject default page size.
		}

		resource.append("&pageSize=").append(pageSize);

		resource.append("&expires=").append(expires);

		resource.append("&signature=");
		resource.append(signatureFactory.create(type, expires, historyId, pageSize));

		return resource.toString();
	}
}
