package com.trifork.sdm.replication.util;

import org.apache.commons.configuration.Configuration;

import com.google.inject.*;


public abstract class ConfiguredModule extends AbstractModule
{
	@Inject
	protected Configuration config;


	public ConfiguredModule()
	{
		requireBinding(Configuration.class);
		requestInjection(this);
	}
}
