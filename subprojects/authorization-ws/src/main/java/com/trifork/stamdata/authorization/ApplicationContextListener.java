package com.trifork.stamdata.authorization;

import static dk.sosi.seal.model.SignatureUtil.setupCryptoProviderForJVM;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.pki.InMemoryIntermediateCertificateCache;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;


public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);

	@Override
	protected Injector getInjector() {

		logger.info("Configuring the stamdata authorization web-service.");

		try {
			// LOAD CONFIGURATION FILES

			InputStream buildInConfig = getClass().getClassLoader().getResourceAsStream("config.properties");

			final Properties properties = new Properties();
			properties.load(buildInConfig);
			buildInConfig.close();

			// READ THE SUBJECT SERIAL NUMBERS OF CLIENTS
			//
			// These numbers are used to allow access to the system.
			// The list contains both CVR and UID but we only need
			// the CVR (for now). This may change in future releases.
			
			String whiteListProperty = properties.getProperty("subjectSerialNumbers", "");

			Set<String> cvrNumbers = Sets.newHashSet();
			
			for (String item : whiteListProperty.split(",")) {
				cvrNumbers.add(item.split("-")[0].split(":")[1]);
			}
			
			final Set<String> whitelist = ImmutableSet.of(cvrNumbers.toArray(new String[] {}));

			// SEAL

			// FIXME: Allow Test Federation for testing.
			
			Properties cryptoSettings = setupCryptoProviderForJVM();
			
			final SOSIFactory sosiFactory = new SOSIFactory(new SOSITestFederation(cryptoSettings, new InMemoryIntermediateCertificateCache()), new EmptyCredentialVault(), cryptoSettings);

			// XML SERIALIZATION

			final JAXBContext jaxbContext = JAXBContext.newInstance(AuthorizationResponseStructure.class, AuthorizationRequestStructure.class);

			// BIND THE DEPENDENCIES
			//
			// All these dependencies are type safe so we just bind them
			// to the instances.
			
			return Guice.createInjector(new ServletModule() {

				@Override
				protected void configureServlets() {
					
					install(new DatabaseModule(
							properties.getProperty("db.connection.jdbcURL"),
							properties.getProperty("db.connection.username"),
							properties.getProperty("db.connection.password", "")
					));
					
					bind(SOSIFactory.class).toInstance(sosiFactory);
					bind(JAXBContext.class).toInstance(jaxbContext);
					bind(new TypeLiteral<Set<String>>() {}).toInstance(whitelist);
					bind(AuthorizationDao.class);

					serve("/").with(WebService.class);
					filter("/").through(PersistenceFilter.class);
					
					logger.info("Done configuring the stamdata authorization web-service.");
				}
			});
		}
		catch (Exception e) {
		
			throw new IllegalStateException("Cannot start the authorization web service.", e);
		}
	}
}
