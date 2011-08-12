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

package com.trifork.stamdata.authorization;

import static dk.sosi.seal.model.SignatureUtil.*;
import static org.slf4j.LoggerFactory.*;

import java.util.*;

import javax.xml.bind.*;

import org.slf4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.*;
import com.google.inject.servlet.*;
import com.trifork.stamdata.ConfigurationLoader;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.pki.*;
import dk.sosi.seal.vault.EmptyCredentialVault;


public class ApplicationContextListener extends GuiceServletContextListener
{
	private static final Logger logger = getLogger(ApplicationContextListener.class);

	private static final String CONFIGURATION_NAME = "stamdata-authorization-lookup-ws";

	private static final String DGWS_TEST_SECURITY = "dgwsTest";

	private JAXBContext jaxbContext;

	@Override
	protected Injector getInjector()
	{
		logger.info("Configuring the stamdata authorization web-service.");

		try
		{
			final Properties properties = ConfigurationLoader.getForComponent(CONFIGURATION_NAME);

			// READ THE SUBJECT SERIAL NUMBERS OF CLIENTS
			//
			// These numbers are used to allow access to the system.
			// The list contains both CVR and UID but we only need
			// the CVR (for now). This may change in future releases.

			String whiteListProperty = properties.getProperty("subjectSerialNumbers", "");

			Set<String> cvrNumbers = Sets.newHashSet();

			for (String item : whiteListProperty.split(","))
			{
				cvrNumbers.add(item.split("-")[0].split(":")[1]);
			}

			final Set<String> whitelist = Sets.newHashSet(cvrNumbers.toArray(new String[] {}));

			// SEAL

			Properties cryptoSettings = setupCryptoProviderForJVM();

			Federation federation;

			if (DGWS_TEST_SECURITY.equalsIgnoreCase(properties.getProperty("security")))
			{
				logger.warn("Allowing ID Cards from the Test STS!");

				federation = new SOSITestFederation(cryptoSettings, new InMemoryIntermediateCertificateCache());
			}
			else
			{
				federation = new SOSIFederation(cryptoSettings, new InMemoryIntermediateCertificateCache());
			}

			final SOSIFactory sosiFactory = new SOSIFactory(federation, new EmptyCredentialVault(), cryptoSettings);

			// XML SERIALIZATION

			jaxbContext = JAXBContext.newInstance(AuthorizationResponseStructure.class, AuthorizationRequestStructure.class);
			
			// BIND THE DEPENDENCIES
			//
			// All these dependencies are type safe so we just bind them
			// to the instances.

			return Guice.createInjector(new ServletModule()
			{
				@Override
				protected void configureServlets()
				{
					install(new DatabaseModule(properties.getProperty("db.connection.jdbcURL"), properties.getProperty("db.connection.username"), properties.getProperty("db.connection.password", "")));
					install(new MonitoringModule());
					
					bind(SOSIFactory.class).toInstance(sosiFactory);
					bind(new TypeLiteral<Set<String>>() {}).toInstance(whitelist);
					bind(AuthorizationDao.class);

					serve("/").with(WebService.class);
					filter("/*").through(PersistenceFilter.class);

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
		catch (Exception e)
		{
			throw new IllegalStateException("Cannot start the authorization lookup web-service.", e);
		}
	}
}
