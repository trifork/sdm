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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;

import com.google.inject.Inject;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;

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
import dk.nsi.stamdata.views.Views;
import dk.nsi.stamdata.views.cpr.Person;
import dk.sosi.seal.model.AuthenticationLevel;

@RunWith(GuiceTestRunner.class)
public class StamdataReplicationIdCardLevelAttackTest {
    public static final String WHITELISTED_CVR = "12345678";
    public static final String NON_WHITELISTED_CVR = "87654321";

    private TestServer server;
    private StamdataReplication client;

    private ReplicationRequestType request;
    private ReplicationResponseType response;
    private Element anyAsElement;

    @Inject
    private Session session;
    @Inject
    private ClientDao clientDao;

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
    public void levelOneRequestRaisesAnException() throws Exception {
        attemptCallWithLevel(1);
    }
    
    @Test(expected = SOAPFaultException.class)
    public void levelTwoRequestRaisesAnException() throws Exception {
        attemptCallWithLevel(2);
    }
    
    @Test
    public void levelThreeRequestDoesNotRaisesAnException() throws Exception {
        attemptCallWithLevel(3);
    }
    
    @Test(expected = SOAPFaultException.class)
    public void levelFourRequestRaisesAnException() throws Exception {
        attemptCallWithLevel(4);
    }
    
    // Helper methods

    private void attemptCallWithLevel(int level) throws ReplicationFault, Exception
    {
        populateDatabase();
        
        createCprPersonRegisterReplicationRequest();
        sendRequest(level);
        
        assertResponseContainsAtom();
    }
    
    private void populateDatabase() {
        Transaction t = session.beginTransaction();

        session.createSQLQuery("SET foreign_key_checks = 0");
        session.createSQLQuery("DELETE FROM  Client_permissions").executeUpdate();
        session.createSQLQuery("DELETE FROM  Client").executeUpdate();
        session.createSQLQuery("SET foreign_key_checks = 1");

        // Example of subject serial number: CVR:19343634-UID:1234
        Client cvrClient = clientDao.create("Region Syd", String.format("CVR:%s-UID:1234", WHITELISTED_CVR));
        cvrClient.addPermission(Views.getViewPath(Person.class));
        session.persist(cvrClient);

        t.commit();
    }
    
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
        assertThat(anyAsElement.getFirstChild().getFirstChild().getTextContent(), is("tag:nsi.dk,2011:cpr/person/v1"));
    }

    private static Map<Integer, AuthenticationLevel> mapFromIntegerLevelToEnum;
    static {
        mapFromIntegerLevelToEnum = new HashMap<Integer, AuthenticationLevel>();
        
        mapFromIntegerLevelToEnum.put(AuthenticationLevel.NO_AUTHENTICATION.getLevel(), AuthenticationLevel.NO_AUTHENTICATION);
        mapFromIntegerLevelToEnum.put(AuthenticationLevel.USERNAME_PASSWORD_AUTHENTICATION.getLevel(), AuthenticationLevel.USERNAME_PASSWORD_AUTHENTICATION);
        mapFromIntegerLevelToEnum.put(AuthenticationLevel.VOCES_TRUSTED_SYSTEM.getLevel(), AuthenticationLevel.VOCES_TRUSTED_SYSTEM);
        mapFromIntegerLevelToEnum.put(AuthenticationLevel.MOCES_TRUSTED_USER.getLevel(), AuthenticationLevel.MOCES_TRUSTED_USER);
    }
    
    private void sendRequest(int levelOfIdCard) throws Exception, ReplicationFault {
        Holder<Security> securityHeader;
        Holder<Header> medcomHeader;

        SecurityWrapper secutityHeadersNotWhitelisted = DGWSHeaderUtil.getSecurityWrapper(mapFromIntegerLevelToEnum.get(levelOfIdCard), WHITELISTED_CVR, "foo", "bar");
        securityHeader = secutityHeadersNotWhitelisted.getSecurity();
        medcomHeader = secutityHeadersNotWhitelisted.getMedcomHeader();

        response = client.replicate(securityHeader, medcomHeader, request);
        anyAsElement = (Element) response.getAny();
    }
}
