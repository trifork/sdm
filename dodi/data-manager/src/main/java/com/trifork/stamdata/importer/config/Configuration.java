/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.config;

import java.io.InputStream;
import java.util.Properties;

import com.trifork.stamdata.importer.ComponentController;
import org.apache.log4j.Logger;

/**
 * @author Jan Buchholdt <jbu@trifork.com>
 * 
 * TODO: Static state is a bad idea in general. Send the configuration around instead of using this class.
 */
@Deprecated
public class Configuration
{
	private static Logger logger = Logger.getLogger(Configuration.class);
	private static Configuration defaultInstance = new Configuration();

	private Properties properties;

	public Configuration()
	{
		this("config");
	}

	public Configuration(String configName)
	{
		try
		{
			// Override the default configuration with the one found in
			// stamdata-data-manager.properties.

			InputStream buildInConfig = getClass().getClassLoader().getResourceAsStream(configName + ".properties");
			InputStream deploymentConfig = getClass().getClassLoader().getResourceAsStream(ConfigurationLoader.DEPLOYMENT_CONFIG_FILE);

			properties = new Properties();
			properties.load(buildInConfig);
			buildInConfig.close();

			if (deploymentConfig != null)
			{
				properties.load(deploymentConfig);
				deploymentConfig.close();
			}
		}
		catch (Exception e)
		{
			logger.error("Error loading the config files.", e);
		}
	}

	public static String getString(String key)
	{
		return defaultInstance.getProperty(key);
	}

	private String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	public static void setDefaultInstance(Configuration conf)
	{
		// Only for unit tests.
		defaultInstance = conf;
	}
}
