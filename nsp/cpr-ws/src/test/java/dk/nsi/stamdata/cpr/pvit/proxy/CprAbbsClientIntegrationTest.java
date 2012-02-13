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
package dk.nsi.stamdata.cpr.pvit.proxy;

import static junit.framework.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.*;

import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;


public class CprAbbsClientIntegrationTest {

    private static CprAbbsStubJettyServer server;
    private static SecurityWrapper securityHeaders;

    private static CprSubscriptionClient client;


    @BeforeClass
    public static void setupSecurityHeaders() throws Exception {

        securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper("22345678", "foo", "bar");

        server = new CprAbbsStubJettyServer();
        server.startServer();

        client = new CprSubscriptionClient("localhost", Integer.toString(server.getPort()), "/cprabbs/service/cprabbs");
    }


    @AfterClass
    public static void stopServer() throws Exception {

        server.stopServer();
    }


    @Test
    public void canCallService() throws MalformedURLException, CprAbbsException {

        List<String> changedCprs = client.getChangedCprs(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), null);

        assertEquals(1, changedCprs.size());
        assertEquals("0000000000", changedCprs.get(0));
    }


    @Test
    public void callsWithSinceWhichDoesntTriggerSpecialBehaviourInStub() throws MalformedURLException, CprAbbsException {

        List<String> changedCprs = client.getChangedCprs(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), CprAbbsFacadeStubImplementation.SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES.plusDays(1));

        assertEquals(1, changedCprs.size());
        assertEquals("2222222222", changedCprs.get(0));
    }


    @Test
    public void callsWithSinceWhichTriggersSpecialBehaviourInStub() throws MalformedURLException, CprAbbsException {

        List<String> changedCprs = client.getChangedCprs(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), CprAbbsFacadeStubImplementation.SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES);

        assertEquals(1, changedCprs.size());
        assertEquals("1111111111", changedCprs.get(0));
    }


    @Test
    public void forwardsIdcardCvrInSecurityHeaders() throws Exception {

        // cvr starting with 1 triggers special behavior in mock service.

        securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper("12345678", "foo", "bar");
        List<String> changedCprs = client.getChangedCprs(securityHeaders.getSecurity(), securityHeaders.getMedcomHeader(), null);

        assertEquals(1, changedCprs.size());
        assertEquals("1234567800", changedCprs.get(0));
    }
}
