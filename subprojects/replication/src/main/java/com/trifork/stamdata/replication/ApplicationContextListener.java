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

package com.trifork.stamdata.replication;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.replication.db.DatabaseModule;
import com.trifork.stamdata.replication.logging.LoggingModule;
import com.trifork.stamdata.replication.monitoring.MonitoringModule;
import com.trifork.stamdata.replication.security.UnrestrictedSecurityModule;
import com.trifork.stamdata.replication.security.dgws.DGWSModule;
import com.trifork.stamdata.replication.webservice.RegistryModule;


public class ApplicationContextListener extends GuiceServletContextListener
{
	private static final Logger logger = getLogger(ApplicationContextListener.class);

	@Override
	protected Injector getInjector()
	{
		Injector injector = null;

		try
		{
			logger.info("Loading configuration.");
			
			InputStream buildInConfig = getClass().getClassLoader().getResourceAsStream("config.properties");
			InputStream deploymentConfig = getClass().getClassLoader().getResourceAsStream("stamdata-replication.properties");

			Properties config = new Properties();
			config.load(buildInConfig);
			buildInConfig.close();

			if (deploymentConfig != null)
			{
				config.load(deploymentConfig);
				deploymentConfig.close();
			}
			
			logger.info("Configuring Stamdata Service.");

			// The order these modules are added is not unimportant.
			// Since some of them add filters to the filter chain
			// they must be placed in the right order, e.i. some of
			// the filters depend on settings from previous filters.

			List<Module> modules = new ArrayList<Module>();

			// CONFIGURE DATA ACCESS
			
			modules.add(new DatabaseModule(
				config.getProperty("db.connection.driverClass"),
				config.getProperty("db.connection.sqlDialect"),
				config.getProperty("db.connection.jdbcURL"),
				config.getProperty("db.connection.username"),
				config.getProperty("db.connection.password", null)
			));

			// CONFIGURE PROFILING & MONITORING
			//
			// NB. The monitoring module must be placed before
			// the registry module. Else the 'status' URL will be
			// inturpreded as a registry.

			modules.add(new MonitoringModule());

			// CONFIGURE AUTHENTICATION & AUTHORIZATION

			String security = config.getProperty("security");
			
			if ("dgws".equals(security))
			{
				modules.add(new DGWSModule());
			}
			else
			{
				logger.warn("Service running without security enabled.");
				modules.add(new UnrestrictedSecurityModule());
			}

			// CONFIGURE WHERE TO FIND THE VIEW CLASSES

			modules.add(new RegistryModule());

			// LOGGING

			modules.add(new LoggingModule());
			
			// CREATE THE INJECTOR

			injector = Guice.createInjector(modules);

			logger.info("Service configured.");
		}
		catch (Exception e)
		{
			throw new RuntimeException("Initialization failed do to a configuration error.", e);
		}

		return injector;
	}
}
