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

import com.trifork.stamdata.jaxws.SealNamespaceResolver;
import dk.nsi._2011._09._23.stamdatacpr.DGWSFault;
import dk.nsi._2011._09._23.stamdatacpr.PersonLookupResponseType;
import dk.nsi._2011._09._23.stamdatacpr.StamdataPersonLookupWithSubscription;
import dk.nsi._2011._09._23.stamdatacpr.StamdataPersonLookupWithSubscriptionService;
import dk.nsi.cprabbs._2011._10.CprAbbsRequestType;
import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.guice.GuiceTestRunner;
import dk.oio.rep.medcom_sundcom_dk.xml.schemas._2007._02._01.PersonInformationStructureType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Ignore
@RunWith(GuiceTestRunner.class)
public class StamdataPersonLookupWithSubscriptionNSPSupport {

    public static final String[] CLIENT_CVRS = {"12345678"/*, "22334455", "33445566", "44556677"*/};
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    final String endpoint = "http://tri-test-niab82:8080/stamdata-cpr-ws/service/StamdataPersonLookupWithSubscription";

    private StamdataPersonLookupWithSubscription client;

    @Before
    public void setUp() throws Exception {
        client = createClient(endpoint);
    }


    @Test
    public void doQuery() throws Exception {
        CprAbbsRequestType query = new CprAbbsRequestType();
        DateTime dt = new DateTime(2010, 1, 1, 8, 0);//DateTime.now();
        //query.setSince(dt.toGregorianCalendar());
        for (String clientCvr : CLIENT_CVRS) {
            SecurityWrapper headers = createHeaders(clientCvr);
            try {
                PersonLookupResponseType response = client.getSubscribedPersonDetails(headers.getSecurity(), headers.getMedcomHeader(), query);
                List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();
                assertNotNull("PersonInformationStructure should never be null, it should at least be an empty list", personInformationStructure);
                System.out.println("getSubscribedPersonDetails - using CVR: " + clientCvr + ", since: " + dt.toString() + " ---> Fandt " + personInformationStructure.size() + " opdaterede personer");
                for (PersonInformationStructureType personInformationStructureType : personInformationStructure) {
                    System.out.println("\t- Person[ CPR: " + personInformationStructureType.getCurrentPersonCivilRegistrationIdentifier() + ", " +
                            "Navn: " + personInformationStructureType.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName() + " " +
                            personInformationStructureType.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonMiddleName() + " " +
                            personInformationStructureType.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonSurnameName()
                            + " ]"
                    );
                }
            } catch (DGWSFault e) {
                e.printStackTrace(System.err);
                fail("Failed getting subscribedPersonDetails - using CVR: " + clientCvr + " Exception: " + e.getMessage());

            }

        }


    }

    private StamdataPersonLookupWithSubscription createClient(String endpointURL) throws MalformedURLException {
        StamdataPersonLookupWithSubscriptionService serviceCatalog = new StamdataPersonLookupWithSubscriptionService(new URL(endpointURL + "?wsdl"), new QName("http://nsi.dk/2011/09/23/StamdataCpr/", "StamdataPersonLookupWithSubscriptionService"));

        // SEAL enforces that the XML prefixes are exactly
        // as it creates them. So we have to make sure we
        // don't change them.

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());

        StamdataPersonLookupWithSubscription client = serviceCatalog.getStamdataPersonLookupWithSubscription();

        return client;
    }

    private SecurityWrapper createHeaders(String clientCVR) throws Exception {
        return DGWSHeaderUtil.getVocesTrustedSecurityWrapper(clientCVR, "foo", "bar");
    }

    public static String convertStreamToStr(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,
                        "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

}
