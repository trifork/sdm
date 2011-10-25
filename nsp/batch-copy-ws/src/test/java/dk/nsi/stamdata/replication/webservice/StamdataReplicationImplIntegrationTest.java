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

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.xpath.XPathFactory;
import org.dom4j.io.DOMReader;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ibm.wsdl.util.IOUtils;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;

import dk.nsi.stamdata.replication.jaxws.Header;
import dk.nsi.stamdata.replication.jaxws.ObjectFactory;
import dk.nsi.stamdata.replication.jaxws.ReplicationRequestType;
import dk.nsi.stamdata.replication.jaxws.ReplicationResponseType;
import dk.nsi.stamdata.replication.jaxws.Security;
import dk.nsi.stamdata.replication.jaxws.StamdataReplication;
import dk.nsi.stamdata.replication.jaxws.StamdataReplicationService;
import dk.nsi.stamdata.testing.TestServer;
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

    @Inject
    private Session session;
    private List<Person> persons = Lists.newArrayList();

    @Before
    public void setUp() throws Exception {
        server = new TestServer().port(8986).contextPath("/").start();

        URL wsdlLocation = new URL("http://localhost:8986/service/StamdataReplication?wsdl");
        QName serviceName = new QName("http://nsi.dk/2011/10/21/StamdataKrs/", "StamdataReplicationService");
        StamdataReplicationService service = new StamdataReplicationService(wsdlLocation, serviceName);

        service.setHandlerResolver(new SealNamespaceResolver());
        client = service.getStamdataReplication();
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
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("person");
        request.setVersion(1L);
        request.setOffset("0");

        isClientAuthority = true;

        populateDatabaseAndSendRequest();
    }

    @Ignore
    @Test(expected = SOAPFaultException.class)
    public void cvrThatIsNotWhitelistedRaisesAnException() throws Exception {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("person");
        request.setVersion(1L);
        request.setOffset("0");

        isClientAuthority = false;

        populateDatabaseAndSendRequest();
    }

    @Test
    public void basicQueryOnEmptyDatabaseReturnsZeroResults() throws Exception {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("person");
        request.setVersion(1L);
        request.setOffset("0");

        isClientAuthority = true;

        populateDatabaseAndSendRequest();

        Element anyAsElement = (Element) response.getAny();
        assertThat(anyAsElement.getLocalName(), is("feed"));
        assertThat(anyAsElement.getNamespaceURI(), is("http://www.w3.org/2005/Atom"));
    }

    @Test
    public void basicQueryOnNonEmptyDatabaseReturnsOneResult() throws Exception {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("person");
        request.setVersion(1L);
        request.setOffset("0");

        isClientAuthority = true;

        createDonaldDuckPerson();

        populateDatabaseAndSendRequest();

        Element anyAsElement = (Element) response.getAny();
        assertThat(anyAsElement.getLocalName(), is("feed"));
        assertThat(anyAsElement.getNamespaceURI(), is("http://www.w3.org/2005/Atom"));
        
        assertThat(anyAsElement.getFirstChild().getFirstChild().getTextContent(), is("tag:trifork.com,2011:cpr/person/v1"));

        /*
        DOMReader domReader = new DOMReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        document.adoptNode(anyAsElement);
        org.dom4j.Document dom4jDocument = domReader.read(document);
        
        dom4jDocument.selectSingleNode("//person[fornavn='Anders']/efternavn");
        */
        /*
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(anyAsElement), new StreamResult(outputStream));
        String transformResult = new String(outputStream.toByteArray());
        String result = expr.evaluate(anyAsElement);
        */
        
//        assertThat(result, is("And"));
    }

    private void populateDatabaseAndSendRequest() throws Exception {
        Transaction t = session.beginTransaction();
        session.createQuery("DELETE FROM Person").executeUpdate();
        for (Person person : persons) {
            session.persist(person);
        }
        t.commit();

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
    }

    // StamdataPersonLookupIntegrationTest contains a "Factories" class that we
    // would rather use
    private void createDonaldDuckPerson() {
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
        Date modifiedDate = new Date();
        Date navnebeskyttelseslettedato = null;
        Date navnebeskyttelsestartdato = null;
        Date validFrom = DateTime.now().minusYears(1).toDate();
        Date validTo = DateTime.now().plusDays(1).toDate();
        Person person = new Person(cpr, koen, fornavn, mellemnavn, efternavn, coNavn, lokalitet, vejnavn,
                bygningsnummer, husnummer, etage, sideDoerNummer, bynavn, postnummer, postdistrikt, status,
                gaeldendeCPR, foedselsdato, stilling, vejKode, kommuneKode, modifiedDate, navnebeskyttelseslettedato,
                navnebeskyttelsestartdato, validFrom, validTo);
        persons.add(person);
    }
}
