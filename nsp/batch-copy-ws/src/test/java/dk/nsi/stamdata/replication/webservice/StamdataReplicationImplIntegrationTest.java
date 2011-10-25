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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sun.xml.bind.v2.runtime.output.NamespaceContextImpl;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;

import dk.nsi.stamdata.replication.jaxws.Header;
import dk.nsi.stamdata.replication.jaxws.ObjectFactory;
import dk.nsi.stamdata.replication.jaxws.ReplicationFault;
import dk.nsi.stamdata.replication.jaxws.ReplicationRequestType;
import dk.nsi.stamdata.replication.jaxws.ReplicationResponseType;
import dk.nsi.stamdata.replication.jaxws.Security;
import dk.nsi.stamdata.replication.jaxws.StamdataReplication;
import dk.nsi.stamdata.replication.jaxws.StamdataReplicationService;
import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.testing.TestServer;
import dk.nsi.stamdata.views.Views;
import dk.nsi.stamdata.views.cpr.Person;

@RunWith(GuiceTestRunner.class)
public class StamdataReplicationImplIntegrationTest {
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
    private DateTime now, lastYear, nextYear;
    
    @Before
    public void setUp() throws Exception {
        server = new TestServer().port(8986).contextPath("/").start();

        URL wsdlLocation = new URL("http://localhost:8986/service/StamdataReplication?wsdl");
        QName serviceName = new QName("http://nsi.dk/2011/10/21/StamdataKrs/", "StamdataReplicationService");
        StamdataReplicationService service = new StamdataReplicationService(wsdlLocation, serviceName);

        service.setHandlerResolver(new SealNamespaceResolver());
        client = service.getStamdataReplication();
        
        now = DateTime.now();
        lastYear = now.minusYears(1);
        nextYear = now.plusYears(1);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    // Tests

    @Test(expected = SOAPFaultException.class)
    public void requestWithoutAnyReplicationTypeGivesSenderSoapFault() throws Exception {
        request = new ReplicationRequestType();

        populateDatabaseAndSendRequest();
    }

    @Test
    public void basicQueryDoesNotRaiseAnException() throws Exception {
        createCprPersonRegisterReplicationRequest();

        isClientAuthority = true;

        populateDatabaseAndSendRequest();
    }

    @Test(expected = ReplicationFault.class)
    public void cvrThatIsNotWhitelistedRaisesAnException() throws Exception {
        createCprPersonRegisterReplicationRequest();

        isClientAuthority = false;

        populateDatabaseAndSendRequest();
    }

    @Test
    public void basicQueryOnEmptyDatabaseReturnsZeroResults() throws Exception {
        createCprPersonRegisterReplicationRequest();

        isClientAuthority = true;

        populateDatabaseAndSendRequest();

        assertResponseContainsAtom();
        
        assertResponseContainsExactNumberOfRecords("person", 0);
    }

    @Test
    public void basicQueryOnNonEmptyDatabaseReturnsOneResult() throws Exception {
        createCprPersonRegisterReplicationRequest();

        isClientAuthority = true;

        Person person = createDonaldDuckPerson(now, lastYear, nextYear);
        persons.add(person);

        populateDatabaseAndSendRequest();

        assertResponseContainsAtom();

        assertResponseContainsExactNumberOfRecords("person", 1);
        
        String fornavn = "Anders";
        String efternavn = "And";
        assertResponseContainsPersonWithSurNameMatchingGivenName(fornavn, efternavn);
    }

    @Test
    public void personsArePagedAccordingToParameter() throws Exception {
        createCprPersonRegisterReplicationRequest();
        
        isClientAuthority = true;

        Person a = createDonaldDuckPerson(now, lastYear, nextYear);
        a.cpr = "1111111111";
        persons.add(a);
        
        Person b = createDonaldDuckPerson(now, lastYear, nextYear);
        b.cpr = "2222222222";
        persons.add(b);

        request.setOffset("0");
        request.setMaxRecords(2L);
        
        populateDatabaseAndSendRequest();
        assertResponseContainsAtom();
        assertResponseContainsExactNumberOfRecords("person", 2);
    }

    @Test
    public void personsAreCorrectlyPaged() throws Exception {
        createCprPersonRegisterReplicationRequest();
        
        isClientAuthority = true;

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
        assertResponseContainsAtom();
        assertResponseContainsExactNumberOfRecords("person", 1);
        assertResponseContainsPersonWithCpr("1111111111");
        
        request.setOffset(getOffsetFromAtomEntry());
        sendRequest();
        assertResponseContainsAtom();
        assertResponseContainsExactNumberOfRecords("person", 1);
        assertResponseContainsPersonWithCpr("2222222222");
        
        request.setOffset(getOffsetFromAtomEntry());
        sendRequest();
        assertResponseContainsAtom();
        assertResponseContainsExactNumberOfRecords("person", 1);
        assertResponseContainsPersonWithCpr("3333333333");
    }

    /*
    @Test
    public void personsAreCorrectlyPagedEvenWhenNewRecordIsInserted() throws Exception {
        createCprPersonRegisterReplicationRequest();
        
        isClientAuthority = true;

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
        assertResponseContainsAtom();
        assertResponseContainsExactNumberOfRecords("person", 1);
        assertResponseContainsPersonWithCpr("1111111111");
        
        request.setOffset(getOffsetFromAtomEntry());
        sendRequest();
        assertResponseContainsAtom();
        assertResponseContainsExactNumberOfRecords("person", 1);
        assertResponseContainsPersonWithCpr("2222222222");
        
        a.modifiedDate = now.toDate();
        insertSinglePersonIntoDatabase(a);
        
        request.setOffset(getOffsetFromAtomEntry());
        sendRequest();
        assertResponseContainsAtom();
        assertResponseContainsExactNumberOfRecords("person", 1);
        assertResponseContainsPersonWithCpr("3333333333");

        request.setOffset(getOffsetFromAtomEntry());
        sendRequest();
        assertResponseContainsAtom();
        assertResponseContainsExactNumberOfRecords("person", 1);
        assertResponseContainsPersonWithCpr("1111111111");
    }
    */

    // Helper methods
    
    private void createCprPersonRegisterReplicationRequest() {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("person");
        request.setVersion(1L);
        request.setOffset("0");
    }
    
    private void assertResponseContainsAtom() {
        assertThat(anyAsElement.getLocalName(), is("feed"));
        assertThat(anyAsElement.getNamespaceURI(), is("http://www.w3.org/2005/Atom"));
        assertThat(anyAsElement.getFirstChild().getFirstChild().getTextContent(), is("tag:trifork.com,2011:cpr/person/v1"));
    }

    private void assertResponseContainsPersonWithSurNameMatchingGivenName(String givenName, String surName) throws XPathExpressionException {
        XPathExpression expression = createXpathExpression("//sdm:person[sdm:fornavn='" + givenName + "']/sdm:efternavn");
        String result = expression.evaluate(anyAsElement);
        assertThat(result, is(surName));
    }

    private void assertResponseContainsExactNumberOfRecords(String tag, int n) throws XPathExpressionException {
        XPathExpression expression = createXpathExpression("count(//sdm:person)");
        String countAsString = expression.evaluate(anyAsElement);
        assertThat(Integer.parseInt(countAsString), is(n));
    }

    private void assertResponseContainsPersonWithCpr(String oracleCpr) throws XPathExpressionException {
        XPathExpression expression = createXpathExpression("//sdm:person/sdm:cpr");
        String result = expression.evaluate(anyAsElement);
        assertThat(result, is(oracleCpr));
    }

    private String getOffsetFromAtomEntry() throws XPathExpressionException {
        XPathExpression expression = createXpathExpression("//atom:entry/atom:id");
        String atomId = expression.evaluate(anyAsElement);
        String[] tokens = atomId.split("/");
        return tokens[tokens.length - 1];
    }

    private XPathExpression createXpathExpression(String expression) throws XPathExpressionException {
        NamespaceContext context = new NamespaceContextMap(
                "krs", "http://nsi.dk/2011/10/21/StamdataKrs/", 
                "atom", "http://www.w3.org/2005/Atom",
                "sdm", "http://trifork.com/-/stamdata/3.0/cpr");
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(context);
        return xpath.compile(expression);
    }
    
    private void populateDatabaseAndSendRequest() throws Exception {
        populateDatabase();
        sendRequest();
    }

    private void populateDatabase() {
        Transaction t = session.beginTransaction();

        session.createQuery("DELETE FROM Client").executeUpdate();
        session.createSQLQuery("DELETE FROM Client_permissions").executeUpdate();
        
        // Example of subject serial number: CVR:19343634-UID:1234
        Client cvrClient = clientDao.create("Region Syd", String.format("CVR:%s-UID:1234", WHITELISTED_CVR));
        cvrClient.addPermission(Views.getViewPath(Person.class));
        session.persist(cvrClient);

        session.createQuery("DELETE FROM Person").executeUpdate();
        for (Person person : persons) {
            session.persist(person);
        }
        t.commit();
    }

    private void sendRequest() throws Exception, ReplicationFault {
        Holder<Security> securityHeader;
        Holder<Header> medcomHeader;

        if (isClientAuthority) {
            SecurityWrapper secutityHeadersNotWhitelisted = DGWSHeaderUtil
                    .getVocesTrustedSecurityWrapper(WHITELISTED_CVR);
            securityHeader = secutityHeadersNotWhitelisted.getSecurity();
            medcomHeader = secutityHeadersNotWhitelisted.getMedcomHeader();
        } else {
            SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(NON_WHITELISTED_CVR);
            securityHeader = securityHeaders.getSecurity();
            medcomHeader = securityHeaders.getMedcomHeader();
        }

        response = client.replicate(securityHeader, medcomHeader, request);
        anyAsElement = (Element) response.getAny();
    }

    // StamdataPersonLookupIntegrationTest contains a "Factories" class that we
    // would rather use
    private Person createDonaldDuckPerson(DateTime modifiedDate, DateTime validFrom, DateTime validTo) {
        String cpr = "0102451234";
        String koen = "M";
        String fornavn = "Anders";
        String mellemnavn = "Von";
        String efternavn = "And";
        String coNavn = null;
        String lokalitet = "Pas";
        String vejnavn = "Ligustervænget";
        String bygningsnummer = "42";
        String husnummer = "123";
        String etage = "12";
        String sideDoerNummer = "th.";
        String bynavn = "Andeby";
        BigInteger postnummer = BigInteger.ZERO;
        String postdistrikt = "1234";
        String status = "1";
        String gaeldendeCPR = "0102451234";
        Date foedselsdato = new Date();
        String stilling = "Liggende";
        BigInteger vejKode = BigInteger.ONE;
        BigInteger kommuneKode = BigInteger.TEN;
        Date navnebeskyttelseslettedato = null;
        Date navnebeskyttelsestartdato = null;
        Person person = new Person(cpr, koen, fornavn, mellemnavn, efternavn, coNavn, lokalitet, vejnavn,
                bygningsnummer, husnummer, etage, sideDoerNummer, bynavn, postnummer, postdistrikt, status,
                gaeldendeCPR, foedselsdato, stilling, vejKode, kommuneKode, modifiedDate.toDate(), navnebeskyttelseslettedato,
                navnebeskyttelsestartdato, validFrom.toDate(), validTo.toDate());
        return person;
    }
}
