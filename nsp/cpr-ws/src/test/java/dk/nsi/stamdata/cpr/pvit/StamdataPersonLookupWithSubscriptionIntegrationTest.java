/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package dk.nsi.stamdata.cpr.pvit;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Stage;
import com.trifork.stamdata.models.cpr.Person;
import dk.nsi.stamdata.cpr.ComponentController.ComponentModule;
import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.integrationtest.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SecurityWrapper;
import dk.nsi.stamdata.cpr.jaxws.SealNamespaceResolver;
import dk.nsi.stamdata.cpr.pvit.proxy.CprAbbsFacadeStubImplementation;
import dk.nsi.stamdata.cpr.pvit.proxy.CprAbbsStubJettyServer;
import dk.nsi.stamdata.cpr.ws.*;
import org.hibernate.Session;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class StamdataPersonLookupWithSubscriptionIntegrationTest extends AbstractWebAppEnvironmentJUnit4Test
{
	public static final QName PVIT_WITH_SUBSCRIPTIONS_SERVICE = new QName("http://nsi.dk/2011/09/23/StamdataCpr/", "StamdataPersonLookupWithSubscriptionService");

	public static final String REQUEST_CVR = "12345678";

	private StamdataPersonLookupWithSubscription client;

	@Inject
	private Session session;

	private Holder<Security> securityHolder;
	private Holder<Header> medcomHolder;
	private static final String EXAMPLE_CPR = "1111111111";
	private Set<Person> personsInDatabase;
	private CprAbbsStubJettyServer cprAbbsServer;

	@Before
	public void setUp() throws Exception
	{
		cprAbbsServer = new CprAbbsStubJettyServer();
		cprAbbsServer.startServer(8099);

		personsInDatabase = new HashSet<Person>();

		// Use Guice to inject dependencies.
		Guice.createInjector(Stage.DEVELOPMENT, new ComponentModule()).injectMembers(this);

		purgePersonTable();
		createExamplePersonsInDatabase();

		URL wsdlLocation = new URL("http://localhost:8100/service/StamdataPersonLookupWithSubscription?wsdl");
		StamdataPersonLookupWithSubscriptionService serviceCatalog = new StamdataPersonLookupWithSubscriptionService(wsdlLocation, PVIT_WITH_SUBSCRIPTIONS_SERVICE);

		// SEAL enforces that the XML prefixes are excatly
		// as it creates them. So we have to make sure we
		// don't change them.

		serviceCatalog.setHandlerResolver(new SealNamespaceResolver());

		client = serviceCatalog.getStamdataPersonLookupWithSubscription();

		SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(REQUEST_CVR, "foo", "bar");
		securityHolder = new Holder<Security>(securityHeaders.getSecurity());
		medcomHolder = new Holder<Header>(securityHeaders.getMedcomHeader());
	}


	@After
	public void tearDown() throws Exception
	{
		cprAbbsServer.stopServer();
		session.disconnect();
	}

	@Test
	public void returnsAllSubscribedPersonsForRequestWithoutSince() throws Exception
	{
		Map<String, List<String>> cprsToReturnForCvrs = new HashMap<String, List<String>>();
		cprsToReturnForCvrs.put(REQUEST_CVR, Arrays.asList(EXAMPLE_CPR, "0101821234"));
		CprAbbsFacadeStubImplementation.cprsToReturnForCvrs = cprsToReturnForCvrs;

		CprAbbsRequest request = new CprAbbsRequest();
		PersonLookupResponseType response = client.getSubscribedPersonDetails(securityHolder, medcomHolder, request);

		assertEquals(2, response.getPersonInformationStructure().size());

		assertPersonInReturnedResponseMatchesSomePersonFromDatabase(response.getPersonInformationStructure().get(0));
		assertPersonInReturnedResponseMatchesSomePersonFromDatabase(response.getPersonInformationStructure().get(1));
	}


	private void purgePersonTable()
	{
		session.createSQLQuery("TRUNCATE Person").executeUpdate();
	}


	private void createExamplePersonsInDatabase()
	{
		createPerson(EXAMPLE_CPR, "M", "8464", "Thomas", "Greve", "Kristensen", new DateTime(1982, 4, 15, 0, 0));
		createPerson("0101821234", "F", "8000", "Margit", "Greve", "Kristensen", new DateTime(1982, 1, 1, 0, 0));
		createPerson("0101821232", "F", "8100", "Margit", "Greve", "Kristensen", new DateTime(1929, 1, 1, 0, 0));
	}


	private Person createPerson(String cpr, String koen, String vejkode, String fornavn, String mellemnavn,
			String efternavn, DateTime foedselsdato)
	{
		Person person = Factories.createPersonWithoutAddressProtection();
		person.cpr = cpr;
		person.koen = koen;
		person.vejKode = vejkode;
		person.fornavn = fornavn;
		if (mellemnavn != null)
		{
			person.mellemnavn = mellemnavn;
		}
		person.efternavn = efternavn;
		person.foedselsdato = foedselsdato.toDate();

		person.setModifiedDate(new Date());
		person.setCreatedDate(new Date());
		person.setValidFrom(DateTime.now().minusDays(1).toDate());
		person.setValidTo(DateTime.now().plusDays(1).toDate());

		savePerson(person);

		return person;
	}


	private Person savePerson(Person person)
	{
		personsInDatabase.add(person);

		session.getTransaction().begin();

		session.save(person);

		session.getTransaction().commit();

		return person;
	}


	private void assertPersonInReturnedResponseMatchesSomePersonFromDatabase(PersonInformationStructureType information)
	{
		for (Person person : personsInDatabase) {
			if (person.getCpr().equals(information.getRegularCPRPerson().getSimpleCPRPerson()
					.getPersonCivilRegistrationIdentifier()) &&
				person.getVejKode().equals(information.getPersonAddressStructure().getAddressComplete().getAddressAccess()
					.getStreetCode())) {
				return;
			}
		}

		fail("No person i database matches " + information.getRegularCPRPerson().getSimpleCPRPerson().getPersonCivilRegistrationIdentifier() + " in response");
	}
}
