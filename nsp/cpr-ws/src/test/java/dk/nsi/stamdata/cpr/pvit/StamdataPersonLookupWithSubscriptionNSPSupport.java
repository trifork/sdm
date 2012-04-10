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

import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.guice.GuiceTestRunner;
import dk.nsi.stamdata.jaxws.generated.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

import com.trifork.stamdata.jaxws.SealNamespaceResolver;

import static org.junit.Assert.*;

@Ignore
@RunWith(GuiceTestRunner.class)
public class StamdataPersonLookupWithSubscriptionNSPSupport {

    final String endpoint = "http://tri-test-niab82:8080/stamdata-cpr-ws/service/StamdataPersonLookupWithSubscription";
    public static final String CLIENT_CVR = "12345678";

    private StamdataPersonLookupWithSubscription client;
    private SecurityWrapper headers;

    @Before
    public void setUp() throws Exception
    {
        client = createClient(endpoint);
        headers = createHeaders(CLIENT_CVR);
    }


    @Test
    public void doQuery() throws Exception {
        CprAbbsRequestType query = new CprAbbsRequestType();
        PersonLookupResponseType response = client.getSubscribedPersonDetails(headers.getSecurity(), headers.getMedcomHeader(), query);
        //0102194925
         assertTrue(response.getPersonInformationStructure().size() != 0);

    }

    private StamdataPersonLookupWithSubscription createClient(String endpointURL) throws MalformedURLException
    {
        StamdataPersonLookupWithSubscriptionService serviceCatalog = new StamdataPersonLookupWithSubscriptionService(new URL(endpointURL + "?wsdl"), new QName("http://nsi.dk/2011/09/23/StamdataCpr/", "StamdataPersonLookupWithSubscriptionService"));

        // SEAL enforces that the XML prefixes are exactly
        // as it creates them. So we have to make sure we
        // don't change them.

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());

        StamdataPersonLookupWithSubscription client = serviceCatalog.getStamdataPersonLookupWithSubscription();

        return client;
    }

    private SecurityWrapper createHeaders(String clientCVR) throws Exception
    {
        return DGWSHeaderUtil.getVocesTrustedSecurityWrapper(clientCVR, "foo", "bar");
    }
}
