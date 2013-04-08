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
import com.google.inject.Provider;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;
import dk.nsi.stamdata.jaxws.generated.*;
import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.testing.TestServer;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
import java.sql.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.abdera.model.AtomDate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@RunWith(GuiceTestRunner.class)
public class DynamicReplicationIntegrationTest {
    private final static String TEST_TABLE = "testtable_skrs";
    private final static String TEST_REGISTER = "testreg";
    private final static String TEST_DATATYPE = "testtype";
    private static final String WHITELISTED_CVR = "25520041";

    private TestServer server;
    private StamdataReplication client;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    private Session session;
    @Inject
    private ClientDao clientDao;
    @Inject
    private Provider<Connection> connectionProvider;

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

        createTestTable();
        createAndWhiteListForTestView();
    }

    @After
    public void tearDown() throws Exception {
        removeView(testViewId);
        server.stop();
    }

    @Test
    public void onlyCopiesNewOrModified() throws Exception {
        // First copy run normal copy
        canCopyAllSupportedDatatypes();
        // Mysql datetimes precision are in seconds so make sure we wait atleast 1 second so the modifieddate id larger
        Thread.sleep(1100);
        // Add a new entry and make sure the old records is not copied again
        insertTestRow("test2", 43, 43000000, 43.43, new Date(), new Date(), 43.43f, true);
        // We request 100 but we should only get one since we only added one new record
        requestAndValidate1ResultWithTekst("test2");

        // Mysql datetimes precision are in seconds so make sure we wait atleast 1 second so the modifieddate id larger
        Thread.sleep(1100);
        // Modify the first record and
        updateModifiedDateOnRecordsWithTekst("test");
        requestAndValidate1ResultWithTekst("test");
    }

    @Test
    public void canCopyAllSupportedDatatypes() throws Exception {
        insertTestRow("test", 42, 42000000, 42.42, new Date(), new Date(), 42.42f, true);
        ReplicationRequestType request = createRequest("00000000000000000000", 1);
        ReplicationResponseType response = sendRequest(request);
        Element responseElem = (Element) response.getAny();

        String textContent = responseElem.getFirstChild().getFirstChild().getTextContent();
        assertEquals(textContent, "tag:nsi.dk,2011:"+TEST_REGISTER+"/"+TEST_DATATYPE+"/v1");

        updateLastOffset(responseElem);

        NodeList contents = extractFeedContent(responseElem);
        assertEquals(contents.getLength(), 1);

        Element elem = (Element) contents.item(0);
        NodeList entry1Content = elem.getElementsByTagName(TEST_DATATYPE + ":" + TEST_DATATYPE);
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
        NodeList entry1Content = elem.getElementsByTagName(TEST_DATATYPE + ":" + TEST_DATATYPE);
        elem = (Element) entry1Content.item(0);
        Node tekstNode = elem.getElementsByTagName("tekst").item(0);
        assertEquals(expectedTekst, tekstNode.getTextContent());
    }

    private void updateModifiedDateOnRecordsWithTekst(String tekst) throws SQLException {
        String sql = "UPDATE testtable_skrs SET ModifiedDate=? WHERE tekst=?";
        Timestamp now = new Timestamp((new Date()).getTime());
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, sql, now, tekst);
        } finally {
            DbUtils.close(conn);
        }
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

        SecurityWrapper secutityHeadersNotWhitelisted = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(WHITELISTED_CVR);
        securityHeader = secutityHeadersNotWhitelisted.getSecurity();
        medcomHeader = secutityHeadersNotWhitelisted.getMedcomHeader();


        return client.replicate(securityHeader, medcomHeader, replicationRequestType);
    }

    private void insertTestRow(String tekst, int tal, int stortTal, double decimal, Date dato, Date datoTid,
                               float flydende, boolean falg) throws SQLException {
        String sql = "INSERT INTO testtable_skrs(tekst, tal, stort_tal, decimal_tal, dato, dato_tid, floatingpoint, flag," +
                " ModifiedDate, ValidFrom, ValidTo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Timestamp now = new Timestamp((new Date()).getTime());
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, sql, tekst, tal, stortTal, decimal, dato, new Timestamp((new Date()).getTime()), flydende,
            falg, now, now, null);
        } finally {
            DbUtils.close(conn);
        }
    }

    private ReplicationRequestType createRequest(String offset, long maxRecords) {
        ReplicationRequestType request = new ObjectFactory().createReplicationRequestType();
        request.setRegister(TEST_REGISTER);
        request.setDatatype(TEST_DATATYPE);
        request.setVersion(1L);
        request.setOffset(offset);
        request.setMaxRecords(maxRecords);
        return request;
    }

    private void createAndWhiteListForTestView() throws SQLException {
        testViewId = insertNewView(TEST_REGISTER, TEST_DATATYPE, 1L, TEST_TABLE);
        addColumnToView(testViewId, true,  "PID",          0, -5, null);
        addColumnToView(testViewId, false, "tekst",        1, 12, 200);
        addColumnToView(testViewId, false, "tal",          2,  4, null);
        addColumnToView(testViewId, false, "stort_tal",    3, -5, null);
        addColumnToView(testViewId, false, "decimal_tal",  4,  3, null);
        addColumnToView(testViewId, false, "dato",         5, 91, null);
        addColumnToView(testViewId, false, "dato_tid",     6, 93, null);
        addColumnToView(testViewId, false, "floatingpoint",7,  6, null);
        addColumnToView(testViewId, false, "flag",         8, 16, null);
        addColumnToView(testViewId, false, "ModifiedDate", 9, 93, null);
        addColumnToView(testViewId, false, "ValidFrom",   10, 93, null);
        addColumnToView(testViewId, false, "ValidTo",     11, 93, null);
        whiteList(TEST_REGISTER + "/" + TEST_DATATYPE + "/v1");
    }

    private void removeView(long viewId) throws SQLException {
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, "DELETE FROM SKRSViewMapping WHERE idSKRSViewMapping=?", viewId);
        } finally {
            DbUtils.close(conn);
        }
    }

    private void whiteList(String permission) {
        Transaction t = session.beginTransaction();
        Client client = clientDao.findByCvr(WHITELISTED_CVR);
        if (client == null) {
            client = clientDao.create("Region Syd", String.format("CVR:%s-UID:1234", WHITELISTED_CVR));
        }
        client.addPermission(permission);
        session.persist(client);
        t.commit();
    }

    private long insertNewView(String register, String datatype, Long version, String tableName) throws SQLException {
        String sql1 = "INSERT INTO SKRSViewMapping(register, datatype, version, tableName, createdDate) ";
        sql1 += " VALUES(?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            preparedStatement = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);

            run.fillStatement(preparedStatement, register, datatype, version, tableName, new Timestamp((new Date()).getTime()));
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        } finally {
            DbUtils.close(preparedStatement);
            DbUtils.close(conn);
        }
        return -1;
    }

    private void createTestTable() throws SQLException {
        String sql = "CREATE  TABLE IF NOT EXISTS `testtable_skrs` (\n" +
                "  `PID` BIGINT(15) NOT NULL AUTO_INCREMENT ,\n" +
                "  `tekst` VARCHAR(200) NULL ,\n" +
                "  `tal` INT NULL ,\n" +
                "  `stort_tal` BIGINT NULL ,\n" +
                "  `decimal_tal` DECIMAL(8,2) NULL ,\n" +
                "  `dato` DATE NULL ,\n" +
                "  `dato_tid` DATETIME NULL ,\n" +
                "  `floatingpoint` FLOAT NULL ,\n" +
                "  `flag` TINYINT(1) NULL ,\n" +
                "  `ModifiedDate` DATETIME NULL ,\n" +
                "  `ValidFrom` DATETIME NULL ,\n" +
                "  `ValidTo` DATETIME NULL ,\n" +
                "  PRIMARY KEY (`PID`) )\n" +
                "ENGINE = InnoDB";
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, sql);
            run.update(conn, "DELETE FROM testtable_skrs");
        } finally {
            DbUtils.close(conn);
        }
    }

    private void addColumnToView(long viewId, boolean isPid, String columnName, int position, int dataType, Integer maxLength) throws SQLException {
        String sql = "INSERT INTO SKRSColumns(viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength)";
        sql += " VALUES (?,?,?,?,?,?,?)";
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, sql, viewId, isPid, columnName, columnName, position, dataType, maxLength);
        } finally {
            DbUtils.close(conn);
        }
    }

}
