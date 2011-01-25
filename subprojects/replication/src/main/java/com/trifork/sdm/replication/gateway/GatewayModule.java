package com.trifork.sdm.replication.gateway;


import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;
import com.trifork.sdm.replication.settings.*;
import com.trifork.sdm.replication.util.PropertyServletModule;
import com.trifork.sdm.replication.util.URLFactory;


public class GatewayModule extends PropertyServletModule
{
	private static final Logger LOG = LoggerFactory.getLogger(GatewayModule.class);

	private static final String PAGE_SIZE = "replication.defaultPageSize";
	private static final String URL_TTL = "replication.urlTTL";
	private static final String PORT = "replication.port";
	private static final String HOST = "replication.host";


	@Override
	protected void configureServlets()
	{
		LOG.info("Configuring the SOAP gateway.");

		int defaultPageSize = Integer.parseInt(property(PAGE_SIZE));
		bindConstant().annotatedWith(DefaultPageSize.class).to(defaultPageSize);

		int ttl = Integer.parseInt(property(URL_TTL));
		bindConstant().annotatedWith(TTL.class).to(ttl);

		int port = Integer.parseInt(property(PORT));
		String host = property(HOST);

		try
		{
			URL replicationURL = new URL("http", host, port, "/");
			bind(URL.class).toInstance(replicationURL);
		}
		catch (MalformedURLException e)
		{
			addError("Invalid replication URL configured.", e);
		}

		serve("/gateway").with(GatewayServlet.class);
	}


	@Inject
	@Provides
	@SOAP
	protected RequestProcessor providerProcessor(URLFactory urlFactory)
	{
		return new SoapProcessor(urlFactory);
	}
}
