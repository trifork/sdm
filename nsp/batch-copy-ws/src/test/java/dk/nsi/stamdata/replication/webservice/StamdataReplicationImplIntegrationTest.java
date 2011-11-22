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
package dk.nsi.stamdata.replication.webservice;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;
import com.trifork.stamdata.persistence.*;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;
import dk.nsi.stamdata.jaxws.generated.*;
import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.testing.TestServer;
import dk.nsi.stamdata.views.Views;
import dk.nsi.stamdata.views.cpr.Person;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(GuiceTestRunner.class)
public class StamdataReplicationImplIntegrationTest
{
    public static final String WHITELISTED_CVR = "12345678";
    public static final String NON_WHITELISTED_CVR = "87654321";
    private boolean isClientAuthority = false;

    private TestServer server;
    private StamdataReplication client;

    private ReplicationRequestType request;
    private ReplicationResponseType response;
    private Element anyAsElement;

    @Inject
    private Session session;
    @Inject
    private ClientDao clientDao;

    private List<Person> persons = Lists.newArrayList();
    private RecordSpecification recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;
    private List<Record> records = Lists.newArrayList();
    private DateTime now, lastYear, nextYear;

    @Before
    public void setUp() throws Exception
    {
        server = new TestServer().port(8986).contextPath("/").start();

        URL wsdlLocation = new URL("http://localhost:8986/service/StamdataReplication?wsdl");
        QName serviceName = new QName("http://nsi.dk/2011/10/21/StamdataKrs/", "StamdataReplicationService");
        StamdataReplicationService service = new StamdataReplicationService(wsdlLocation, serviceName);

        service.setHandlerResolver(new SealNamespaceResolver());
        client = service.getStamdataReplication();

        now = DateTime.now();
        lastYear = now.minusYears(1);
        nextYear = now.plusYears(1);

        isClientAuthority = true;
    }

    @After
    public void tearDown() throws Exception
    {
        server.stop();
    }

    // Tests

    @Test(expected = SOAPFaultException.class)
    public void requestWithoutAnyReplicationTypeGivesSenderSoapFault() throws Exception
    {

        request = new ReplicationRequestType();
        populateDatabaseAndSendRequest();
    }

    @Test
    public void basicQueryDoesNotRaiseAnException() throws Exception
    {

        createCprPersonRegisterReplicationRequest();
        populateDatabaseAndSendRequest();
    }

    @Test(expected = ReplicationFault.class)
    public void cvrThatIsNotWhitelistedRaisesAnException() throws Exception
    {

        createCprPersonRegisterReplicationRequest();
        isClientAuthority = false;
        populateDatabaseAndSendRequest();
    }

    @Test(expected = ReplicationFault.class)
    public void cvrThatIsWhitelistedForAnotherRegisterRaisesAnException() throws Exception
    {

        createSorHospitalRegisterReplicationRequest();
        populateDatabaseAndSendRequest();
    }

    @Test(expected = ReplicationFault.class)
    public void requestToAnUnexistingRegisterAndDatatypeRaisesAnException() throws Exception
    {

        createUnexistingRegisterAndDatatypeReplicationRequest();
        populateDatabaseAndSendRequest();
    }

    @Test(expected = ReplicationFault.class)
    public void requestToAnExistingRegisterButUnexistingDatatypeRaisesAnException() throws Exception
    {

        createExistingRegisterButUnexistingDatatypeReplicationRequest();
        populateDatabaseAndSendRequest();
    }

    @Test
    public void basicQueryOnEmptyDatabaseReturnsZeroResults() throws Exception
    {

        createCprPersonRegisterReplicationRequest();
        populateDatabaseAndSendRequest();
        assertResponseContainsCprAtom();
        assertResponseContainsExactNumberOfRecords("sdm:person", 0);
    }

    @Test
    public void basicQueryOnNonEmptyDatabaseReturnsOneResult() throws Exception
    {

        createCprPersonRegisterReplicationRequest();

        Person person = createDonaldDuckPerson(now, lastYear, nextYear);
        persons.add(person);

        populateDatabaseAndSendRequest();

        assertResponseContainsCprAtom();

        assertResponseContainsExactNumberOfRecords("sdm:person", 1);

        String fornavn = "Joakim";
        String efternavn = "And";
        assertResponseContainsPersonWithSurNameMatchingGivenName(fornavn, efternavn);
    }

    @Test
    public void personsArePagedAccordingToParameter() throws Exception
    {

        createCprPersonRegisterReplicationRequest();

        Person a = createDonaldDuckPerson(now, lastYear, nextYear);
        a.cpr = "1111111111";
        persons.add(a);

        Person b = createDonaldDuckPerson(now, lastYear, nextYear);
        b.cpr = "2222222222";
        persons.add(b);

        request.setOffset("0");
        request.setMaxRecords(2L);

        populateDatabaseAndSendRequest();
        assertResponseContainsCprAtom();
        assertResponseContainsExactNumberOfRecords("sdm:person", 2);
    }

    @Test
    public void personsAreCorrectlyPaged() throws Exception
    {

        createCprPersonRegisterReplicationRequest();

        DateTime yesterday = now.minusDays(1);

        Person a = createDonaldDuckPerson(yesterday, lastYear, nextYear);
        a.cpr = "1111111111";
        persons.add(a);

        Person b = createDonaldDuckPerson(yesterday, lastYear, nextYear);
        b.cpr = "2222222222";
        persons.add(b);

        Person c = createDonaldDuckPerson(now, lastYear, nextYear);
        c.cpr = "3333333333";
        persons.add(c);

        request.setOffset("0");
        request.setMaxRecords(1L);

        populateDatabaseAndSendRequest();
        assertResponseContainsCprAtom();
        assertResponseContainsExactNumberOfRecords("sdm:person", 1);
        assertResponseContainsPersonWithCpr("1111111111");

        request.setOffset(getOffsetFromAtomEntry());
        sendRequest();
        assertResponseContainsCprAtom();
        assertResponseContainsExactNumberOfRecords("sdm:person", 1);
        assertResponseContainsPersonWithCpr("2222222222");

        request.setOffset(getOffsetFromAtomEntry());
        sendRequest();
        assertResponseContainsCprAtom();
        assertResponseContainsExactNumberOfRecords("sdm:person", 1);
        assertResponseContainsPersonWithCpr("3333333333");
    }

    @Test
    public void testSikredeCopy() throws Exception
    {
        RecordBuilder builder = new RecordBuilder(SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        builder.field("CPRnr", "1234567890");
        Record record = builder.addDummyFieldsAndBuild();
        records.add(record);

        createSikredeReplicationRequest("sikrede", "sikrede");
        populateDatabaseAndSendRequest();

        assertResponseContainsRecordAtom("sikrede", "sikrede");
        assertResponseContainsExactNumberOfRecords("sikrede:sikrede", 1);
        assertResponseContainsValueOnXPath("//sikrede:sikrede/sikrede:CPRnr", "1234567890");
    }

     @Test
    public void testYderregisterCopy() throws Exception
    {
        recordSpecification = YderregisterRecordSpecs.YDER_RECORD_TYPE;
        Record record = new RecordBuilder(YderregisterRecordSpecs.YDER_RECORD_TYPE).field("HistIdYder", "1234567890123456").addDummyFieldsAndBuild();
        records.add(record);

        createSikredeReplicationRequest("yderregister", "yder");

        populateDatabaseAndSendRequest();

        assertResponseContainsRecordAtom("yderregister", "yder");
        assertResponseContainsExactNumberOfRecords("yder:yder", 1);
        assertResponseContainsValueOnXPath("//yder:yder/yder:HistIdYder", "1234567890123456");
    }

    @Test
   public void testYderregisterPersonCopy() throws Exception
   {
       recordSpecification = YderregisterRecordSpecs.PERSON_RECORD_TYPE;
       Record record = new RecordBuilder(recordSpecification).field("HistIdPerson", "1234").addDummyFieldsAndBuild();
       records.add(record);

       createSikredeReplicationRequest("yderregister", "person");

       populateDatabaseAndSendRequest();

       assertResponseContainsRecordAtom("yderregister", "person");
       assertResponseContainsExactNumberOfRecords("yder:person", 1);
       assertResponseContainsValueOnXPath("//yder:person/yder:HistIdPerson", "1234");
   }

    public String anyElementAsString() throws TransformerException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(anyAsElement), new StreamResult(byteArrayOutputStream));
        return new String(byteArrayOutputStream.toByteArray());
    }

    @Test
    public void testThatWeAreAbleToHandleMoreThanTwentyRequestsForWsdlInSuccession() throws Exception
    {
        URL wsdlLocation = new URL("http://localhost:8986/service/StamdataReplication?wsdl");
        QName serviceName = new QName("http://nsi.dk/2011/10/21/StamdataKrs/", "StamdataReplicationService");

        for (int i = 0; i < 20; i++)
        {
            StamdataReplicationService service = new StamdataReplicationService(wsdlLocation, serviceName);

            service.setHandlerResolver(new SealNamespaceResolver());
            client = service.getStamdataReplication();
        }
    }

    @Test
    public void testThatWeAreAbleToHandleMoreThanTwentyConnectionsInSuccession() throws Exception
    {
        createCprPersonRegisterReplicationRequest();
        populateDatabaseAndSendRequest();
        for (int i = 0; i < 20; i++)
        {
            sendRequest();
        }
    }

    // Helper methods

    private void createCprPersonRegisterReplicationRequest()
    {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("person");
        request.setVersion(1L);
        request.setOffset("0");
    }

    private void createSikredeReplicationRequest(String register, String datatype)
    {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister(register);
        request.setDatatype(datatype);
        request.setVersion(1L);
        request.setOffset("0");
    }

    private void createSorHospitalRegisterReplicationRequest()
    {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("sor");
        request.setDatatype("sygehus");
        request.setVersion(1L);
        request.setOffset("0");
    }

    private void createUnexistingRegisterAndDatatypeReplicationRequest()
    {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("foo");
        request.setDatatype("bar");
        request.setVersion(1L);
        request.setOffset("0");
    }

    private void createExistingRegisterButUnexistingDatatypeReplicationRequest()
    {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("sor");
        request.setDatatype("bar");
        request.setVersion(1L);
        request.setOffset("0");
    }

    private void assertResponseContainsCprAtom()
    {
        assertThat(anyAsElement.getLocalName(), is("feed"));
        assertThat(anyAsElement.getNamespaceURI(), is("http://www.w3.org/2005/Atom"));
        assertThat(anyAsElement.getFirstChild().getFirstChild().getTextContent(), is("tag:trifork.com,2011:cpr/person/v1"));
    }

    private void assertResponseContainsRecordAtom(String register, String datatype)
    {
        assertThat(anyAsElement.getLocalName(), is("feed"));
        assertThat(anyAsElement.getNamespaceURI(), is("http://www.w3.org/2005/Atom"));
        assertThat(anyAsElement.getFirstChild().getFirstChild().getTextContent(), is("tag:trifork.com,2011:" + register + "/" + datatype + "/v1"));
    }

    private void assertResponseContainsPersonWithSurNameMatchingGivenName(String givenName, String surName) throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression("//sdm:person[sdm:fornavn='" + givenName + "']/sdm:efternavn");
        String result = expression.evaluate(anyAsElement);
        assertThat(result, is(surName));
    }

    private void assertResponseContainsExactNumberOfRecords(String tag, int n) throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression("count(//" + tag + ")");
        String countAsString = expression.evaluate(anyAsElement);
        assertThat(Integer.parseInt(countAsString), is(n));
    }

    private void assertResponseContainsPersonWithCpr(String oracleCpr) throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression("//sdm:person/sdm:cpr");
        String result = expression.evaluate(anyAsElement);
        assertThat(result, is(oracleCpr));
    }

    private void assertResponseContainsValueOnXPath(String xPath, String oracle) throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression(xPath);
        String result = expression.evaluate(anyAsElement);
        assertThat(result, is(oracle));
    }

    private String getOffsetFromAtomEntry() throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression("//atom:entry/atom:id");
        String atomId = expression.evaluate(anyAsElement);
        String[] tokens = atomId.split("/");
        return tokens[tokens.length - 1];
    }

    private XPathExpression createXpathExpression(String expression) throws XPathExpressionException
    {
        NamespaceContext context = new NamespaceContextMap(
                "krs", "http://nsi.dk/2011/10/21/StamdataKrs/",
                "atom", "http://www.w3.org/2005/Atom",
                "sdm", "http://trifork.com/-/stamdata/3.0/cpr",
                "sikrede", "http://trifork.com/-/stamdata/3.0/sikrede",
                "yder", "http://trifork.com/-/stamdata/3.0/yderregister");
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(context);
        return xpath.compile(expression);
    }

    private void populateDatabaseAndSendRequest() throws Exception
    {
        populateDatabase();
        sendRequest();
    }

    private void populateDatabase() throws SQLException
    {
        Transaction t = session.beginTransaction();

        session.createQuery("DELETE FROM Client").executeUpdate();
        session.createSQLQuery("DELETE FROM Client_permissions").executeUpdate();

        // Example of subject serial number: CVR:19343634-UID:1234
        Client cvrClient = clientDao.create("Region Syd", String.format("CVR:%s-UID:1234", WHITELISTED_CVR));
        cvrClient.addPermission(Views.getViewPath(Person.class));
        cvrClient.addPermission("sikrede/sikrede/v1");
        cvrClient.addPermission("yderregister/yder/v1");
        cvrClient.addPermission("yderregister/person/v1");
        session.persist(cvrClient);

        session.createQuery("DELETE FROM Person").executeUpdate();
        for (Person person : persons)
        {
            session.persist(person);
        }

        Connection connection = session.connection();
        connection.createStatement().executeUpdate("DROP TABLE IF EXISTS " + recordSpecification.getTable());
        connection.createStatement().executeUpdate(RecordMySQLTableGenerator.createSqlSchema(recordSpecification));
        RecordPersister recordPersister = new RecordPersister(connection, new Instant());

        for (Record sikredeRecord : records)
        {
            recordPersister.persist(sikredeRecord, recordSpecification);
        }

        t.commit();
    }

    private void sendRequest() throws Exception, ReplicationFault
    {
        Holder<Security> securityHeader;
        Holder<Header> medcomHeader;

        if (isClientAuthority)
        {
            SecurityWrapper secutityHeadersNotWhitelisted = DGWSHeaderUtil
                    .getVocesTrustedSecurityWrapper(WHITELISTED_CVR);
            securityHeader = secutityHeadersNotWhitelisted.getSecurity();
            medcomHeader = secutityHeadersNotWhitelisted.getMedcomHeader();
        } else
        {
            SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(NON_WHITELISTED_CVR);
            securityHeader = securityHeaders.getSecurity();
            medcomHeader = securityHeaders.getMedcomHeader();
        }

        response = client.replicate(securityHeader, medcomHeader, request);
        anyAsElement = (Element) response.getAny();
    }

    // StamdataPersonLookupIntegrationTest contains a "Factories" class that we
    // would rather use
    private Person createDonaldDuckPerson(DateTime modifiedDate, DateTime validFrom, DateTime validTo)
    {
        String cpr = "0102451234";
        String koen = "M";
        String fornavn = "Joakim";
        String mellemnavn = "von";
        String efternavn = "And";
        String coNavn = "Andersine And";
        String lokalitet = "Pengetanken";
        String vejnavn = "Ligustervænget";
        String bygningsnummer = "42";
        String husnummer = "123";
        String etage = "12";
        String sideDoerNummer = "th.";
        String bynavn = "Andeby";
        BigInteger postnummer = new BigInteger("8000");
        String postdistrikt = "Gåserød";
        String status = "01";
        String gaeldendeCPR = "3105459876";
        Date foedselsdato = new DateTime(1947, 12, 24, 0, 0).toDate();
        String stilling = "Gnier";
        BigInteger vejKode = new BigInteger("740");
        BigInteger kommuneKode = new BigInteger("314");
        Date navnebeskyttelseslettedato = null;
        Date navnebeskyttelsestartdato = null;
        Person person = new Person(cpr, koen, fornavn, mellemnavn, efternavn, coNavn, lokalitet, vejnavn,
                bygningsnummer, husnummer, etage, sideDoerNummer, bynavn, postnummer, postdistrikt, status,
                gaeldendeCPR, foedselsdato, stilling, vejKode, kommuneKode, modifiedDate.toDate(), navnebeskyttelseslettedato,
                navnebeskyttelsestartdato, validFrom.toDate(), validTo.toDate());
        return person;
    }
}
