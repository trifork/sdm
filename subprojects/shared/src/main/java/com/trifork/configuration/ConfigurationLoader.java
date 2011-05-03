package com.trifork.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to load configuration from multiple configuration files.
 * Configuration files are loaded from both classpath and and a configuration
 * file directory. The names of the configuration files that are loaded are
 * based on the following pattern [prefix].[environment].properties the
 * environmentString is used is a comma-separated list of environments. For
 * example, "default,production" would specify that, first the configuration in
 * [prefix].default.properties should be loaded, then the configuration in
 * [prefix].production.properties should be loaded, overriding any properties in
 * the default configuration.
 * 
 * The prefix should designate the module being configured, e.g. "replication"
 * or "importer". Then configuration for multiple modules can reside in the same
 * directory.
 * 
 * Configuration files in the file system overrides configuration files on the
 * classpath if the environment is the same.
 * 
 * @see SystemPropertyBasedConfigurationLoader
 * @author ahj
 * 
 */
public class ConfigurationLoader {
	private final String configurationFilePrefix;
	private final String configurationFileDirectory;
	private final String environmentString;
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);

	public ConfigurationLoader(String environmentString,
			String configurationFilePrefix, String configurationFileDirectory) {
		this.environmentString = environmentString;
		this.configurationFilePrefix = configurationFilePrefix;
		this.configurationFileDirectory = configurationFileDirectory;
	}

	public Configuration loadConfiguration() {
		logger.info("Loading configuration. Prefix: {} environments: {} configuration file directory: {}", new Object[] {configurationFilePrefix, environmentString, configurationFileDirectory});
		Properties props = new Properties();
		StringTokenizer st = new StringTokenizer(environmentString, ",");
		while (st.hasMoreElements()) {
			String environment = st.nextToken().trim();
			loadPropertiesForEnvironment(props, environment);
		}
		BaseConfiguration config = new BaseConfiguration();
		for (Entry<?, ?> entry : props.entrySet()) {
			config.addProperty(entry.getKey().toString(), entry.getValue());
		}
		return config;
	}

	private void loadPropertiesForEnvironment(Properties props,
			String environment) {
		try {
			String filename = configurationFilePrefix + "." + environment
					+ ".properties";
			InputStream input = ConfigurationLoader.class
					.getResourceAsStream("/" + filename);
			if (input != null) {
				logger.info("Loading configuration from classpath file {}", filename);
				props.load(input);
				input.close();
			}
			else {
				logger.info("Did not load configuration from classpath file {}, file did not exist", filename);
			}
			File fileSystemFile = new File(configurationFileDirectory, filename);
			if (fileSystemFile.exists()) {
				logger.info("Loading configuration from {}", fileSystemFile.getAbsolutePath());
				InputStream fileInput = new FileInputStream(fileSystemFile);
				props.load(fileInput);
				fileInput.close();
			}
			else {
				logger.info("Did not load configuration from {}, file did not exist", fileSystemFile.getAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException(
					"Could not load properties for environment " + environment,
					e);
		}
	}
}
