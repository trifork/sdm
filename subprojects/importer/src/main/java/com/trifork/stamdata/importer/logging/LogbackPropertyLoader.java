package com.trifork.stamdata.importer.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.trifork.stamdata.importer.ApplicationContextListener;

import ch.qos.logback.core.PropertyDefinerBase;


/**
 * This class is used by Logback to figure out which file it should log to.
 *
 * This is done in order to allow the log file's path to have a default value in
 * 'config.properties' which is build-in. While still allowing it to be
 * overridden in the external config file.
 */
public class LogbackPropertyLoader extends PropertyDefinerBase
{
	private static final String LOGGING_FILE_PROPERTY = "logging.file";
	
	private final String path;

	public LogbackPropertyLoader() throws IOException
	{
		InputStream buildInConfig = getClass().getClassLoader().getResourceAsStream(ApplicationContextListener.BUILDIN_CONFIG_FILE);
		InputStream deploymentConfig = getClass().getClassLoader().getResourceAsStream(ApplicationContextListener.DEPLOYMENT_CONFIG_FILE);

		Properties properties = new Properties();
		properties.load(buildInConfig);
		buildInConfig.close();

		if (deploymentConfig != null)
		{
			properties.load(deploymentConfig);
			deploymentConfig.close();
		}

		path = properties.getProperty(LOGGING_FILE_PROPERTY);
	}

	@Override
	public String getPropertyValue()
	{
		return path;
	}
}
