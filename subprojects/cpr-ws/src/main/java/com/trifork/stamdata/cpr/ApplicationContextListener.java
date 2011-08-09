package com.trifork.stamdata.cpr;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.pki.InMemoryIntermediateCertificateCache;
import dk.sosi.seal.pki.SOSIFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;
import org.slf4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import static dk.sosi.seal.model.SignatureUtil.setupCryptoProviderForJVM;
import static org.slf4j.LoggerFactory.getLogger;


public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);

	private static JAXBContext jaxbContext;

	@Override
	protected Injector getInjector() {

		logger.info("Configuring the stamdata cpr web-service.");

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
			
			Properties cryptoSettings = setupCryptoProviderForJVM();
			
			final SOSIFactory sosiFactory = new SOSIFactory(new SOSIFederation(cryptoSettings, new InMemoryIntermediateCertificateCache()), new EmptyCredentialVault(), cryptoSettings);

			// XML SERIALIZATION

			jaxbContext = JAXBContext.newInstance(AuthorizationResponseStructure.class, AuthorizationRequestStructure.class);

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
					bind(new TypeLiteral<Set<String>>() {}).toInstance(whitelist);
					bind(AuthorizationDao.class);

					serve("/").with(WebService.class);
					filter("/").through(PersistenceFilter.class);
					
					logger.info("Done configuring the stamdata authorization web-service.");
				}

				@Provides
				@SuppressWarnings("unused")
				public Marshaller provideMarshaller() throws JAXBException
				{
					return jaxbContext.createMarshaller();
				}

				@Provides
				@SuppressWarnings("unused")
				public Unmarshaller provideUnmarshaller() throws JAXBException
				{
					return jaxbContext.createUnmarshaller();
				}
			});
		}
		catch (Exception e) {
		
			throw new IllegalStateException("Cannot start the authorization web service.", e);
		}
	}
}
