package com.trifork.sdm.replication.util;


import java.util.Properties;

import com.google.inject.servlet.ServletModule;


public abstract class PropertyServletModule extends ServletModule
{
	private final PropertyHelper properties = new PropertyHelper();


	protected String property(String key)
	{
		return properties.getProperty(key, null);
	}


	protected String property(String key, String defaultValue)
	{
		return properties.getProperty(key, defaultValue);
	}


	protected Properties getProperties()
	{
		return properties.getProperties();
	}
}
