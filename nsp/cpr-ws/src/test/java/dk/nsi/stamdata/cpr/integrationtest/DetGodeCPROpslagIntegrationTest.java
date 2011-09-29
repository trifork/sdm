package dk.nsi.stamdata.cpr.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.soap.SOAPFaultException;

import org.hibernate.Session;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Stage;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.SikredeYderRelation;
import com.trifork.stamdata.models.sikrede.Yderregister;

import dk.nsi.dgws.DgwsIdcardFilter;
import dk.nsi.stamdata.cpr.ComponentController.ComponentModule;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SealNamespacePrefixSoapHandler;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SecurityWrapper;
import dk.nsi.stamdata.cpr.integrationtest.dgws.TestSTSMock;
import dk.nsi.stamdata.cpr.medcom.FaultMessages;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag;
import dk.nsi.stamdata.cpr.ws.DetGodeCPROpslagService;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationOut;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationOut;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.PersonHealthCareInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonWithHealthCareInformationStructureType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.sosi.seal.model.constants.FaultCodeValues;

public class DetGodeCPROpslagIntegrationTest extends AbstractWebAppEnvironmentJUnit4Test
{
	public static final QName DET_GODE_CPR_OPSLAG_SERVICE = new QName("http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/", "DetGodeCPROpslagService");
	public static final String CVR_WHITELISTED = "12345678";
	public static final String CVR_NOT_WHITELISTED = "87654321";

    private DetGodeCPROpslag client;

    @Inject
    private Session session;

    @BeforeClass
    public static void setIdcardFilterInTestMode() {
        // TODO: Comment why is this needed.

        System.setProperty(DgwsIdcardFilter.USE_TEST_FEDERATION_INIT_PARAM_KEY, "true");
    }

    @Before
    public void setUp() throws MalformedURLException {
        // Prepare the test,
        // using Guice to inject dependencies.

        Guice.createInjector(Stage.DEVELOPMENT, new ComponentModule()).injectMembers(this);

        // Clean out any existing data. (Because we don't have an in-memory db.)

        purgePersonTable();
        purgeSikrede();

        // This client is used to access the web-service.

        URL wsdlLocation = new URL("http://localhost:8100/service/DetGodeCPROpslag?wsdl");
        DetGodeCPROpslagService serviceCatalog = new DetGodeCPROpslagService(wsdlLocation, DET_GODE_CPR_OPSLAG_SERVICE);

        // TODO: Comment why this resolver is needed.

        serviceCatalog.setHandlerResolver(new HandlerResolver() {
            @Override
            @SuppressWarnings("rawtypes")
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                return Lists.newArrayList((Handler) new SealNamespacePrefixSoapHandler());
            }
        });

        client = serviceCatalog.getDetGodeCPROpslag();
    }

    @After
    public void tearDown() throws Exception {
        session.disconnect();
    }

    private void purgePersonTable() {
        session.createSQLQuery("TRUNCATE Person").executeUpdate();
    }

    private void purgeSikrede() {
        session.createSQLQuery("TRUNCATE SikredeYderRelation").executeUpdate();
        session.createSQLQuery("TRUNCATE Yderregister").executeUpdate();
    }

    @Test
    public void requestWithoutPersonIdentifierGivesSenderSoapFault() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();

        try {
            SecurityWrapper securityHeaders = TestSTSMock.getVocesTrustedSecurityWrapper(CVR_WHITELISTED, "foo", "bar");

            client.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);
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
            SecurityWrapper securityHeaders = TestSTSMock.getVocesTrustedSecurityWrapper(CVR_WHITELISTED, "foo", "bar");

            client.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);
            fail("Expected SOAPFault");
        } catch (SOAPFaultException fault) {
            assertEquals(SOAPConstants.SOAP_SENDER_FAULT, fault.getFault().getFaultCodeAsQName());
            assertEquals(FaultMessages.NO_DATA_FOUND_FAULT_MSG, fault.getFault().getFaultString());
        }
    }

    @Test
    @Ignore("Instead test that a person with active protection is protected.")
    public void requestWithCvrNotWhitelistedGivesSoapFaultWithDGWSNotAuthorizedFaultCode() throws Exception {
        GetPersonInformationIn request = new GetPersonInformationIn();
        request.setPersonCivilRegistrationIdentifier("1111111111");
        try {
            SecurityWrapper securityHeaders = TestSTSMock.getVocesTrustedSecurityWrapper(CVR_NOT_WHITELISTED, "foo", "bar");

            client.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);
            fail("Expected DGWS");
        } catch (DGWSFault fault) {
            Assert.assertEquals(FaultCodeValues.NOT_AUTHORIZED, fault.getMessage());
        }
    }

    @Test
    public void requestForExistingPersonGivesPersonInformation() throws Exception {
        session.getTransaction().begin();
        Person person = new Person();
        person.cpr = "1111111111";
        person.koen = "M";
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
        SecurityWrapper securityHeaders = TestSTSMock.getVocesTrustedSecurityWrapper(CVR_WHITELISTED, "foo", "bar");

        GetPersonInformationOut personInformation = client.getPersonInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);

        PersonInformationStructureType information = personInformation.getPersonInformationStructure();
        Assert.assertEquals("1111111111", information.getRegularCPRPerson().getSimpleCPRPerson().getPersonCivilRegistrationIdentifier());
        Assert.assertEquals("8464", information.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetCode());
    }

    @Test
    public void requestPersonWithHealthcareInformation() throws Exception {
        session.getTransaction().begin();
        Person person = new Person();
        person.cpr = "1111111111";
        person.koen = "M";
        person.vejKode = "8464";
        person.foedselsdato = new Date();
        person.setModifiedDate(new Date());
        person.setCreatedDate(new Date());
        person.setValidFrom(DateTime.now().minusDays(1).toDate());
        person.setValidTo(DateTime.now().plusDays(1).toDate());

        SikredeYderRelation sikredeYderRelation = new SikredeYderRelation();
        sikredeYderRelation.setGruppeKodeIkraftDato(new Date());
        sikredeYderRelation.setGruppekodeRegistreringDato(new Date());
        sikredeYderRelation.setYdernummerIkraftDato(new Date());
        sikredeYderRelation.setYdernummerRegistreringDato(new Date());
        sikredeYderRelation.setCreatedDate(new Date());
        sikredeYderRelation.setModifiedDate(new Date());
        sikredeYderRelation.setSikringsgruppeKode('1');
        sikredeYderRelation.setYdernummer(1234);
        sikredeYderRelation.setCpr(person.getCpr());
        sikredeYderRelation.setType("C");
        sikredeYderRelation.setId(sikredeYderRelation.getCpr() + "-" + sikredeYderRelation.getType());
        sikredeYderRelation.setValidFrom(DateTime.now().minusDays(1).toDate());
        sikredeYderRelation.setValidTo(DateTime.now().plusDays(1).toDate());

        Yderregister yderregister = new Yderregister();
        yderregister.setBynavn("Randers");
        yderregister.setCreatedDate(new Date());
        yderregister.setEmail("hej@verden.dk");
        yderregister.setModifiedDate(new Date());
        yderregister.setNavn("Jørgens Klinik");
        yderregister.setNummer(sikredeYderRelation.getYdernummer());
        yderregister.setPostnummer("8900");
        yderregister.setTelefon("89898989");
        yderregister.setValidFrom(DateTime.now().minusDays(1).toDate());
        yderregister.setValidTo(DateTime.now().plusDays(1).toDate());
        yderregister.setVejnavn("Rådhuspladsen 4");

        session.save(person);
        session.save(sikredeYderRelation);
        session.save(yderregister);
        
        session.flush();
        session.getTransaction().commit();

        GetPersonWithHealthCareInformationIn request = new GetPersonWithHealthCareInformationIn();
        request.setPersonCivilRegistrationIdentifier("1111111111");
        SecurityWrapper securityHeaders = TestSTSMock.getVocesTrustedSecurityWrapper(CVR_WHITELISTED, "foo", "bar");

        GetPersonWithHealthCareInformationOut personWithHealthCareInformation = client.getPersonWithHealthCareInformation(new Holder<Security>(securityHeaders.getSecurity()), new Holder<Header>(securityHeaders.getMedcomHeader()), request);

        PersonWithHealthCareInformationStructureType healthCareInformationStructure = personWithHealthCareInformation.getPersonWithHealthCareInformationStructure();

        PersonInformationStructureType personInformationStructure = healthCareInformationStructure.getPersonInformationStructure();
        Assert.assertEquals("1111111111", personInformationStructure.getRegularCPRPerson().getSimpleCPRPerson().getPersonCivilRegistrationIdentifier());
        Assert.assertEquals("8464", personInformationStructure.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetCode());

        PersonHealthCareInformationStructureType personHealthCareInformationStructure = healthCareInformationStructure.getPersonHealthCareInformationStructure();
        Assert.assertEquals("Ydernummer på tilknyttet læge matcher ikke", 1234, personHealthCareInformationStructure.getAssociatedGeneralPractitionerStructure().getAssociatedGeneralPractitionerIdentifier().intValue());

    }
}
