package com.trifork.configuration;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

public class SystemPropertyBasedConfigurationLoader {
	private final String filePrefix;
	private final String envPropertyName;
	private final String pathPropertyName;

	public SystemPropertyBasedConfigurationLoader(String filePrefix, String envPropertyName, String pathPropertyName) {
		this.filePrefix = filePrefix;
		this.envPropertyName = envPropertyName;
		this.pathPropertyName = pathPropertyName;
	}

	public Configuration loadConfiguration() throws IOException {
		String environment = System.getProperty(envPropertyName);
		if(environment == null) {
			environment = "default";
		}
		String path = System.getProperty(pathPropertyName);
		if(path == null) {
			path = "/";
		}
		return new ConfigurationLoader(environment, filePrefix, path).loadConfiguration();
	}
}
