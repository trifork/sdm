package com.trifork.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

public class ConfigurationLoader {
	private final String configurationFilePrefix;
	private final String configurationFileDirectory;
	private final String environmentString;

	public ConfigurationLoader(String environmentString, String configurationFilePrefix, String configurationFileDirectory) {
		this.environmentString = environmentString;
		this.configurationFilePrefix = configurationFilePrefix;
		this.configurationFileDirectory = configurationFileDirectory;
	}

	public Configuration loadConfiguration() {
		Properties props = new Properties();
		StringTokenizer st = new StringTokenizer(environmentString, ",");
		while(st.hasMoreElements()) {
			String environment = st.nextToken().trim();
			loadPropertiesForEnvironment(props, environment);
		}
		BaseConfiguration config = new BaseConfiguration();
		for(Entry<?, ?> entry : props.entrySet()) {
			config.addProperty(entry.getKey().toString(), entry.getValue());
		}
		return config;
	}

	private void loadPropertiesForEnvironment(Properties props, String environment)  {
		try {
			String filename = configurationFilePrefix + "." + environment
					+ ".properties";
			InputStream input = ConfigurationLoader.class
					.getResourceAsStream("/" + filename);
			if (input != null) {
				props.load(input);
				input.close();
			}
			File fileSystemFile = new File(configurationFileDirectory, filename);
			if (fileSystemFile.exists()) {
				InputStream fileInput = new FileInputStream(fileSystemFile);
				props.load(fileInput);
				fileInput.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not load properties for environment " + environment, e);
		}
	}
}
