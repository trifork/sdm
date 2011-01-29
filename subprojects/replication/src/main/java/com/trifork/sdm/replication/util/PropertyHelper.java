package com.trifork.sdm.replication.util;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import com.trifork.stamdata.Nullable;


public class PropertyHelper
{

	private static Properties properties;

	{
		properties = new Properties();

		// Load the default properties from the build-in config file.

		try
		{
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
			properties.load(inputStream);
			inputStream.close();
		}
		catch (Throwable t)
		{

			throw new RuntimeException("Could not read the build-in 'config.properties' file.", t);
		}

		// Add the jar's parent directory to the mix,
		// and override any properties that are set here.

		try
		{
			URL configFile = getClass().getClassLoader().getResource(".");
			configFile = new URL(configFile, "config.properties");

			FileInputStream fileStream = new FileInputStream(new File(configFile.toURI()));
			properties.load(fileStream);
			fileStream.close();
		}
		catch (Throwable t)
		{

			// noop
		}
	}


	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}


	public String getProperty(String key, @Nullable String defaultValue)
	{
		return properties.getProperty(key, defaultValue);
	}


	public Properties getProperties()
	{
		return properties;
	}
}
