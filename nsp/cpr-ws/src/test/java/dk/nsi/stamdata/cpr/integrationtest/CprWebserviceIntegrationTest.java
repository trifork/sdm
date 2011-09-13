package dk.nsi.stamdata.cpr.integrationtest;

import dk.nsi.stamdata.cpr.DetGodeCPROpslagFaultMessages;
import dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag;
import dk.nsi.stamdata.cpr.ws.DetGodeCPROpslagService;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationIn;
import org.hisrc.hifaces20.testing.webappenvironment.WebAppEnvironment;
import org.hisrc.hifaces20.testing.webappenvironment.annotations.PropertiesWebAppEnvironmentConfig;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.WebAppEnvironmentRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.ws.soap.SOAPFaultException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CprWebserviceIntegrationTest {
    public static final QName DET_GODE_CPR_OPSLAG_SERVICE = new QName("http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/", "DetGodeCPROpslagService");
    private WebAppEnvironment webAppEnvironment;

    @Rule
	public MethodRule webAppEnvironmentRule = WebAppEnvironmentRule.INSTANCE;
    private DetGodeCPROpslag opslag;


    @PropertiesWebAppEnvironmentConfig
    public void setWebAppEnvironment(WebAppEnvironment env) {
        webAppEnvironment = env;
    }

    @Before
    public void setupClient() throws MalformedURLException {
        URL wsdlLocation = new URL("http://localhost:8100/service/opslag");
        opslag = new DetGodeCPROpslagService(wsdlLocation, DET_GODE_CPR_OPSLAG_SERVICE).getDetGodeCPROpslag();
    }

	@Test
	public void requestWithoutPersonIdentifierGivesSenderSoapFault() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();

        try {
            opslag.getPersonInformation(request);
            fail("Expected SOAPFault");
        } catch (SOAPFaultException fault) {
            assertEquals(SOAPConstants.SOAP_SENDER_FAULT, fault.getFault().getFaultCodeAsQName());
        }
	}

    @Test
    public void requestWithNonExistingPersonIdentifierGivesSenderSoapFault() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();
        request.setPersonCivilRegistrationIdentifier("1111111111");

        try {
            opslag.getPersonInformation(request);
            fail("Expected SOAPFault");
        } catch (SOAPFaultException fault) {
            assertEquals(SOAPConstants.SOAP_SENDER_FAULT, fault.getFault().getFaultCodeAsQName());
            assertEquals(DetGodeCPROpslagFaultMessages.NO_DATA_FOUND_FAULT_MSG, fault.getFault().getFaultString());
        }
    }

    @Test
    public void requestWithCvrNotWhitelistedGivesSenderSoapFault() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();
        request.setPersonCivilRegistrationIdentifier("1111111111");



        try {
            opslag.getPersonInformation(request);
            fail("Expected SOAPFault");
        } catch (SOAPFaultException fault) {
            assertEquals(SOAPConstants.SOAP_SENDER_FAULT, fault.getFault().getFaultCodeAsQName());
            assertEquals(DetGodeCPROpslagFaultMessages.NO_DATA_FOUND_FAULT_MSG, fault.getFault().getFaultString());
        }
    }
}
