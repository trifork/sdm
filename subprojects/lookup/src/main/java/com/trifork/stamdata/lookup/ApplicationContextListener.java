package com.trifork.stamdata.lookup;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.configuration.SystemPropertyBasedConfigurationLoader;
import com.trifork.stamdata.db.SessionFactoryModule;
import com.trifork.stamdata.lookup.rest.RestModule;
import com.trifork.stamdata.lookup.security.SecurityModule;
import com.trifork.stamdata.ssl.OcesSslModule;
import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.views.cpr.KommunaleForhold;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;
import com.trifork.stamdata.views.cpr.Valgoplysninger;

public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);
	private static final String STAMDATA_ENVIRONMENT_STRING_SYSPROP = "sdm.environment";
	private static final String STAMDATA_CONFIG_DIRECTORY_SYSPROP = "sdm.config.directory";

	@Override
	protected Injector getInjector() {

		Injector injector = null;

		try {
			logger.info("Loading configuration.");
			Configuration config = new SystemPropertyBasedConfigurationLoader(
					"lookup", STAMDATA_ENVIRONMENT_STRING_SYSPROP,
					STAMDATA_CONFIG_DIRECTORY_SYSPROP).loadConfiguration();

			logger.info("Configuring Stamdata Service.");

			List<Module> modules = new ArrayList<Module>();
			boolean useOcesTest = config.getBoolean("security.ssl.test");
			String sslTerminationMethod = config.getString("security.ssl.termination.method");
			modules.add(new OcesSslModule(useOcesTest, sslTerminationMethod));
			@SuppressWarnings("unchecked")
			List<String> authorizedClients = config.getList("security.authorized.clients");
			modules.add(new SecurityModule(authorizedClients));

			
			modules.add(new RestModule());
			Set<Class<?>> entityClasses = new HashSet<Class<?>>();
			entityClasses.add(Person.class);
			entityClasses.add(Folkekirkeoplysninger.class);
			entityClasses.add(Statsborgerskab.class);
			entityClasses.add(Foedselsregistreringsoplysninger.class);
			entityClasses.add(Civilstand.class);
			entityClasses.add(BarnRelation.class);
			entityClasses.add(ForaeldremyndighedsRelation.class);
			entityClasses.add(KommunaleForhold.class);
			entityClasses.add(Udrejseoplysninger.class);
			entityClasses.add(UmyndiggoerelseVaergeRelation.class);
			entityClasses.add(Valgoplysninger.class);
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
		return injector;
	}

}
