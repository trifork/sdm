package com.trifork.stamdata.replication.util;

import java.net.URL;
import org.apache.commons.configuration.*;
import com.google.inject.servlet.ServletModule;


/**
 * Superclass granting modules easy access to the app's configuration.
 * 
 * Loads the application's configuration file 'config.properties', and combines
 * the properties in them with the JVM system properties. The properties file
 * will only be loaded once.
 */
public abstract class ConfiguredModule extends ServletModule {

	private static CompositeConfiguration config;

	{
		try {
			URL configFile = getClass().getClassLoader().getResource("config.properties");
			config = new CompositeConfiguration(new PropertiesConfiguration(configFile));
			config.addConfiguration(new SystemConfiguration());
		}
		catch (ConfigurationException e) {
			addError("Could not load configuration.", e);
		}
	}

	protected Configuration getConfig() {

		return config;
	}
}
