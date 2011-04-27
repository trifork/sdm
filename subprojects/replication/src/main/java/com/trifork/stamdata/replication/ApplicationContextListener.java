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

package com.trifork.stamdata.replication;

import static org.slf4j.LoggerFactory.getLogger;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.replication.db.DatabaseModule;
import com.trifork.stamdata.replication.gui.GuiModule;
import com.trifork.stamdata.replication.logging.LoggingModule;
import com.trifork.stamdata.replication.monitoring.MonitoringModule;
import com.trifork.stamdata.replication.replication.RegistryModule;
import com.trifork.stamdata.replication.security.UnrestrictedSecurityModule;
import com.trifork.stamdata.replication.security.dgws.DGWSModule;
import com.trifork.stamdata.replication.security.ssl.SslModule;


public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);

	@Override
	protected Injector getInjector() {

		Injector injector = null;

		try {
			logger.info("Loading configuration.");

			CompositeConfiguration config = new CompositeConfiguration();
			config.addConfiguration(new PropertiesConfiguration(getClass().getClassLoader().getResource("config.properties")));

			URL deploymentConfig = getClass().getClassLoader().getResource("stamdata-replication.properties");

			if (deploymentConfig != null) {
				config.addConfiguration(new PropertiesConfiguration(getClass().getClassLoader().getResource("stamdata-replication.properties")));
			}

			logger.info("Configuring Stamdata Service.");

			// The order these modules are added is not unimportant.
			// Since some of them add filters to the filter chain
			// they must be placed in the right order, e.i. some of
			// the filters depend on settings from previous filters.

			List<Module> modules = new ArrayList<Module>();
			
			// CONFIGURE DATA ACCESS

			modules.add(new DatabaseModule(
				config.getString("db.connection.driverClass"),
				config.getString("db.connection.sqlDialect"),
				config.getString("db.connection.jdbcURL"),
				config.getString("db.connection.username"),
				config.getString("db.connection.password", null)
			));

			// CONFIGURE PROFILING & MONITORING

			modules.add(new MonitoringModule());

			// CONFIGURE AUTHENTICATION & AUTHORIZATION

			String security = config.getString("security");
			if (security.equals("dgws")) {
				modules.add(new DGWSModule());
			} else if (security.equals("ssl")) {
				modules.add(new SslModule(config.getBoolean("security.ssl.test")));
			} else {
				modules.add(new UnrestrictedSecurityModule());
			}

			// CONFIGURE WHERE TO FIND THE VIEW CLASSES

			modules.add(new RegistryModule());

			// CONFIGURE THE ADMIN GUI

			modules.add(new GuiModule(
				config.getString("rid2cpr.endpoint"),
				getClass().getClassLoader().getResource(config.getString("rid2cpr.keystore")).toExternalForm(),
				config.getString("rid2cpr.keystorePassword"),
				config.getInt("rid2cpr.callTimeout"),
				getWhiteList(config)
			));

			// LOGGING

			modules.add(new LoggingModule());

			injector = Guice.createInjector(modules);

			logger.info("Service configured.");
		}
		catch (Exception e) {
			logger.error("Initialization failed do to a configuration error.", e);
		}

		return injector;
	}

	private Map<String, String> getWhiteList(final Configuration config) {

		String[] cvrs = config.getStringArray("whitelist");
		String[] names = config.getStringArray("whitelistNames");

		Map<String, String> whiteList = new HashMap<String, String>();

		for (int i = 0; i < cvrs.length; i++)
			whiteList.put(names[i], cvrs[i]);

		return whiteList;
	}
}
