package com.trifork.stamdata;

import java.io.*;
import java.util.Properties;

import org.slf4j.*;


public class ConfigurationLoader
{
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

	public static final String BUILDIN_CONFIG_FILE = "config.properties";
	public static final String TEST_CONFIGURATION_FILE = "test-config.properties";

	public static Properties getForComponent(String componentName)
	{
		try
		{
			// Load the build in configuration. These values will be overriden
			// by any properties in the production configuration file.
			
			ClassLoader classLoader = ConfigurationLoader.class.getClassLoader();

			InputStream buildInConfig = classLoader.getResourceAsStream(BUILDIN_CONFIG_FILE);
			Preconditions.checkState(buildInConfig != null, "The component did not contain a build-in configuration file with name 'config.properties'.");

			final Properties config = new Properties();

			config.load(buildInConfig);

			buildInConfig.close();

			// If the file test-config.properties is included we don't warn
			// about the missing production configuration file.
			
			if (classLoader.getResource(TEST_CONFIGURATION_FILE) != null)
			{
				logger.info("Found {} will not load production configuration unless this file is removed.", TEST_CONFIGURATION_FILE);
				return config;
			}
			
			// Otherwise we try to load the production configuration.
			
			Preconditions.checkArgument(componentName != null && !componentName.isEmpty(), "componentName");
			InputStream deploymentConfig = classLoader.getResourceAsStream(componentName + ".properties");

			if (deploymentConfig != null)
			{
				logger.info("Configuration file '{}.properties' found.", componentName);

				config.load(deploymentConfig);
				deploymentConfig.close();
			}
			else
			{
				logger.warn("Could not find '{}.properties'. Using default configuration.", componentName);
			}

			return config;
		}
		catch (IOException e)
		{
			throw new RuntimeException("An unexpected error occured while reading configuration files.", e);
		}
	}
}
