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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.configuration.SystemPropertyBasedConfigurationLoader;
import com.trifork.stamdata.replication.db.DatabaseModule;
import com.trifork.stamdata.replication.gui.GuiModule;
import com.trifork.stamdata.replication.gui.security.saml.SamlSecurityModule;
import com.trifork.stamdata.replication.gui.security.twowayssl.TwoWaySslSecurityModule;
import com.trifork.stamdata.replication.gui.security.unrestricted.GuiUnrestrictedSecurityModule;
import com.trifork.stamdata.replication.logging.DefaultClientIpModule;
import com.trifork.stamdata.replication.logging.LoggingModule;
import com.trifork.stamdata.replication.logging.ZeusClientIpModule;
import com.trifork.stamdata.replication.monitoring.MonitoringModule;
import com.trifork.stamdata.replication.replication.RegistryModule;
import com.trifork.stamdata.replication.security.UnrestrictedSecurityModule;
import com.trifork.stamdata.replication.security.dgws.DGWSModule;
import com.trifork.stamdata.replication.security.ssl.SslModule;
import com.trifork.stamdata.ssl.CommonSslModule;


public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);
	private static final String STAMDATA_ENVIRONMENT_STRING_SYSPROP = "sdm.environment";
	private static final String STAMDATA_CONFIG_DIRECTORY_SYSPROP = "sdm.config.directory";

	@Override
	protected Injector getInjector() {

		Injector injector = null;

		try {
			logger.info("Loading configuration.");
			Configuration config = new SystemPropertyBasedConfigurationLoader("replication", STAMDATA_ENVIRONMENT_STRING_SYSPROP, STAMDATA_CONFIG_DIRECTORY_SYSPROP).loadConfiguration();

			logger.info("Configuring Stamdata Service.");

			// The order these modules are added is not unimportant.
			// Since some of them add filters to the filter chain
			// they must be placed in the right order, e.i. some of
			// the filters depend on settings from previous filters.

			List<Module> modules = new ArrayList<Module>();

			// LOGGING
			// must be before other filters, because we want logging in these other filters to have requestId, clientIp etc. in the MDC.
			modules.add(new LoggingModule());

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
			String guiSecurity = config.getString("gui.security");
			boolean twoWaySslInUse = "twowayssl".equals(security) || 
			"twowayssl".equals(guiSecurity);
			if(twoWaySslInUse) {
				modules.add(new CommonSslModule(config.getBoolean("security.ssl.test"), config.getString("security.ssl.termination.method")));
			}
			
			String sslTerminationMethod = config.getString("security.ssl.termination.method");
			
			boolean zeusLoadBalancerInUse = twoWaySslInUse && "zeusLoadBalancer".equals(sslTerminationMethod);
			
			// The Zeus load balancer sends the actual client ip in a header. We are not really
			// interested in logging the ip-address of the load-balancer in every request!
			if(zeusLoadBalancerInUse) {
				modules.add(new ZeusClientIpModule());
			}
			else {
				modules.add(new DefaultClientIpModule());
			}
			
			if ("dgws".equals(security)) {
				modules.add(new DGWSModule());
			} else if ("twowayssl".equals(security)) {
				modules.add(new SslModule());
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
			
			if("saml".equals(guiSecurity)) {
				modules.add(new SamlSecurityModule());
			}
			else if ("twowayssl".equals(guiSecurity)) {
				modules.add(new TwoWaySslSecurityModule());
			}
			else if ("none".equals(guiSecurity)){
				modules.add(new GuiUnrestrictedSecurityModule());
			}
			else {
				throw new RuntimeException("Valid parameters for gui.security are saml, twowayssl,none");
			}

			injector = Guice.createInjector(modules);

			logger.info("Service configured.");
		}
		catch (Exception e) {
			throw new RuntimeException("Initialization failed do to a configuration error.", e);
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
