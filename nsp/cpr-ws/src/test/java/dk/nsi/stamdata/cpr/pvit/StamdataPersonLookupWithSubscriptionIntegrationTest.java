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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

import org.hibernate.Session;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Stage;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;

import dk.nsi.stamdata.cpr.ComponentController.ComponentModule;
import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.pvit.proxy.CprAbbsFacadeStubImplementation;
import dk.nsi.stamdata.cpr.pvit.proxy.CprAbbsStubJettyServer;
import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.jaxws.generated.CprAbbsRequestType;
import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.PersonLookupResponseType;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookupWithSubscription;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookupWithSubscriptionService;


public class StamdataPersonLookupWithSubscriptionIntegrationTest extends AbstractWebAppEnvironmentJUnit4Test
{
	public static final QName PVIT_WITH_SUBSCRIPTIONS_SERVICE = new QName("http://nsi.dk/2011/09/23/StamdataCpr/", "StamdataPersonLookupWithSubscriptionService");
	public static final String CLIENT_CVR = "12345678";

    private static final int PORT = 8190;
    
	@Inject
	private Session session;

	private CprAbbsStubJettyServer cprAbbsServer;
	
	private List<Person> persons = Lists.newArrayList();
	
	private CprAbbsRequestType request = new CprAbbsRequestType();
	private PersonLookupResponseType response;

	private static final String CHANGED_PERSON_CPR1 = "0101822231";
	private static final String CHANGED_PERSON_CPR2 = "0101821234";
	private static final String OTHER_CPR = "2705842246";
	private static final String MESSAGE_ID = "42foobar";
    private Holder<Header> medcomHeader;


	@Before
	public void setUp() throws Exception
	{
        Guice.createInjector(Stage.DEVELOPMENT, new ComponentModule()).injectMembers(this);

	    cprAbbsServer = new CprAbbsStubJettyServer();
		cprAbbsServer.startServer(PORT);
		
		Map<String, List<String>> cprsToReturnForCvrs = Maps.newHashMap();
	    cprsToReturnForCvrs.put(CLIENT_CVR, Lists.newArrayList(CHANGED_PERSON_CPR1, CHANGED_PERSON_CPR2));
	    CprAbbsFacadeStubImplementation.cprsToReturnForCvrs = cprsToReturnForCvrs;
	}


	@After
	public void tearDown() throws Exception
	{
		cprAbbsServer.stopServer();
	}


	@Test
	public void returnsAllSubscribedPersonsForRequestWithoutSince() throws Exception
	{
	    persons.add(Factories.createPersonWithCPR(CHANGED_PERSON_CPR1));
	    persons.add(Factories.createPersonWithCPR(CHANGED_PERSON_CPR2));
	    persons.add(Factories.createPersonWithCPR(OTHER_CPR));
	    
		sendRequest();

		assertThat(response.getPersonInformationStructure().size(), is(2));
		
		assertThat(response.getPersonInformationStructure().get(0).getRegularCPRPerson().getSimpleCPRPerson().getPersonCivilRegistrationIdentifier(), is(CHANGED_PERSON_CPR1));
	    assertThat(response.getPersonInformationStructure().get(1).getRegularCPRPerson().getSimpleCPRPerson().getPersonCivilRegistrationIdentifier(), is(CHANGED_PERSON_CPR2));
	    
	    assertThat(medcomHeader.value.getLinking().getInResponseToMessageID(), is(MESSAGE_ID));
	}


	private void sendRequest() throws Exception
	{
	    session.getTransaction().begin();
		session.createQuery("DELETE FROM Person").executeUpdate();

        for (Person person : persons)
        {
            session.persist(person);
        }

        session.getTransaction().commit();
        
        URL wsdlLocation = new URL("http://localhost:" + PORT + "/service/StamdataPersonLookupWithSubscription?wsdl");
        StamdataPersonLookupWithSubscriptionService serviceCatalog = new StamdataPersonLookupWithSubscriptionService(wsdlLocation, PVIT_WITH_SUBSCRIPTIONS_SERVICE);
        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());
        
        SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(CLIENT_CVR, "foo", "bar");
        StamdataPersonLookupWithSubscription client = serviceCatalog.getStamdataPersonLookupWithSubscription();
        
        medcomHeader = securityHeaders.getMedcomHeader();
        medcomHeader.value.getLinking().setMessageID(MESSAGE_ID);
        
        response = client.getSubscribedPersonDetails(securityHeaders.getSecurity(), medcomHeader, request);
    }
}
