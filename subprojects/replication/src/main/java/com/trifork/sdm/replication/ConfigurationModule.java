package com.trifork.sdm.replication;

import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;


/**
 * Loads the application's configuration, combining the properties in
 * 'config.properties' with the JVM system properties.
 */
public class ConfigurationModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		try
		{
			Properties properties = new Properties();

			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
			properties.load(inputStream);
			inputStream.close();

			bind(Properties.class).toInstance(properties);

			// We can also access the properties directly through
			// Guice @Named annotations.

			Names.bindProperties(binder(), properties);
		}
		catch (Exception e)
		{
			addError("Could not load the application's configuration.", e);
		}
	}
}
