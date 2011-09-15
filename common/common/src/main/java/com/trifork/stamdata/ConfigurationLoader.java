package com.trifork.stamdata;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigurationLoader
{
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

	protected ConfigurationLoader()
	{
	}

	public static Properties loadForName(String componentName)
	{
		checkNotNull(componentName, "componentName");

		// LOAD CONFIGURATION FILES

		ClassLoader classLoader = ConfigurationLoader.class.getClassLoader();

		InputStream buildInConfig = classLoader.getResourceAsStream("config.properties");
		InputStream testConfig = classLoader.getResourceAsStream("test-" + componentName + ".properties");
		InputStream deploymentConfig = classLoader.getResourceAsStream(componentName + ".properties");

		final Properties config = new Properties();

		try
		{
			config.load(buildInConfig);
			buildInConfig.close();

			if (testConfig != null)
			{
				logger.info("Test Configuration file 'test-{}.properties' found.", componentName);

				config.load(testConfig);
			}
			else if (deploymentConfig != null)
			{
				logger.info("Configuration file '{}.properties' found.", componentName);

				config.load(deploymentConfig);
			}
			else
			{
				logger.warn("Could not find {}.properties. Using default configuration.", componentName);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("Problem loading the component's configuration files. component=" + componentName, e);
		}
		finally
		{
			IOUtils.closeQuietly(buildInConfig);
			IOUtils.closeQuietly(testConfig);
			IOUtils.closeQuietly(deploymentConfig);
		}
		
		return config;
	}
}
