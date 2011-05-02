package com.trifork.stamdata.replication;

import org.apache.commons.configuration.Configuration;

import com.trifork.configuration.ConfigurationLoader;

public class ConfigurationHelper {
	public static Configuration getConfiguration() {
		return new ConfigurationLoader("default,unittest", "replication", "/").loadConfiguration();
	}
}
