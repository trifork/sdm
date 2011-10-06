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

import dk.nsi.stamdata.cpr.integrationtest.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SecurityWrapper;
import dk.nsi.stamdata.cpr.jaxws.SealNamespaceResolver;
import dk.nsi.stamdata.cpr.ws.PersonLookupRequestType;
import dk.nsi.stamdata.cpr.ws.PersonLookupResponseType;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookup;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookupService;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;


@SuppressWarnings({"UnusedDeclaration"}) // used by jmeter scripts
public class StamdataPersonLookupSampler extends AbstractJavaSamplerClient {

    private static final String REQUESTED_CPR_PARAM = "RequestedCPR";
    private static final String ENDPOINT_URL_PARAM = "EndpointURL";
    private static final String CLIENT_CVR_PARAM = "ClientCVR";


	@Override
    public Arguments getDefaultParameters()
    {
        Arguments args = new Arguments();

        args.addArgument(ENDPOINT_URL_PARAM, "http://localhost:80/stamdata-cpr-ws/service/StamdataPersonLookup");
        args.addArgument(CLIENT_CVR_PARAM, "12345678");
        args.addArgument(REQUESTED_CPR_PARAM, "0101821234");
        
        return args;
    }


    @Override
    public SampleResult runTest(JavaSamplerContext context)
    {
        SampleResult result = new SampleResult();

        String endpointURL = context.getParameter(ENDPOINT_URL_PARAM);
        String clientCVR = context.getParameter(CLIENT_CVR_PARAM);
        String requestedCPR = context.getParameter(REQUESTED_CPR_PARAM);

        try
        {
            StamdataPersonLookup client = createClient(endpointURL);
            
            SecurityWrapper headers = createHeaders(clientCVR);
            PersonLookupRequestType query = new PersonLookupRequestType();

            query.setCivilRegistrationNumberPersonQuery(requestedCPR);
            
            // Wait until the last minute before starting the
            // timer.
            result.sampleStart();
	        PersonLookupResponseType responseType = client.getPersonDetails(headers.getSecurity(), headers.getMedcomHeader(), query);
	        result.sampleEnd();

	        if (responseType.getPersonInformationStructure().size() == 0) {
		        result.setSuccessful(false);
		        result.setResponseMessage("Expected at least 1 person in result, but found none");
	        } else {
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

	private String firstCprFromResponse(PersonLookupResponseType responseType) {
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
