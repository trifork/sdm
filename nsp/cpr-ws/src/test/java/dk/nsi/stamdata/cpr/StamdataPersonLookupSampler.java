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
import dk.nsi.stamdata.jaxws.generated.NamePersonQueryType;
import dk.nsi.stamdata.jaxws.generated.PersonLookupRequestType;
import dk.nsi.stamdata.jaxws.generated.PersonLookupResponseType;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookup;
import dk.nsi.stamdata.jaxws.generated.StamdataPersonLookupService;


/**
 * Performance Test used by JMeter.
 */
public class StamdataPersonLookupSampler extends AbstractJavaSamplerClient
{
    private static final String REQUESTED_GIVENNAME_PARAM = "RequestedGivenName";
    private static final String REQUESTED_SURNAME_PARAM = "RequestedSurname";
    private static final String ENDPOINT_URL_PARAM = "EndpointURL";
    private static final String CLIENT_CVR_PARAM = "ClientCVR";


    @Override
    public Arguments getDefaultParameters()
    {
        Arguments args = new Arguments();

        args.addArgument(ENDPOINT_URL_PARAM, "http://localhost:80/stamdata-cpr-ws/service/StamdataPersonLookup");
        args.addArgument(CLIENT_CVR_PARAM, "12345678");
        args.addArgument(REQUESTED_GIVENNAME_PARAM, "Thomas");
        args.addArgument(REQUESTED_SURNAME_PARAM, "Kristensen");

        return args;
    }


    @Override
    public SampleResult runTest(JavaSamplerContext context)
    {
        String endpointURL = context.getParameter(ENDPOINT_URL_PARAM);
        String clientCVR = context.getParameter(CLIENT_CVR_PARAM);
        String requestedGivenname = context.getParameter(REQUESTED_GIVENNAME_PARAM);
        String requestedSurname = context.getParameter(REQUESTED_SURNAME_PARAM);

        SampleResult result = new SampleResult();

        try
        {
            StamdataPersonLookup client = createClient(endpointURL);

            SecurityWrapper headers = createHeaders(clientCVR);
            PersonLookupRequestType query = new PersonLookupRequestType();

            NamePersonQueryType namePersonQuery = new NamePersonQueryType();
            namePersonQuery.setPersonGivenName(requestedGivenname);
            namePersonQuery.setPersonSurnameName(requestedSurname);
            query.setNamePersonQuery(namePersonQuery);

            // Wait until the last minute before starting the
            // timer.
            result.sampleStart();
            PersonLookupResponseType responseType = client.getPersonDetails(headers.getSecurity(), headers.getMedcomHeader(), query);
            result.sampleEnd();

            int numberOfReturnedPersons = responseType.getPersonInformationStructure().size();
            if (numberOfReturnedPersons == 0)
            {
                result.setSuccessful(false);
                result.setResponseMessage("Expected at least 1 person in result, but found none");
            }
            else
            {
                result.setResponseMessage("StamdataPersonLookup getPersonDetails request for " + requestedGivenname + " " + requestedSurname + ", " + numberOfReturnedPersons + " persons in response");
                result.setResponseOK();
            }
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


    private StamdataPersonLookup createClient(String endpointURL) throws MalformedURLException
    {
        QName PVIT_SERVICE_QNAME = new QName("http://nsi.dk/2011/09/23/StamdataCpr/", "StamdataPersonLookupService");

        URL wsdlLocation = new URL(endpointURL + "?wsdl");
        StamdataPersonLookupService serviceCatalog = new StamdataPersonLookupService(wsdlLocation, PVIT_SERVICE_QNAME);

        // SEAL enforces that the XML prefixes are exactly
        // as it creates them. So we have to make sure we
        // don't change them.

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());
        StamdataPersonLookup client = serviceCatalog.getStamdataPersonLookup();

        return client;
    }


    private SecurityWrapper createHeaders(String clientCVR) throws Exception
    {
        return DGWSHeaderUtil.getVocesTrustedSecurityWrapper(clientCVR, "foo", "bar");
    }
}
