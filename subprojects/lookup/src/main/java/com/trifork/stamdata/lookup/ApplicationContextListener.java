package com.trifork.stamdata.lookup;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.trifork.stamdata.views.cpr.*;
import com.trifork.stamdata.views.usagelog.UsageLogEntry;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.configuration.SystemPropertyBasedConfigurationLoader;
import com.trifork.stamdata.db.SessionFactoryModule;
import com.trifork.stamdata.logging.LogConfigurer;
import com.trifork.stamdata.lookup.rest.RestModule;
import com.trifork.stamdata.lookup.security.SecurityModule;
import com.trifork.stamdata.ssl.OcesSslModule;

public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);
	public static final String STAMDATA_ENVIRONMENT_STRING_SYSPROP = "sdm.environment";
	public static final String STAMDATA_CONFIG_DIRECTORY_SYSPROP = "sdm.config.directory";

	@Override
	protected Injector getInjector() {

		Injector injector = null;

		try {
			logger.info("Loading configuration.");
			Configuration config = new SystemPropertyBasedConfigurationLoader(
					"lookup", STAMDATA_ENVIRONMENT_STRING_SYSPROP,
					STAMDATA_CONFIG_DIRECTORY_SYSPROP).loadConfiguration();
			String sslTerminationMethod = config.getString("security.ssl.termination.method");

			logger.info("Configuring Stamdata Lookup Service.");

			List<Module> modules = new ArrayList<Module>();

			// LOGGING
			// must be before other filters, because we want logging in these other filters to have requestId, clientIp etc. in the MDC.
			LogConfigurer.configureLogging(true, sslTerminationMethod, modules);

			boolean useOcesTest = config.getBoolean("security.ssl.test");
			modules.add(new OcesSslModule(useOcesTest, sslTerminationMethod));
			@SuppressWarnings("unchecked")
			List<String> authorizedClients = config.getList("security.authorized.clients");
			modules.add(new SecurityModule(authorizedClients));

			
			modules.add(new RestModule());
			Set<Class<?>> entityClasses = new HashSet<Class<?>>();
            entityClasses.add(BarnRelation.class);
            entityClasses.add(Beskyttelse.class);
            entityClasses.add(Civilstand.class);
            entityClasses.add(Foedselsregistreringsoplysninger.class);
            entityClasses.add(Folkekirkeoplysninger.class);
            entityClasses.add(ForaeldremyndighedsRelation.class);
            entityClasses.add(KommunaleForhold.class);
            entityClasses.add(MorOgFaroplysninger.class);
            entityClasses.add(Person.class);
            entityClasses.add(Statsborgerskab.class);
            entityClasses.add(Udrejseoplysninger.class);
            entityClasses.add(UmyndiggoerelseVaergeRelation.class);
            entityClasses.add(Valgoplysninger.class);
            entityClasses.add(UsageLogEntry.class);

			modules.add(new SessionFactoryModule (
				config.getString("db.connection.driverClass"),
				config.getString("db.connection.sqlDialect"),
				config.getString("db.connection.jdbcURL"),
				config.getString("db.connection.username"),
				config.getString("db.connection.password", null), 
				entityClasses
			));
			injector = Guice.createInjector(modules);

		} catch (Exception e) {
			throw new RuntimeException(
					"Initialization failed do to a configuration error.", e);
		}
		logger.info("Done configuring Stamdata Lookup Service.");
		return injector;
	}

}
