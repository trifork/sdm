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

import static dk.nsi.stamdata.cpr.Factories.TWO_DAYS_AGO;
import static dk.nsi.stamdata.cpr.Factories.YESTERDAY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;

import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.guice.GuiceTestRunner;
import dk.nsi.stamdata.jaxws.generated.CivilRegistrationNumberListPersonQueryType;
import dk.nsi.stamdata.jaxws.generated.DGWSFault;
import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.NamePersonQueryType;
import dk.nsi.stamdata.jaxws.generated.PersonLookupRequestType;
import dk.nsi.stamdata.jaxws.generated.PersonLookupResponseType;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookup;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookupService;

/**
 * This test class is ignored as it is only used for testing a deployed war
 */

@Ignore
@RunWith(GuiceTestRunner.class)
public class CallingJbossServerTest 
{
    public static final QName PVIT_SERVICE_QNAME = new QName("http://nsi.dk/2011/09/23/StamdataCpr/", "StamdataPersonLookupService");

    private List<Person> persons = Lists.newArrayList();
    private PersonLookupRequestType request = new PersonLookupRequestType();
    private PersonLookupResponseType response;

    public static final String WHITELISTED_CVR = "12345678";
    public static final String NON_WHITELISTED_CVR = "87654321";

    private static StamdataPersonLookup client;
    private static StamdataPersonLookupService serviceCatalog;

    private boolean isClientAuthority = false;
    
    @Inject
    private Session session;


    @Before
    public void setUp() throws Exception
    {
        URL wsdlLocation = new URL("http://localhost:8080/stamdata-cpr-ws/service/StamdataPersonLookup?wsdl");
        serviceCatalog = new StamdataPersonLookupService(wsdlLocation, PVIT_SERVICE_QNAME);

        // SEAL enforces that the XML prefixes are exactly
        // as it creates them. So we have to make sure we
        // don't change them.

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());

        client = serviceCatalog.getStamdataPersonLookup();
    }


    @Test(expected = SOAPFaultException.class)
    public void requestWithoutAnyQueryTypeGivesSenderSoapFault() throws Exception
    {
        request = new PersonLookupRequestType();

        prepareDatabaseAndSendRequest();
    }


    @Test(expected = SOAPFaultException.class)
    public void requestWithTwoQueryTypeGivesSenderSoapFault() throws Exception
    {
        request.setCivilRegistrationNumberPersonQuery("2805842569");

        NamePersonQueryType namePersonQueryType = new NamePersonQueryType();
        namePersonQueryType.setPersonGivenName("Thomas");
        namePersonQueryType.setPersonMiddleName("Greve");
        namePersonQueryType.setPersonSurnameName("Kristensen");
        request.setNamePersonQuery(namePersonQueryType);

        prepareDatabaseAndSendRequest();
    }


    @Test
    public void requestWithACprNumberNotPresentInDatabaseReturnsNothing() throws Exception
    {
        persons.add(Factories.createPersonWithCPR("2905853347"));

        request.setCivilRegistrationNumberPersonQuery("0103952595");
        
        prepareDatabaseAndSendRequest();

        assertThat(response.getPersonInformationStructure().size(), is(0));
    }


    @Test
    public void requestWithACprNumberPresentInDatabase() throws Exception
    {
        Person person = Factories.createPerson();

        persons.add(person);
        persons.add(Factories.createPerson());

        request.setCivilRegistrationNumberPersonQuery(person.getCpr());

        prepareDatabaseAndSendRequest();

        assertThat(response.getPersonInformationStructure().size(), is(1));
    }


    @Test
    public void requestWithSeveralCprNumbersNoneOfWhichAreInTheDatabase() throws Exception
    {
        persons.add(Factories.createPersonWithCPR("0000000000"));

        request.setCivilRegistrationNumberListPersonQuery(new CivilRegistrationNumberListPersonQueryType());
        request.getCivilRegistrationNumberListPersonQuery().getCivilRegistrationNumber().add("0206562469");
        request.getCivilRegistrationNumberListPersonQuery().getCivilRegistrationNumber().add("0302801961");

        prepareDatabaseAndSendRequest();

        assertThat(response.getPersonInformationStructure().size(), is(0));
    }


    @Test
    public void requestWithSeveralCprNumbersOfWhichSomeAreInTheDatabase() throws Exception
    {
        String EXISTING_CPR_1 = "0302801961";
        String EXISTING_CPR_2 = "0905852363";
        String NON_EXISTING_CPR = "0405852364";

        persons.add(Factories.createPersonWithCPR(EXISTING_CPR_1));
        persons.add(Factories.createPersonWithCPR(EXISTING_CPR_2));

        request.setCivilRegistrationNumberListPersonQuery(new CivilRegistrationNumberListPersonQueryType());
        request.getCivilRegistrationNumberListPersonQuery().getCivilRegistrationNumber().add(EXISTING_CPR_1);
        request.getCivilRegistrationNumberListPersonQuery().getCivilRegistrationNumber().add(EXISTING_CPR_2);
        request.getCivilRegistrationNumberListPersonQuery().getCivilRegistrationNumber().add(NON_EXISTING_CPR);

        prepareDatabaseAndSendRequest();

        assertEquals(2, response.getPersonInformationStructure().size());
    }


    @Test
    public void requestWithBirthDateNotFoundInDatabase() throws Exception
    {
        persons.add(Factories.createPersonWithBirthday(TWO_DAYS_AGO));

        XMLGregorianCalendar REQUESTED_BIRTHDAY = PersonMapper.newXMLGregorianCalendar(YESTERDAY);
        request.setBirthDatePersonQuery(REQUESTED_BIRTHDAY);

        prepareDatabaseAndSendRequest();

        assertTrue(response.getPersonInformationStructure().isEmpty());
    }


    @Test
    public void requestWithBirthDateFoundInDatabaseSeveralTimes() throws Exception
    {
        persons.add(Factories.createPersonWithBirthday(Factories.YEAR_2000));
        persons.add(Factories.createPersonWithBirthday(Factories.YEAR_2000));
        persons.add(Factories.createPersonWithBirthday(Factories.YEAR_1999));

        XMLGregorianCalendar birthday = PersonMapper.newXMLGregorianCalendar(Factories.YEAR_2000);
        request.setBirthDatePersonQuery(birthday);

        prepareDatabaseAndSendRequest();

        assertEquals(2, response.getPersonInformationStructure().size());
    }


    @Test
    public void requestWithNameNotFoundInDatabase() throws Exception
    {
        createPersonWithName("Peter", "Konrad", "Sørensen");
        createPersonWithName("Anders", null, "Thuesen");

        NamePersonQueryType value = new NamePersonQueryType();
        value.setPersonGivenName("Ragna");
        value.setPersonSurnameName("Brock");
        request.setNamePersonQuery(value);

        prepareDatabaseAndSendRequest();

        assertTrue(response.getPersonInformationStructure().isEmpty());
    }


    @Test
    public void requestWithNameFoundOnceInDatabase() throws Exception
    {
        createPersonWithName("Peter", "Konrad", "Sørensen");
        createPersonWithName("Thomas", "Greve", "Kristensen");

        NamePersonQueryType name = new NamePersonQueryType();
        name.setPersonGivenName("Thomas");
        name.setPersonSurnameName("Kristensen");
        request.setNamePersonQuery(name);

        prepareDatabaseAndSendRequest();

        assertEquals(1, response.getPersonInformationStructure().size());
    }


    @Test
    public void requestWithNameFoundSeveralTimesInDatabase() throws Exception
    {
        createPersonWithName("Peter", "Konrad", "Sørensen");
        createPersonWithName("Thomas", "Greve", "Kristensen");
        createPersonWithName("Peter", null, "Sørensen");

        NamePersonQueryType value = new NamePersonQueryType();
        value.setPersonGivenName("Peter");
        value.setPersonSurnameName("Sørensen");
        request.setNamePersonQuery(value);

        prepareDatabaseAndSendRequest();

        assertEquals(2, response.getPersonInformationStructure().size());
    }


    @Test
    public void requestWithNonWhitelistedCVRAndAPersonWithActiveProtectionShouldReturnCensoredData() throws Exception
    {
        Person person = Factories.createPersonWithAddressProtection();
        persons.add(person);

        request.setCivilRegistrationNumberPersonQuery(person.getCpr());

        prepareDatabaseAndSendRequest();

        String givenName = response.getPersonInformationStructure().get(0).getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName();
        assertThat(givenName, is("ADRESSEBESKYTTET"));
    }
    
    
    @Test
    public void requestWithWhitelistedCVRAndAPersonWithActiveProtectionShouldReturnRealData() throws Exception
    {
        isClientAuthority = true;
        
        Person person = Factories.createPersonWithAddressProtection();
        persons.add(person);

        request.setCivilRegistrationNumberPersonQuery(person.getCpr());

        prepareDatabaseAndSendRequest();

        String givenName = response.getPersonInformationStructure().get(0).getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName();
        assertThat(givenName, is(person.getFornavn()));
    }

    @Test
    public void testThatServiceIsAbleToHandleTwentySuccessiveRequests() throws Exception
    {
        isClientAuthority = true;

        Person person = Factories.createPersonWithAddressProtection();
        persons.add(person);

        request.setCivilRegistrationNumberPersonQuery(person.getCpr());

        prepareDatabaseAndSendRequest();
        
        for(int i = 0; i < 30; i++)
        {
            sendRequest();
        }
    }

    private Person createPersonWithName(String givenName, @Nullable String middleName, String surName)
    {
        Person person = Factories.createPerson();
        person.setFornavn(givenName);
        person.setMellemnavn(middleName);
        person.setEfternavn(surName);

        persons.add(person);

        return person;
    }


    private void prepareDatabaseAndSendRequest() throws Exception
    {
        Transaction t = session.beginTransaction();
        session.createQuery("DELETE FROM Person").executeUpdate();
        for (Person person : persons)
        {
            session.persist(person);
        }
        t.commit();

        sendRequest();
    }

    private void sendRequest() throws Exception, DGWSFault {
        Holder<Security> securityHeader;
        Holder<Header> medcomHeader;

        if (isClientAuthority)
        {
            SecurityWrapper secutityHeadersNotWhitelisted = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(WHITELISTED_CVR, "foo2", "bar2");
            securityHeader = secutityHeadersNotWhitelisted.getSecurity();
            medcomHeader = secutityHeadersNotWhitelisted.getMedcomHeader();
        }
        else
        {
            SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(NON_WHITELISTED_CVR, "foo", "bar");
            securityHeader = securityHeaders.getSecurity();
            medcomHeader = securityHeaders.getMedcomHeader();
        }

        response = client.getPersonDetails(securityHeader, medcomHeader, request);
    }
}
