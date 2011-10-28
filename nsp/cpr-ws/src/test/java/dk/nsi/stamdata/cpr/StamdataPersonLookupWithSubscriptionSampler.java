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
package dk.nsi.stamdata.cpr;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.trifork.stamdata.jaxws.SealNamespaceResolver;

import dk.nsi.stamdata.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.dgws.SecurityWrapper;
import dk.nsi.stamdata.jaxws.generated.CprAbbsRequestType;
import dk.nsi.stamdata.jaxws.generated.PersonLookupResponseType;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookupWithSubscription;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookupWithSubscriptionService;

/**
 * Performance Test used by JMeter.
 */
public class StamdataPersonLookupWithSubscriptionSampler extends AbstractJavaSamplerClient
{

    private static final String ENDPOINT_URL_PARAM = "EndpointURL";
    private static final String CLIENT_CVR_PARAM = "ClientCVR";


    @Override
    public Arguments getDefaultParameters()
    {
        Arguments args = new Arguments();

        args.addArgument(ENDPOINT_URL_PARAM, "http://localhost:8080/stamdata-cpr-ws/service/StamdataPersonLookupWithSubscription");
        args.addArgument(CLIENT_CVR_PARAM, "12345678");

        return args;
    }


    @Override
    public SampleResult runTest(JavaSamplerContext context)
    {
        String endpointURL = context.getParameter(ENDPOINT_URL_PARAM);
        String clientCVR = context.getParameter(CLIENT_CVR_PARAM);

        SampleResult result = new SampleResult();

        try
        {
            StamdataPersonLookupWithSubscription client = createClient(endpointURL);

            SecurityWrapper headers = createHeaders(clientCVR);

            CprAbbsRequestType query = new CprAbbsRequestType();

            // Wait until the last minute before starting the timer.
            result.sampleStart();
            PersonLookupResponseType response = client.getSubscribedPersonDetails(headers.getSecurity(), headers.getMedcomHeader(), query);
            result.sampleEnd();

            int numberOfReturnedPersons = response.getPersonInformationStructure().size();
            result.setResponseMessage("StamdataPersonLookupWithSubscription getSubscribedPersonDetails request for cvr " + clientCVR + " , " + numberOfReturnedPersons + " persons in response");

            // as the result is stateful ("changed since last call", we cannot
            // assert that the response contains any persons
            result.setResponseOK();
        }
        catch (Exception e)
        {
            result.sampleEnd();
            e.printStackTrace();
            result.setSuccessful(false);
        }

        return result;
    }

    // used by jmeter scripts
    @SuppressWarnings("unused")
    private String firstCprFromResponse(PersonLookupResponseType responseType)
    {
        return responseType.getPersonInformationStructure().get(0).getRegularCPRPerson().getSimpleCPRPerson().getPersonCivilRegistrationIdentifier();
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
