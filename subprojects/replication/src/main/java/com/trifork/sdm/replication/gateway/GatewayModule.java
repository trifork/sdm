package com.trifork.sdm.replication.gateway;


import static com.google.inject.name.Names.*;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.*;

import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.trifork.sdm.replication.gateway.properties.*;


public class GatewayModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		requireBinding(Key.get(String.class, named("replication.url")));
		requireBinding(Key.get(int.class, named("replication.urlTTL")));
		requireBinding(Key.get(int.class, named("replication.defaultPageSize")));
		requireBinding(Key.get(String.class, named("replication.secret")));

		bind(RequestProcessor.class).annotatedWith(SOAP.class).to(SoapProcessor.class);

		// Set up the route.

		serve("/gateway").with(GatewayServlet.class);
	}


	@Inject
	@Provides
	@Singleton
	protected URL provideURL(@Named("replication.url") String url) throws MalformedURLException
	{
		return new URL(url);
	}


	@Inject
	@Provides
	@TTL
	protected int provideTTL(@Named("replication.urlTTL") int ttl) throws MalformedURLException
	{
		return ttl;
	}


	@Inject
	@Provides
	@DefaultPageSize
	protected int provideDefaultPageSize(@Named("replication.defaultPageSize") int pageSize) throws MalformedURLException
	{
		return pageSize;
	}


	@Inject
	@Provides
	@Secret
	protected String provideSecret(@Named("replication.secret") String secret)
	{
		return secret;
	}
}
