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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
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

import com.google.inject.Provider;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordMySQLTableGenerator;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;

import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.ObjectFactory;
import dk.nsi.stamdata.jaxws.generated.ReplicationFault;
import dk.nsi.stamdata.jaxws.generated.ReplicationRequestType;
import dk.nsi.stamdata.jaxws.generated.ReplicationResponseType;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.nsi.stamdata.jaxws.generated.StamdataReplication;
import dk.nsi.stamdata.jaxws.generated.StamdataReplicationService;
import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.testing.TestServer;

@RunWith(GuiceTestRunner.class)
public class StamdataReplicationImplIntegrationTest
{
    public static final String WHITELISTED_CVR = "25520041";
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
    @Inject
    private Provider<Connection> connectionProvider;

    private RecordSpecification recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;
    private List<Record> records = Lists.newArrayList();
    private DateTime now, lastYear, nextYear;
	private List<String> permissions;

	@Before
    public void setUp() throws Exception
    {
        server = new TestServer().port(8986).contextPath("/").start();

        URL wsdlLocation = new URL("http://localhost:8986/service/StamdataReplication?wsdl");
        //URL wsdlLocation = new URL("http://ext15-cniab01.nsp-test.netic.dk:8080/stamdata-batch-copy-ws/service/StamdataReplication?wsdl");
        QName serviceName = new QName("http://nsi.dk/2011/10/21/StamdataKrs/", "StamdataReplicationService");
        StamdataReplicationService service = new StamdataReplicationService(wsdlLocation, serviceName);

        service.setHandlerResolver(new SealNamespaceResolver());
        client = service.getStamdataReplication();

        now = DateTime.now();
        lastYear = now.minusYears(1);
        nextYear = now.plusYears(1);

        isClientAuthority = true;
	    
	    permissions = Arrays.asList(new String[] {
									"cpr/person/v1",
									"sikrede/sikrede/v1",
									"yderregister/yder/v1",
									"yderregister/person/v1",
									"bemyndigelsesservice/bemyndigelse/v1",
									"sor/sygehusafdeling/v1",
									"vitamin/grunddata/v1",
									"vitamin/firmadata/v1",
									"vitamin/udgaaedenavne/v1",
									"vitamin/indholdsstoffer/v1",
									"cpr/barnrelation/v1",
									"tilskudsblanket/forhoejettakst/v1"});
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
    public void testSikredeCopy() throws Exception
    {
        RecordBuilder builder = new RecordBuilder(SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        builder.field("CPRnr", "1234567890");
        Record record = builder.addDummyFieldsAndBuild();
        records.add(record);

        createReplicationRequest("sikrede", "sikrede");
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

        createReplicationRequest("yderregister", "yder");

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

       createReplicationRequest("yderregister", "person");

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

    @Test
    public void testCPRBarnRelationCopy() throws Exception
    {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("barnrelation");
        request.setVersion(1L);
        request.setOffset("00000000000000000000");
        request.setMaxRecords(500L);

        populateDatabaseAndSendRequest();

        //printDocument(anyAsElement.getOwnerDocument(), System.out);

        assertResponseContainsRecordAtom("cpr", "barnrelation");
    }

	/**
	 * This is a regression test covering the change introduced by NSPSUPPORT-57 to the documentation
	 */
	@Test()
	public void testPermissionsAreNotCommaSeparated() throws Exception {
		createReplicationRequest("vitamin", "grunddata");

		// the documentation stated that this was the way to get the 4 vitamin permissions, so let's try it
		permissions = Arrays.asList("vitamin/grunddata/v1,vitamin/firmadata/v1,vitamin/udgaaedenavne/v1,vitamin/indholdsstoffer/v1");

		try {
			populateDatabaseAndSendRequest();
			fail("Expected SOAP fault saying that the client does not have the necessary permissions");
		} catch (ReplicationFault fault) {
			assertTrue(fault.getMessage().contains("The provided cvr is not authorized to fetch this datatype"));
		}
	}

	// Helper methods
    
    // Pretty print XML document - good for debugging
    private static void printDocument(Document doc, OutputStream out) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
        } catch(Exception e) {
            // ignore - this is a test method
            e.printStackTrace();
        }
    }

    private void createReplicationRequest(String register, String datatype) {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister(register);
        request.setDatatype(datatype);
        request.setVersion(1L);
        request.setOffset("0");
    }

    private void createCprPersonRegisterReplicationRequest()
    {
        request = new ObjectFactory().createReplicationRequestType();
        request.setRegister("cpr");
        request.setDatatype("person");
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
        assertEquals(anyAsElement.getLocalName(), "feed");
        assertEquals(anyAsElement.getNamespaceURI(), "http://www.w3.org/2005/Atom");
        assertEquals(anyAsElement.getFirstChild().getFirstChild().getTextContent(), "tag:nsi.dk,2011:cpr/person/v1");
    }

    private void assertResponseContainsRecordAtom(String register, String datatype)
    {
    	assertEquals(anyAsElement.getLocalName(), "feed");
    	assertEquals(anyAsElement.getNamespaceURI(), "http://www.w3.org/2005/Atom");
    	assertEquals(anyAsElement.getFirstChild().getFirstChild().getTextContent(), "tag:nsi.dk,2011:" + register + "/" + datatype + "/v1");
    }

    private void assertResponseContainsPersonWithSurNameMatchingGivenName(String givenName, String surName) throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression("//sdm:person[sdm:fornavn='" + givenName + "']/sdm:efternavn");
        String result = expression.evaluate(anyAsElement);
        assertEquals(result, surName);
    }

    private void assertResponseContainsExactNumberOfRecords(String tag, int n) throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression("count(//" + tag + ")");
        String countAsString = expression.evaluate(anyAsElement);
        assertEquals(Integer.parseInt(countAsString), n);
    }

    private void assertResponseContainsPersonWithCpr(String oracleCpr) throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression("//sdm:person/sdm:cpr");
        String result = expression.evaluate(anyAsElement);
        
        //printDocument(anyAsElement.getOwnerDocument(), System.out);

        assertEquals(result, oracleCpr);
    }

    private void assertResponseContainsValueOnXPath(String xPath, String oracle) throws XPathExpressionException
    {
        XPathExpression expression = createXpathExpression(xPath);
        String result = expression.evaluate(anyAsElement);
        assertEquals(result, oracle);
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
                "sdm", "http://nsi.dk/-/stamdata/3.0/cpr",
                "sikrede", "http://nsi.dk/-/stamdata/3.0/sikrede",
                "yder", "http://nsi.dk/-/stamdata/3.0/yderregister",
                "grunddata", "http://nsi.dk/-/stamdata/3.0/vitamin",
                "forhoejettakst", "http://nsi.dk/-/stamdata/3.0/tilskudsblanket",
                "bemyndigelse", "http://nsi.dk/-/stamdata/3.0/bemyndigelsesservice");
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
	    for (String permission : permissions) {
		    cvrClient.addPermission(permission);
	    }
        session.persist(cvrClient);

        Connection connection = session.connection();
        connection.createStatement().executeUpdate("DROP TABLE IF EXISTS " + recordSpecification.getTable());
        connection.createStatement().executeUpdate(RecordMySQLTableGenerator.createSqlSchema(recordSpecification));
        RecordPersister recordPersister = new RecordPersister(connectionProvider, new Instant());

        for (Record r : records)
        {
            recordPersister.persist(r, recordSpecification);
        }

        t.commit();
    }

    private void sendRequest() throws Exception, ReplicationFault
    {
        Holder<Security> securityHeader;
        Holder<Header> medcomHeader;

        if (isClientAuthority)
        {
            SecurityWrapper secutityHeadersNotWhitelisted = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(WHITELISTED_CVR);
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

}
