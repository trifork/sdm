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
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import dk.sosi.seal.model.AuthenticationLevel;


@RunWith(GuiceTestRunner.class)
public class AuthorizationServletIdCardLevelAttackTest
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

    @Test(expected = DGWSFault.class)
    public void requestWithLevelOneIdCardRaisesAnException() throws Exception {
        sendRequestWithGivenSecurityLevel(1);
    }

    @Test(expected = DGWSFault.class)
    public void requestWithLevelTwoIdCardRaisesAnException() throws Exception {
        sendRequestWithGivenSecurityLevel(2);
    }

    @Test
    public void requestWithLevelthreeIdCardDoesNotRaisesAnException() throws Exception {
        sendRequestWithGivenSecurityLevel(3);
    }

    @Test(expected = DGWSFault.class)
    public void requestWithLevelFourIdCardRaisesAnException() throws Exception {
        sendRequestWithGivenSecurityLevel(4);
    }    
    private void sendRequestWithGivenSecurityLevel(int securityLevel) throws Exception
    {
        Authorization authorization1 = createAuthorization();
        authorization1.cpr = "1111122222";
        authorization1.educationCode = "2131";
        authorization1.firstName = "Peter";
        authorization1.lastName = "Andersen";
        authorization1.authorizationCode = "B1114";
        
        // Add another to make sure the right one is selected.
        
        Authorization authorization2 = createAuthorization();
        authorization2.cpr = "2222211111";
        
        request.setCpr(authorization1.cpr);
        
        sendRequest(securityLevel);

        assertThat(response.getFirstName(), is(authorization1.firstName));
        assertThat(response.getLastName(), is(authorization1.lastName));
        
        assertThat(response.getAuthorization().size(), is(1));
        
        AuthorizationType authorization = response.getAuthorization().get(0);
        
        assertThat(authorization.getEducationCode(), is(authorization1.educationCode));
        assertThat(authorization.getAuthorizationCode(), is(authorization1.authorizationCode));
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
    
    private static Map<Integer, AuthenticationLevel> mapFromIntegerLevelToEnum;
    static {
        mapFromIntegerLevelToEnum = new HashMap<Integer, AuthenticationLevel>();

        mapFromIntegerLevelToEnum.put(AuthenticationLevel.NO_AUTHENTICATION.getLevel(),
                AuthenticationLevel.NO_AUTHENTICATION);
        mapFromIntegerLevelToEnum.put(AuthenticationLevel.USERNAME_PASSWORD_AUTHENTICATION.getLevel(),
                AuthenticationLevel.USERNAME_PASSWORD_AUTHENTICATION);
        mapFromIntegerLevelToEnum.put(AuthenticationLevel.VOCES_TRUSTED_SYSTEM.getLevel(),
                AuthenticationLevel.VOCES_TRUSTED_SYSTEM);
        mapFromIntegerLevelToEnum.put(AuthenticationLevel.MOCES_TRUSTED_USER.getLevel(),
                AuthenticationLevel.MOCES_TRUSTED_USER);
    }
    
    public void sendRequest(int securityLevel) throws Exception
    {
        persistAuthorizations();
        
        final QName SERVICE_QNAME = new QName("http://nsi.dk/-/stamdata/3.0", "AuthorizationService");

        URL wsdlLocation = new URL("http://localhost:8972/service/AuthorizationService?wsdl");
        AuthorizationService serviceCatalog = new AuthorizationService(wsdlLocation, SERVICE_QNAME);

        // SEAL enforces that the XML prefixes are exactly
        // as it creates them. So we have to make sure we
        // don't change them.

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());
        
        client = serviceCatalog.getAuthorizationPort();
        
        SecurityWrapper headers = DGWSHeaderUtil.getSecurityWrapper(
                mapFromIntegerLevelToEnum.get(securityLevel), WHITELISTED_CVR, "foo", "bar");

        response = client.authorization(headers.getSecurity(), headers.getMedcomHeader(), request);
    }
}
