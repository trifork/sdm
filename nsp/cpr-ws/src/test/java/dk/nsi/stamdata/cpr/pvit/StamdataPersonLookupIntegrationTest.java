package dk.nsi.stamdata.cpr.pvit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.hibernate.Session;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Stage;
import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.dgws.DgwsIdcardFilter;
import dk.nsi.stamdata.cpr.ComponentController.ComponentModule;
import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SealNamespacePrefixSoapHandler;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SecurityWrapper;
import dk.nsi.stamdata.cpr.integrationtest.dgws.TestSTSMock;
import dk.nsi.stamdata.cpr.ws.CivilRegistrationNumberListPersonQueryType;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.NamePersonQueryType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonLookupRequestType;
import dk.nsi.stamdata.cpr.ws.PersonLookupResponseType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookup;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookupService;

public class StamdataPersonLookupIntegrationTest extends AbstractWebAppEnvironmentJUnit4Test {

    private static final String EXAMPLE_CPR = "1111111111";
    public static final QName PVIT_SERVICE = new QName("http://nsi.dk/2011/09/23/StamdataCpr/",
            "StamdataPersonLookupService");
    public static final String CVR_WHITELISTED = "12345678";
    public static final String CVR_NOT_WHITELISTED = "87654321";

    private StamdataPersonLookup client;

    @Inject
    private Session session;
    private Holder<Security> securityHolder;
    private Holder<Header> medcomHolder;

    @BeforeClass
    public static void setIdcardFilterInTestMode() {
        // TODO: Comment why is this needed.
        System.setProperty(DgwsIdcardFilter.USE_TEST_FEDERATION_INIT_PARAM_KEY, "true");
    }

    @Before
    public void setUp() throws Exception {
        // Use Guice to inject dependencies.
        Guice.createInjector(Stage.DEVELOPMENT, new ComponentModule()).injectMembers(this);

        purgePersonTable();
        createExamplePersonInDatabase();

        URL wsdlLocation = new URL("http://localhost:8100/service/StamdataPersonLookup?wsdl");
        StamdataPersonLookupService serviceCatalog = new StamdataPersonLookupService(wsdlLocation, PVIT_SERVICE);

        // TODO: Comment why this resolver is needed.
        serviceCatalog.setHandlerResolver(new HandlerResolver() {
            @Override
            @SuppressWarnings("rawtypes")
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                return Lists.newArrayList((Handler) new SealNamespacePrefixSoapHandler());
            }
        });

        client = serviceCatalog.getStamdataPersonLookup();

        SecurityWrapper securityHeaders = TestSTSMock.getVocesTrustedSecurityWrapper(CVR_WHITELISTED, "foo", "bar");
        securityHolder = new Holder<Security>(securityHeaders.getSecurity());
        medcomHolder = new Holder<Header>(securityHeaders.getMedcomHeader());

    }

    @After
    public void tearDown() throws Exception {
        session.disconnect();
    }

    @Test
    public void requestWithoutAnyQueryTypeGivesSenderSoapFault() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();
        try {
            client.getPersonDetails(securityHolder, medcomHolder, query);
            fail("Expected DGWSFault");
        } catch (DGWSFault fault) {
            assertEquals("processing_problem", fault.getMessage());
        }
    }

    @Test
    public void requestWithTwoQueryTypeGivesSenderSoapFault() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();
        query.setCivilRegistrationNumberPersonQuery("1234567890");
        NamePersonQueryType namePersonQueryType = new NamePersonQueryType();
        namePersonQueryType.setPersonGivenName("Thomas");
        namePersonQueryType.setPersonMiddleName("Greve");
        namePersonQueryType.setPersonSurnameName("Kristensen");
        query.setNamePersonQuery(namePersonQueryType);

        try {
            client.getPersonDetails(securityHolder, medcomHolder, query);
            fail("Expected DGWSFault");
        } catch (DGWSFault fault) {
            assertEquals("processing_problem", fault.getMessage());
        }
    }

    @Test
    public void requestWithACprNumberNotPresentInDatabase() throws Exception {
        String decoyCpr = "1234567890";

        PersonLookupRequestType query = new PersonLookupRequestType();
        query.setCivilRegistrationNumberPersonQuery(decoyCpr);

        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);

        assertEquals(0, response.getPersonInformationStructure().size());
    }

    @Test
    public void requestWithACprNumberPresentInDatabase() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();
        query.setCivilRegistrationNumberPersonQuery(EXAMPLE_CPR);

        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);

        assertEquals(1, response.getPersonInformationStructure().size());

        PersonInformationStructureType information = response.getPersonInformationStructure().get(0);
        assertReturnedResponseMatchesPersonFromDatabase(information);
    }

    @Test
    public void requestWithSeveralCprNumbersNoneOfWhichAreInTheDatabase() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();
        CivilRegistrationNumberListPersonQueryType list = new CivilRegistrationNumberListPersonQueryType();
        query.setCivilRegistrationNumberListPersonQuery(list);
        list.getCivilRegistrationNumber().add("1234567890");
        list.getCivilRegistrationNumber().add("0987654321");

        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);

        assertEquals(0, response.getPersonInformationStructure().size());
    }

    @Test
    public void requestWithSeveralCprNumbersOfWhichOneIsInTheDatabase() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();
        CivilRegistrationNumberListPersonQueryType list = new CivilRegistrationNumberListPersonQueryType();
        query.setCivilRegistrationNumberListPersonQuery(list);
        list.getCivilRegistrationNumber().add("1234567890");
        list.getCivilRegistrationNumber().add(EXAMPLE_CPR);
        list.getCivilRegistrationNumber().add("0987654321");

        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);

        assertEquals(1, response.getPersonInformationStructure().size());
        assertReturnedResponseMatchesPersonFromDatabase(response.getPersonInformationStructure().get(0));
    }

    @Test
    public void requestWithBirthDateNotFoundInDatabase() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();

        DateTime dateTime = new DateTime(1982, 5, 1, 0, 0, 0);
        XMLGregorianCalendar cal = PersonMapper.newXMLGregorianCalendar(dateTime.toDate());
        query.setBirthDatePersonQuery(cal);
        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);
        assertTrue("A person was not expected to exist with this birth date " + dateTime.toString(), response
                .getPersonInformationStructure().isEmpty());
    }

    @Test
    public void requestWithBirthDateFoundInDatabase() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();

        DateTime dateTime = new DateTime(1982, 4, 15, 0, 0, 0);
        XMLGregorianCalendar cal = PersonMapper.newXMLGregorianCalendar(dateTime.toDate());
        query.setBirthDatePersonQuery(cal);
        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);
        assertEquals("A person was expected to exist with this birth date " + dateTime.toString(), 1, response
                .getPersonInformationStructure().size());
        assertReturnedResponseMatchesPersonFromDatabase(response.getPersonInformationStructure().get(0));
    }

    @Test
    public void requestWithBirthDateFoundInDatabaseSeveralTimes() throws Exception {
        createPerson("1504823210", "M", "8484", "Birger", null, "Thomsen", new DateTime(1982, 4, 15, 0, 0, 0));

        PersonLookupRequestType query = new PersonLookupRequestType();

        DateTime dateTime = new DateTime(1982, 4, 15, 0, 0, 0);
        XMLGregorianCalendar cal = PersonMapper.newXMLGregorianCalendar(dateTime.toDate());
        query.setBirthDatePersonQuery(cal);
        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);
        assertEquals("Two people was expected to exist with this birth date " + dateTime.toString(), 2, response
                .getPersonInformationStructure().size());
    }

    @Test
    public void requestWithNameNotFoundInDatabase() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();
        NamePersonQueryType value = new NamePersonQueryType();
        value.setPersonGivenName("Ragna");
        value.setPersonSurnameName("Brock");
        query.setNamePersonQuery(value);

        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);
        assertTrue(
                "A person was NOT expected to exist with this name " + value.getPersonGivenName() + " "
                        + value.getPersonMiddleName() + " " + value.getPersonSurnameName(), response
                        .getPersonInformationStructure().isEmpty());
    }

    @Test
    public void requestWithNameFoundOnceInDatabase() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();
        NamePersonQueryType value = new NamePersonQueryType();
        value.setPersonGivenName("Thomas");
        value.setPersonSurnameName("Kristensen");
        query.setNamePersonQuery(value);

        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);
        assertEquals(
                "A person was expected to exist with this name " + value.getPersonGivenName() + " "
                        + value.getPersonMiddleName() + " " + value.getPersonSurnameName(), 1, response
                        .getPersonInformationStructure().size());
        assertReturnedResponseMatchesPersonFromDatabase(response.getPersonInformationStructure().get(0));
    }

    @Test
    public void requestWithNameFoundSeveralTimesInDatabase() throws Exception {
        PersonLookupRequestType query = new PersonLookupRequestType();
        NamePersonQueryType value = new NamePersonQueryType();
        value.setPersonMiddleName("Greve");
        value.setPersonSurnameName("Kristensen");
        query.setNamePersonQuery(value);

        PersonLookupResponseType response = client.getPersonDetails(securityHolder, medcomHolder, query);
        assertEquals(
                "Two people was expected to exist with this name " + value.getPersonGivenName() + " "
                        + value.getPersonMiddleName() + " " + value.getPersonSurnameName(), 2, response
                        .getPersonInformationStructure().size());
    }

    private void purgePersonTable() {
        session.createSQLQuery("TRUNCATE Person").executeUpdate();
    }

    private void createExamplePersonInDatabase() {
        createPerson(EXAMPLE_CPR, "M", "8464", "Thomas", "Greve", "Kristensen", new DateTime(1982, 4, 15, 0, 0));
        createPerson("0101821234", "F", "8000", "Margit", "Greve", "Kristensen", new DateTime(1982, 1, 1, 0, 0));
    }

    private Person createPerson(String cpr, String koen, String vejkode, String fornavn, String mellemnavn,
            String efternavn, DateTime foedselsdato) {
        session.getTransaction().begin();

        Person person = Factories.createPersonWithoutAddressProtection();
        person.cpr = cpr;
        person.koen = koen;
        person.vejKode = vejkode;
        person.fornavn = fornavn;
        if (mellemnavn != null) {
            person.mellemnavn = mellemnavn;
        }
        person.efternavn = efternavn;
        person.foedselsdato = foedselsdato.toDate();
        ;
        person.setModifiedDate(new Date());
        person.setCreatedDate(new Date());
        person.setValidFrom(DateTime.now().minusDays(1).toDate());
        person.setValidTo(DateTime.now().plusDays(1).toDate());
        session.save(person);
        session.flush();
        session.getTransaction().commit();

        return person;
    }

    private void assertReturnedResponseMatchesPersonFromDatabase(PersonInformationStructureType information) {
        assertEquals(EXAMPLE_CPR, information.getRegularCPRPerson().getSimpleCPRPerson()
                .getPersonCivilRegistrationIdentifier());
        assertEquals("8464", information.getPersonAddressStructure().getAddressComplete().getAddressAccess()
                .getStreetCode());
    }
}
