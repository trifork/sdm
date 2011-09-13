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

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.replication.db.DatabaseModule;
import com.trifork.stamdata.replication.logging.LoggingModule;
import com.trifork.stamdata.replication.monitoring.MonitoringModule;
import com.trifork.stamdata.replication.security.UnrestrictedSecurityModule;
import com.trifork.stamdata.replication.security.dgws.DGWSModule;
import com.trifork.stamdata.replication.webservice.RegistryModule;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.pki.InMemoryIntermediateCertificateCache;
import dk.sosi.seal.pki.SOSIFederation;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;


public class ApplicationContextListener extends GuiceServletContextListener
{
	private static final Logger logger = getLogger(ApplicationContextListener.class);
	
	private static final String COMPONENT_NAME = "stamdata-batch-copy-ws";

	@Override
	protected Injector getInjector()
	{
		Injector injector = null;
		
		Properties configuration = ConfigurationLoader.loadForName(COMPONENT_NAME);

		try
		{	
			logger.info("Configuring Stamdata WebService.");

			// The order these modules are added is not unimportant.
			// Since some of them add filters to the filter chain
			// they must be placed in the right order, e.i. some of
			// the filters depend on settings from previous filters.

			List<Module> modules = Lists.newArrayList();

			// CONFIGURE DATA ACCESS
			
			modules.add(new DatabaseModule(
				configuration.getProperty("db.connection.driverClass"),
				configuration.getProperty("db.connection.sqlDialect"),
				configuration.getProperty("db.connection.jdbcURL"),
				configuration.getProperty("db.connection.username"),
				configuration.getProperty("db.connection.password", null)
			));

			// CONFIGURE PROFILING & MONITORING
			//
			// NB. The monitoring module must be placed before
			// the registry module. Else the 'status' URL will be
			// interpreted as a registry.

			modules.add(new MonitoringModule());

			// CONFIGURE AUTHENTICATION & AUTHORIZATION

			String security = configuration.getProperty("security");
			
			if ("none".equalsIgnoreCase(security))
			{
				logger.warn("Service running in development mode. Security is completly disabled!");
				modules.add(new UnrestrictedSecurityModule());
			}
			else if ("dgwsTest".equalsIgnoreCase(security))
			{
				logger.warn("Service running in test mode. This will allow ID Cards signed by the test STS!");
				
				modules.add(new DGWSModule() {
					
					@Provides
					@Singleton
					@SuppressWarnings("unused")
					public SOSIFactory provideSOSIFactory()
					{
						Properties encryption = SignatureUtil.setupCryptoProviderForJVM();
						Federation federation = new SOSITestFederation(encryption, new InMemoryIntermediateCertificateCache());
						return new SOSIFactory(federation, new EmptyCredentialVault(), encryption);
					}
				});
			}
			else
			{
				logger.info("Service running in production mode.");
				
				modules.add(new DGWSModule() {
					
					@Provides
					@Singleton
					@SuppressWarnings("unused")
					public SOSIFactory provideSOSIFactory()
					{
						Properties encryption = SignatureUtil.setupCryptoProviderForJVM();
						Federation federation = new SOSIFederation(encryption, new InMemoryIntermediateCertificateCache());
						return new SOSIFactory(federation, new EmptyCredentialVault(), encryption);
					}
				});
			}

			// CONFIGURE WHERE TO FIND THE VIEW CLASSES

			modules.add(new RegistryModule());

			// LOGGING

			modules.add(new LoggingModule());
			
			// CREATE THE INJECTOR

			injector = Guice.createInjector(modules);

			logger.info("Stamdata Batch Copy Service has been initialized.");
		}
		catch (Exception e)
		{
			throw new RuntimeException("Initialization failed do to a configuration error.", e);
		}

		return injector;
	}
}
