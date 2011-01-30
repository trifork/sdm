package com.trifork.sdm.replication.gateway;

import java.net.*;

import com.google.inject.Provides;
import com.trifork.sdm.replication.gateway.properties.*;
import com.trifork.sdm.replication.util.ConfiguredModule;


public class GatewayModule extends ConfiguredModule
{
	@Override
	protected void configureServlets()
	{
		bind(RequestProcessor.class).annotatedWith(SOAP.class).to(SoapProcessor.class);

		// Set up the route.

		serve("/gateway").with(GatewayServlet.class);
	}


	@Provides
	protected URL provideURL() throws MalformedURLException
	{
		return new URL(getConfig().getString("replication.url"));
	}


	@Provides
	@TTL
	protected int provideTTL() throws MalformedURLException
	{
		return getConfig().getInt("replication.urlTTL");
	}


	@Provides
	@DefaultPageSize
	protected int provideDefaultPageSize() throws MalformedURLException
	{
		return getConfig().getInt("replication.defaultPageSize");
	}


	@Provides
	@Secret
	protected String provideSecret()
	{
		return getConfig().getString("replication.secret");
	}
}
