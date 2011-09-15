package dk.nsi.stamdata.cpr.integrationtest;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.models.cpr.Person;
import dk.nsi.stamdata.cpr.ApplicationController;
import dk.nsi.stamdata.cpr.DetGodeCPROpslagFaultMessages;
import dk.nsi.stamdata.cpr.SessionProvider;
import dk.nsi.stamdata.cpr.integrationtest.dgws.IdCardBuilder;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SealNamespacePrefixMapper;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SecurityWrapper;
import dk.nsi.stamdata.cpr.ws.*;
import dk.sosi.seal.model.constants.FaultCodeValues;
import javassist.bytecode.stackmap.MapMaker;
import org.hibernate.Session;
import org.hisrc.hifaces20.testing.webappenvironment.WebAppEnvironment;
import org.hisrc.hifaces20.testing.webappenvironment.annotations.PropertiesWebAppEnvironmentConfig;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.WebAppEnvironmentRule;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.asm.tree.JumpInsnNode;
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
    public static final String CVR_WHITELISTED = "12345678";
    public static final String CVR_NOT_WHITELISTED = "87654321";
    private WebAppEnvironment webAppEnvironment;

    @Rule
	public MethodRule webAppEnvironmentRule = WebAppEnvironmentRule.INSTANCE;
    private DetGodeCPROpslag opslag;
    private Session session;


    @PropertiesWebAppEnvironmentConfig
    public void setWebAppEnvironment(WebAppEnvironment env) {
        webAppEnvironment = env;
    }

    @Before
    public void setupSession() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = ConfigurationLoader.loadForName(ApplicationController.COMPONENT_NAME);
                bind(Session.class).toProvider(SessionProvider.class);
                Names.bindProperties(binder(), properties);
            }
        });

        session = injector.getInstance(Session.class);

        purgePersontable();
    }

    @Before
    public void setupClient() throws MalformedURLException {
        URL wsdlLocation = new URL("http://localhost:8100/service/opslag?wsdl");
        DetGodeCPROpslagService service = new DetGodeCPROpslagService(wsdlLocation, DET_GODE_CPR_OPSLAG_SERVICE);

        HandlerResolver handlerResolver = new HandlerResolver() {
            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlers = new ArrayList(1);
                handlers.add(new SealNamespacePrefixSoapHandler());
                return handlers;
            }
        };
        service.setHandlerResolver(handlerResolver);

        opslag = service.getDetGodeCPROpslag();
    }

    private void purgePersontable() {
        session.createSQLQuery("TRUNCATE Person").executeUpdate();
    }

    @Test
	public void requestWithoutPersonIdentifierGivesSenderSoapFault() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();

        try {
            SecurityWrapper securityHeaders = IdCardBuilder.getVocesTrustedSecurityWrapper(CVR_WHITELISTED, "foo", "bar");

            opslag.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);
            fail("Expected SOAPFault");
        } catch (SOAPFaultException fault) {
            assertEquals(SOAPConstants.SOAP_SENDER_FAULT, fault.getFault().getFaultCodeAsQName());
        }
	}

    @Test
    public void requestWithNonExistingPersonIdentifierGivesSenderSoapFault() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();
        request.setPersonCivilRegistrationIdentifier("7777777777");

        try {
            SecurityWrapper securityHeaders = IdCardBuilder.getVocesTrustedSecurityWrapper(CVR_WHITELISTED, "foo", "bar");

            opslag.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);
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
            SecurityWrapper securityHeaders = IdCardBuilder.getVocesTrustedSecurityWrapper(CVR_NOT_WHITELISTED, "foo", "bar");

            opslag.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);
            fail("Expected DGWS");
        } catch (DGWSFault fault) {
            Assert.assertEquals(FaultCodeValues.NOT_AUTHORIZED, fault.getMessage());
        }
    }

    @Test
    public void requestWithWhitelistedCvrAndExistingPersonGivesPersonInformation() throws Exception {
        session.getTransaction().begin();
        Person person = new Person();
        person.cpr="1111111111";
        person.koen="M";
        person.vejKode = "8464";
        person.foedselsdato = new Date();
        person.setModifiedDate(new Date());
        person.setCreatedDate(new Date());
        person.setValidFrom(DateTime.now().minusDays(1).toDate());
        person.setValidTo(DateTime.now().plusDays(1).toDate());

        session.save(person);
        session.flush();
        session.getTransaction().commit();

        GetPersonInformationIn request = new GetPersonInformationIn();
        request.setPersonCivilRegistrationIdentifier("1111111111");
        SecurityWrapper securityHeaders = IdCardBuilder.getVocesTrustedSecurityWrapper(CVR_WHITELISTED, "foo", "bar");

        GetPersonInformationOut personInformation = opslag.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);

        PersonInformationStructureType information = personInformation.getPersonInformationStructure();
        Assert.assertEquals("1111111111", information.getCurrentPersonCivilRegistrationIdentifier());
        Assert.assertEquals("8464", information.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetCode());
    }
}
