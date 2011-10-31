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
package com.trifork.stamdata.authorization;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.trifork.stamdata.authorization.models.Authorization;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;
import com.trifork.stamdata.persistence.Transactional;

import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.jaxws.generated.AuthorizationPortType;
import dk.nsi.stamdata.jaxws.generated.AuthorizationRequestType;
import dk.nsi.stamdata.jaxws.generated.AuthorizationResponseType;
import dk.nsi.stamdata.jaxws.generated.AuthorizationService;
import dk.nsi.stamdata.jaxws.generated.AuthorizationType;
import dk.nsi.stamdata.jaxws.generated.DGWSFault;
import dk.nsi.stamdata.jaxws.generated.ObjectFactory;
import dk.nsi.stamdata.testing.TestServer;


@RunWith(GuiceTestRunner.class)
public class TestAuthorizationServlet
{
    TestServer server;
    List<Authorization> authorizations = Lists.newArrayList();
    
    static final String WHITELISTED_CVR = "19343634";
    static final String NON_WHITELISTED_CVR = "12341234";
    
    String cvr = WHITELISTED_CVR;
    
    @Inject
    Session session;
    
    private AuthorizationRequestType request = new ObjectFactory().createAuthorizationRequestType();
    private AuthorizationResponseType response = null;
    private AuthorizationPortType client;

    @Before
    public void setUp() throws Exception
    {
         server = new TestServer().start();
    }


    @After
    public void tearDown() throws Exception
    {
        server.stop();
    }


    @Test
    public void shouldReturnTheExpectedAuthorizationWhenThereAreOthersThatDontMatchTheQuery() throws Exception
    {
        Authorization authorization1_1 = createAuthorization();
        authorization1_1.cpr = "1111122222";
        authorization1_1.educationCode = "2131";
        authorization1_1.firstName = "Peter";
        authorization1_1.lastName = "Andersen";
        authorization1_1.authorizationCode = "B1114";
        
        Authorization authorization1_2 = createAuthorization();
        authorization1_2.cpr = "1111122222";
        authorization1_2.firstName = "Peter";
        authorization1_2.lastName = "Andersen";
        authorization1_2.authorizationCode = "A2114";
        authorization1_2.educationCode = "5155";
        
        // Add another to make sure the right one is selected.
        
        Authorization authorization2 = createAuthorization();
        authorization2.cpr = "2222211111";
        
        request.setCpr(authorization1_1.cpr);
        
        sendRequest();

        assertThat(response.getFirstName(), is(authorization1_1.firstName));
        assertThat(response.getLastName(), is(authorization1_1.lastName));
        
        assertThat(response.getAuthorization().size(), is(2));
        
        AuthorizationType authorization = response.getAuthorization().get(1);
        
        assertThat(authorization.getEducationCode(), is(authorization1_1.educationCode));
        assertThat(authorization.getAuthorizationCode(), is(authorization1_1.authorizationCode));
        
        authorization = response.getAuthorization().get(0);

        assertThat(authorization.getEducationCode(), is(authorization1_2.educationCode));
        assertThat(authorization.getAuthorizationCode(), is(authorization1_2.authorizationCode));
        assertThat(authorization.getEducationName(), is("Fodterapeut"));
    }
    
    @Test(expected = DGWSFault.class)
    public void shouldReturnFaultIfNotWhitelisted() throws Exception
    {
        Authorization authorization = createAuthorization();
        request.setCpr(authorization.cpr);
        
        cvr = NON_WHITELISTED_CVR;
        
        sendRequest();
    }
    
    @Test
    public void shouldReturnEnEmptyResponseIfNoAuthorizationsWereFound() throws Exception
    {
        createAuthorization();
        
        request.setCpr("0000000000");
        
        sendRequest();
        
        assertThat(response.getFirstName(), is(nullValue()));
    }
    
    public Authorization createAuthorization()
    {
        Authorization auth = new Authorization("1234567890", "Ib", "Sørensen", "A1234", "2312");
        authorizations.add(auth);
        return auth;
    }
    
    @Transactional
    public void persistAuthorizations()
    {
        session.createQuery("DELETE FROM Authorization").executeUpdate();
        
        for (Authorization authorization : authorizations)
        {
            session.persist(authorization);
        }
    }
    
    public void sendRequest() throws Exception
    {
        persistAuthorizations();
        
        final QName SERVICE_QNAME = new QName("http://trifork.com/-/stamdata/3.0", "AuthorizationService");

        URL wsdlLocation = new URL("http://localhost:8080/service/AuthorizationService?wsdl");
        AuthorizationService serviceCatalog = new AuthorizationService(wsdlLocation, SERVICE_QNAME);

        // SEAL enforces that the XML prefixes are exactly
        // as it creates them. So we have to make sure we
        // don't change them.

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());
        
        client = serviceCatalog.getAuthorizationPort();
        
        SecurityWrapper headers = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(cvr, "foo2", "bar2");

        response = client.authorization(headers.getSecurity(), headers.getMedcomHeader(), request);
    }
}
