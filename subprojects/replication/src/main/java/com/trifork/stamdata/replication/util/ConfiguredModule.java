// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.servlet.ServletModule;


/**
 * Superclass granting modules easy access to the app's configuration.
 * 
 * Loads the application's configuration file 'config.properties'.
 */
public abstract class ConfiguredModule extends ServletModule {

	private Properties properties = new Properties();

	protected ConfiguredModule() throws IOException {

		InputStream file = getClass().getClassLoader().getResourceAsStream("/config.properties");
		properties.load(file);
		file.close();
	}

	protected String getProperty(String key) {

		return properties.getProperty(key);
	}

	protected int getIntProperty(String key) {

		return Integer.parseInt(getProperty(key));
	}

	protected String[] getStringArrayProperty(String key) {

		String value = getProperty(key);

		return (value == null) ? null : value.split(",");
	}
}
