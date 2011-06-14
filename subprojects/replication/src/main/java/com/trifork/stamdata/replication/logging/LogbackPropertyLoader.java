package com.trifork.stamdata.replication.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ch.qos.logback.core.PropertyDefinerBase;


/**
 * This class is used by Logback to figure out which file it should log to.
 *
 * This is done in order to allow the log file's path to have a default value in
 * 'config.properties' which is build-in. While still allowing it to be
 * overridden in the external config file.
 *
 * @author Thomas Børlum (thb@trifork.com)
 */
public class LogbackPropertyLoader extends PropertyDefinerBase {

	private final String path;

	public LogbackPropertyLoader() throws IOException {

		InputStream buildInConfig = getClass().getClassLoader().getResourceAsStream("config.properties");
		InputStream deploymentConfig = getClass().getClassLoader().getResourceAsStream("stamdata-importer.properties");

		Properties properties = new Properties();
		properties.load(buildInConfig);
		buildInConfig.close();

		if (deploymentConfig != null) {
			properties.load(deploymentConfig);
			deploymentConfig.close();
		}

		path = properties.getProperty("logging.file");
	}

	@Override
	public String getPropertyValue() {

		return path;
	}
}
