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

import com.google.inject.Inject;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;
import dk.nsi.stamdata.jaxws.generated.*;
import dk.nsi.stamdata.replication.TestTableCreator;
import dk.nsi.stamdata.testing.TestServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.abdera.model.AtomDate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@RunWith(GuiceTestRunner.class)
public class DynamicReplicationIntegrationTest {


    private TestServer server;
    private StamdataReplication client;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    private TestTableCreator testTableCreator;

    private String lastOffset;

    private long testViewId;

    @Before
    public void setUp() throws Exception
    {
        server = new TestServer().port(8986).contextPath("/").start();

        URL wsdlLocation = new URL("http://localhost:8986/service/StamdataReplication?wsdl");
        QName serviceName = new QName("http://nsi.dk/2011/10/21/StamdataKrs/", "StamdataReplicationService");
        StamdataReplicationService service = new StamdataReplicationService(wsdlLocation, serviceName);

        service.setHandlerResolver(new SealNamespaceResolver());
        client = service.getStamdataReplication();

        testTableCreator.createTestTable();
        testViewId = testTableCreator.createAndWhiteListForTestView();
    }

    @After
    public void tearDown() throws Exception {
        testTableCreator.removeView(testViewId);
        server.stop();
    }

    @Test
    public void onlyCopiesNewOrModified() throws Exception {
        // First copy run normal copy
        canCopyAllSupportedDatatypes();
        // Mysql datetimes precision are in seconds so make sure we wait atleast 1 second so the modifieddate id larger
        Thread.sleep(1100);
        // Add a new entry and make sure the old records is not copied again
        testTableCreator.insertTestRow("test2", 43, 43000000, 43.43, new Date(), new Date(), 43.43f, true);
        // We request 100 but we should only get one since we only added one new record
        requestAndValidate1ResultWithTekst("test2");

        // Mysql datetimes precision are in seconds so make sure we wait atleast 1 second so the modifieddate id larger
        Thread.sleep(1100);
        // Modify the first record and
        testTableCreator.updateModifiedDateOnRecordsWithTekst("test");
        requestAndValidate1ResultWithTekst("test");
    }

    @Test
    public void canCopyAllSupportedDatatypes() throws Exception {
        testTableCreator.insertTestRow("test", 42, 42000000, 42.42, new Date(), new Date(), 42.42f, true);
        ReplicationRequestType request = createRequest("00000000000000000000", 1);
        ReplicationResponseType response = sendRequest(request);
        Element responseElem = (Element) response.getAny();

        String textContent = responseElem.getFirstChild().getFirstChild().getTextContent();
        assertEquals(textContent, "tag:nsi.dk,2011:"+TestTableCreator.TEST_REGISTER+"/"+TestTableCreator.TEST_DATATYPE+"/v1");

        updateLastOffset(responseElem);

        NodeList contents = extractFeedContent(responseElem);
        assertEquals(contents.getLength(), 1);

        Element elem = (Element) contents.item(0);
        NodeList entry1Content = elem.getElementsByTagName(TestTableCreator.TEST_DATATYPE + ":" + TestTableCreator.TEST_DATATYPE);
        elem = (Element) entry1Content.item(0);
        Node tekstNode = elem.getElementsByTagName("tekst").item(0);
        assertEquals("test", tekstNode.getTextContent());

        Node node = elem.getElementsByTagName("tal").item(0);
        assertEquals("42", node.getTextContent());

        node = elem.getElementsByTagName("stort_tal").item(0);
        assertEquals("42000000", node.getTextContent());

        node = elem.getElementsByTagName("decimal_tal").item(0);
        assertEquals("42.42", node.getTextContent());

        String dateText = elem.getElementsByTagName("dato").item(0).getTextContent();
        Date date = dateFormat.parse(dateText);
        assertNotNull(date);

        node = elem.getElementsByTagName("dato_tid").item(0);
        Date dateTime = AtomDate.parse(node.getTextContent());
        assertNotNull(dateTime);

        node = elem.getElementsByTagName("floatingpoint").item(0);
        assertEquals("42.42", node.getTextContent());

        node = elem.getElementsByTagName("flag").item(0);
        assertEquals("1", node.getTextContent());
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void requestAndValidate1ResultWithTekst(String expectedTekst) throws Exception {
        ReplicationRequestType request = createRequest(lastOffset, 100);
        ReplicationResponseType response = sendRequest(request);
        Element responseElem = (Element) response.getAny();
        updateLastOffset(responseElem);

        NodeList contents = extractFeedContent(responseElem);
        assertEquals(contents.getLength(), 1);

        Element elem = (Element) contents.item(0);
        NodeList entry1Content =
                elem.getElementsByTagName(TestTableCreator.TEST_DATATYPE + ":" + TestTableCreator.TEST_DATATYPE);
        elem = (Element) entry1Content.item(0);
        Node tekstNode = elem.getElementsByTagName("tekst").item(0);
        assertEquals(expectedTekst, tekstNode.getTextContent());
    }

    private void updateLastOffset(Element responseElem) {
        NodeList entries = responseElem.getElementsByTagName("atom:entry");
        Element elem = (Element) entries.item(0);
        entries = elem.getElementsByTagName("atom:id");
        elem = (Element) entries.item(0);
        lastOffset = elem.getTextContent();
        lastOffset = lastOffset.substring(lastOffset.length()-20);
    }

    private NodeList extractFeedContent(Element responseElem) {
        return responseElem.getElementsByTagName("atom:content");
    }

    private ReplicationResponseType sendRequest(ReplicationRequestType replicationRequestType) throws Exception
    {
        Holder<Security> securityHeader;
        Holder<Header> medcomHeader;

        SecurityWrapper secutityHeadersNotWhitelisted =
                DGWSHeaderUtil.getVocesTrustedSecurityWrapper(TestTableCreator.WHITELISTED_CVR);
        securityHeader = secutityHeadersNotWhitelisted.getSecurity();
        medcomHeader = secutityHeadersNotWhitelisted.getMedcomHeader();
        return client.replicate(securityHeader, medcomHeader, replicationRequestType);
    }

    private ReplicationRequestType createRequest(String offset, long maxRecords) {
        ReplicationRequestType request = new ObjectFactory().createReplicationRequestType();
        request.setRegister(TestTableCreator.TEST_REGISTER);
        request.setDatatype(TestTableCreator.TEST_DATATYPE);
        request.setVersion(1L);
        request.setOffset(offset);
        request.setMaxRecords(maxRecords);
        return request;
    }


}
