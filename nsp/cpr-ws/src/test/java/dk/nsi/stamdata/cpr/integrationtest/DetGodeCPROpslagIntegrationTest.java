package dk.nsi.stamdata.cpr.integrationtest;

import dk.nsi.stamdata.cpr.DetGodeCPROpslagFaultMessages;
import dk.nsi.stamdata.cpr.integrationtest.dgws.IdCardBuilder;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SealNamespacePrefixMapper;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SecurityWrapper;
import dk.nsi.stamdata.cpr.ws.*;
import org.hisrc.hifaces20.testing.webappenvironment.WebAppEnvironment;
import org.hisrc.hifaces20.testing.webappenvironment.annotations.PropertiesWebAppEnvironmentConfig;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.WebAppEnvironmentRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DetGodeCPROpslagIntegrationTest {
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
        URL wsdlLocation = new URL("http://localhost:8100/service/opslag?wsdl");
        DetGodeCPROpslagService service = new DetGodeCPROpslagService(wsdlLocation, DET_GODE_CPR_OPSLAG_SERVICE);
        HandlerResolver handlerResolver = new HandlerResolver() {
            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlers = new ArrayList<Handler>(1);
                handlers.add(new SOAPHandler<SOAPMessageContext>() {
                    private Map<String,String> prefixMap = SealNamespacePrefixMapper.prefixMap;

                    @Override
                    public Set<QName> getHeaders() {
                        return null;
                    }

                    @Override
                    public boolean handleMessage(SOAPMessageContext context) {
                        try {
                            SOAPHeader soapHeader = context.getMessage().getSOAPHeader();

                            for (Map.Entry<String, String> prefixMapEntry: prefixMap.entrySet()) {
                                soapHeader.addNamespaceDeclaration(prefixMapEntry.getValue(), prefixMapEntry.getKey());
                            }

                            Iterator iterator = soapHeader.examineAllHeaderElements();
                            while (iterator.hasNext()) {
                                SOAPHeaderElement element = (SOAPHeaderElement)iterator.next();
                                changePrefix(element.getChildNodes());
                            }
                        } catch (SOAPException e) {
                            throw new RuntimeException(e);
                        }

                        return true;
                    }

                    private void changePrefix(NodeList elementsToProcess) {
                        for (int i = 0; i < elementsToProcess.getLength(); i++) {
                            Node node = elementsToProcess.item(i);
                            String nodeNS = node.getNamespaceURI();
                            if (prefixMap.containsKey(nodeNS)) {
                                node.setPrefix(prefixMap.get(nodeNS));
                            }

                            changePrefix(node.getChildNodes());
                        }
                    }

                    @Override
                    public boolean handleFault(SOAPMessageContext context) {
                        return true;
                    }

                    @Override
                    public void close(MessageContext context) { }
                });
                return handlers;
            }
        };
        service.setHandlerResolver(handlerResolver);
        opslag = service.getDetGodeCPROpslag();
    }

	@Test
	public void requestWithoutPersonIdentifierGivesSenderSoapFault() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();

        try {
            Header medcomHeader = null;
            Security wsseHeader = new Security();

            opslag.getPersonInformation(new Holder<Security>(wsseHeader), new Holder<Header>(medcomHeader), request);
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
            Header medcomHeader = null;
            Security wsseHeader = new Security();

            opslag.getPersonInformation(new Holder<Security>(wsseHeader), new Holder<Header>(medcomHeader), request);
            fail("Expected SOAPFault");
        } catch (SOAPFaultException fault) {
            assertEquals(SOAPConstants.SOAP_SENDER_FAULT, fault.getFault().getFaultCodeAsQName());
            assertEquals(DetGodeCPROpslagFaultMessages.NO_DATA_FOUND_FAULT_MSG, fault.getFault().getFaultString());
        }
    }

    @Test
    public void requestWithCvrNotWhitelistedGivesSoapFaultWithDGWSNotAuthorizedFaultCode() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();
        request.setPersonCivilRegistrationIdentifier("1111111111");
        try {
            SecurityWrapper securityHeaders = IdCardBuilder.getVocesTrustedSecurityWrapper("123", "foo", "bar");

            Assert.assertNotNull(securityHeaders.getSecurity());
            Assert.assertNotNull(securityHeaders.getMedcomHeader());

            opslag.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);
            fail("Expected SOAPFault");
        } catch (SOAPFaultException fault) {
            assertEquals(SOAPConstants.SOAP_SENDER_FAULT, fault.getFault().getFaultCodeAsQName());
            DetailEntry next = (DetailEntry)fault.getFault().getDetail().getDetailEntries().next();
        }
    }
}
