
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package dk.trifork.sdm.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.configuration.ConfigurationLoader;
import com.trifork.configuration.SystemPropertyBasedConfigurationLoader;


public class Configuration {

	private static Logger logger = LoggerFactory.getLogger(Configuration.class);
	private static Configuration defaultInstance = new Configuration();

	private static final String STAMDATA_ENVIRONMENT_STRING_SYSPROP = "sdm.environment";
	private static final String STAMDATA_CONFIG_DIRECTORY_SYSPROP = "sdm.config.directory";
	private org.apache.commons.configuration.Configuration loadedConfiguration;

	public Configuration() {
		try {
			loadedConfiguration = new SystemPropertyBasedConfigurationLoader("importer", STAMDATA_ENVIRONMENT_STRING_SYSPROP, STAMDATA_CONFIG_DIRECTORY_SYSPROP).loadConfiguration();
		}
		catch (Exception e) {
			logger.error("Error loading config.properties not found.");
		}
	}
	
	public Configuration(String env) {
		try {
			loadedConfiguration = new ConfigurationLoader(env,"importer","/").loadConfiguration();
		}
		catch (Exception e) {
			logger.error("Error loading config.properties not found.");
		}
		
	}

	public String getNotNullProperty(String key) {

		String value = loadedConfiguration.getString(key);
		if (value == null) {
			throw new RuntimeException("no value found for property key: " + key);
		}
		return value;
	}

	public int getIntProperty(String key) {

		return Integer.parseInt(getNotNullProperty(key));
	}

	public Date getDateProperty(String key) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(getNotNullProperty(key));
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getString(String key) {

		String s = defaultInstance.getProperty(key);
		return s;
	}

	public static Integer getInt(String key) {

		return Integer.parseInt(defaultInstance.getProperty(key));
	}

	private String getProperty(String key) {

		return loadedConfiguration.getString(key);
	}

	public static void setDefaultInstance(Configuration conf) {

		// Only for unit tests
		defaultInstance = conf;
	}
}
