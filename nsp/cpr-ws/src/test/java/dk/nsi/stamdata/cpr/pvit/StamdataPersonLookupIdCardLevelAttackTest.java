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
package dk.nsi.stamdata.cpr.pvit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.trifork.stamdata.jaxws.SealNamespaceResolver;

import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.guice.GuiceTestRunner;
import dk.nsi.stamdata.jaxws.generated.DGWSFault;
import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.PersonLookupRequestType;
import dk.nsi.stamdata.jaxws.generated.PersonLookupResponseType;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookup;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookupService;
import dk.sosi.seal.model.AuthenticationLevel;

@RunWith(GuiceTestRunner.class)
public class StamdataPersonLookupIdCardLevelAttackTest extends AbstractWebAppEnvironmentJUnit4Test {
    public static final QName PVIT_SERVICE_QNAME = new QName("http://nsi.dk/2011/09/23/StamdataCpr/",
            "StamdataPersonLookupService");

    private List<Person> persons = Lists.newArrayList();
    private PersonLookupRequestType request = new PersonLookupRequestType();
    private PersonLookupResponseType response;

    public static final String WHITELISTED_CVR = "12345678";

    private static StamdataPersonLookup client;
    private static StamdataPersonLookupService serviceCatalog;

    @Before
    public void setUp() throws Exception {
        URL wsdlLocation = new URL("http://localhost:8100/service/StamdataPersonLookup?wsdl");
        serviceCatalog = new StamdataPersonLookupService(wsdlLocation, PVIT_SERVICE_QNAME);

        // SEAL enforces that the XML prefixes are exactly
        // as it creates them. So we have to make sure we
        // don't change them.

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());

        client = serviceCatalog.getStamdataPersonLookup();
    }

    @Test(expected = SOAPFaultException.class)
    public void requestWithLevelOneIdCardRaisesAnException() throws Exception {
        sendRequestWithGivenSecurityLevel(1);
    }

    @Test(expected = SOAPFaultException.class)
    public void requestWithLevelTwoIdCardRaisesAnException() throws Exception {
        sendRequestWithGivenSecurityLevel(2);
    }

    @Test
    public void requestWithLevelthreeIdCardDoesNotRaisesAnException() throws Exception {
        sendRequestWithGivenSecurityLevel(3);
    }

    @Test(expected = SOAPFaultException.class)
    public void requestWithLevelFourIdCardRaisesAnException() throws Exception {
        sendRequestWithGivenSecurityLevel(4);
    }

    private void sendRequestWithGivenSecurityLevel(int levelOfIdCard) throws DGWSFault, Exception {
        persons.add(Factories.createPersonWithCPR("2905853347"));

        request.setCivilRegistrationNumberPersonQuery("0103952595");

        sendRequest(levelOfIdCard);

        assertThat(response.getPersonInformationStructure().size(), is(0));
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

    private void sendRequest(int levelOfIdCard) throws Exception, DGWSFault {
        Holder<Security> securityHeader;
        Holder<Header> medcomHeader;

        SecurityWrapper secutityHeadersNotWhitelisted = DGWSHeaderUtil.getSecurityWrapper(
                mapFromIntegerLevelToEnum.get(levelOfIdCard), WHITELISTED_CVR, "foo", "bar");
        securityHeader = secutityHeadersNotWhitelisted.getSecurity();
        medcomHeader = secutityHeadersNotWhitelisted.getMedcomHeader();

        response = client.getPersonDetails(securityHeader, medcomHeader, request);
    }
}
