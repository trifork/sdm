package com.trifork.configuration;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

public class ConfigurationLoaderTest {
	@Test
	public void canLoadFromClasspath()  {
		ConfigurationLoader loader = new ConfigurationLoader("test", "prefix", "/");
		Configuration result = loader.loadConfiguration();
		assertEquals("testValue", result.getProperty("testProperty"));
	}

	@Test
	public void fileSystemPropsOverridesClasspathProps() throws IOException {
		String tmpDir = System.getProperty("java.io.tmpdir");
		File confFile = new File(tmpDir, "prefix.test.properties");
		confFile.deleteOnExit();
		FileWriter writer = new FileWriter(confFile);
		writer.write("testProperty=testValueFromFile");
		writer.close();
		Configuration result = new ConfigurationLoader("test", "prefix", tmpDir).loadConfiguration();
		assertEquals("testValueFromFile", result.getProperty("testProperty"));
		assertEquals("testValue2", result.getProperty("testProperty2"));
	}

	@Test
	public void canUseMultipleEnvironments() throws IOException {
		ConfigurationLoader loader = new ConfigurationLoader("test,test2", "prefix", "/");
		Configuration result = loader.loadConfiguration();
		assertEquals("overridenTestValue", result.getProperty("testProperty"));
	}
}
