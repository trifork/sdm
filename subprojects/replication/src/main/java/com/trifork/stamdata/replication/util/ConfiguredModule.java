package com.trifork.stamdata.replication.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.servlet.ServletModule;


/**
 * Superclass granting modules easy access to the app's configuration.
 * 
 * Loads the application's configuration file 'config.properties'.
 */
public abstract class ConfiguredModule extends ServletModule {

	private Properties properties = new Properties();

	protected ConfiguredModule() throws IOException {

		InputStream file = getClass().getClassLoader().getResourceAsStream("/config.properties");
		properties.load(file);
		file.close();
	}

	protected String getProperty(String key) {

		return properties.getProperty(key);
	}

	protected int getIntProperty(String key) {

		return Integer.parseInt(getProperty(key));
	}

	protected String[] getStringArrayProperty(String key) {

		String value = getProperty(key);

		return (value == null) ? null : value.split(",");
	}
}
