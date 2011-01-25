package com.trifork.sdm.replication.util;


import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.trifork.sdm.replication.settings.Host;
import com.trifork.sdm.replication.settings.Port;


public class URLModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		bind(URLFactory.class);
		bind(SignatureFactory.class);
	}


	@Inject
	@Provides
	public URL provideURL(@Host String host, @Port int port) throws MalformedURLException
	{
		return new URL("http", host, port, "/");
	}
}
