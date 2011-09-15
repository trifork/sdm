package dk.nsi.stamdata.cpr;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.models.cpr.Person;
import dk.nsi.dgws.DgwsIdcardFilter;
import dk.nsi.stamdata.cpr.annotations.Whitelist;
import dk.nsi.stamdata.cpr.ws.*;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.FaultCodeValues;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.ServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static com.trifork.stamdata.Preconditions.checkState;

@WebService(serviceName = "DetGodeCprOpslag", endpointInterface = "dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag")
public class DetGodeCPROpslagImpl implements DetGodeCPROpslag
{	
	private static Logger logger = LoggerFactory.getLogger(DetGodeCPROpslagImpl.class);
	
	private static final String NS_TNS = "http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/";
	private static final String NS_DGWS_1_0 = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd"; // TODO: Shouldn't this be 1.0.1?
	private static final String NS_WS_SECURITY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	
    @Inject
	@Whitelist
	private Set<String> whitelist;
	
	@Inject
	private Provider<Fetcher> fetcherPool;

    @Resource
    private WebServiceContext context;
	
	@PostConstruct
	protected void init()
	{
		// This is a bit of a hack allowing Guice to inject
		// the dependencies without having to jump through
		// hoops to get it to do so automatically.
		
		checkState(ApplicationController.injector != null, "The application controller must be instanciated before jax-ws.");
		
		ApplicationController.injector.injectMembers(this);
	}

	@Override
    public GetPersonInformationOut getPersonInformation(
            @WebParam(name = "Security",
                      targetNamespace = NS_WS_SECURITY,
                      mode = WebParam.Mode.INOUT,
                      partName = "wsseHeader")
                      Holder<Security> wsseHeader,
            @WebParam(name = "Header",
                      targetNamespace = NS_DGWS_1_0,
                      mode = WebParam.Mode.INOUT,
                      partName = "medcomHeader")
                      Holder<Header> medcomHeader,
            @WebParam(name = "getPersonInformationIn",
                      targetNamespace = NS_TNS,
                      partName = "parameters")
                      GetPersonInformationIn input) throws DGWSFault {
		// 1. Check the white list to see if the client is authorized.

        String clientCVR = fetchIDCardFromRequestContext().getSystemInfo().getCareProvider().getID();
        String pnr = input.getPersonCivilRegistrationIdentifier();

        if (!whitelist.contains(clientCVR)) {
            logger.warn("Unauthorized access attempt. client_cvr={}, requested_pnr={}", clientCVR, pnr);
            throwDGWSFault(wsseHeader, medcomHeader, DetGodeCPROpslagFaultMessages.CALLER_NOT_AUTHORIZED, FaultCodeValues.NOT_AUTHORIZED);
        } else {
            logger.info("Access granted. client_cvr={}, requested_pnr={}", clientCVR, pnr);
        }

        // 2. Fetch the person from the database.

		checkClientAuthorization(pnr, wsseHeader, medcomHeader);

		// 2. Validate the input parameters.

		checkInputParameters(pnr);
		
		// 2. Fetch the person from the database.
		//
		// NOTE: Unfortunately the specification is defined so that we have to return a
		// fault if no person is found. We cannot change this to return nil which would
		// be a nicer protocol.

		Person person = fetchPersonWithPnr(pnr);

		// We now have the requested person. Use it to fill in
		// the response.
		
		GetPersonInformationOut output = new GetPersonInformationOut();
		PersonInformationStructureType personInformation = mapPersonInformation(person);
		output.setPersonInformationStructure(personInformation);
		
		return output;
	}


	@Override
	public GetPersonWithHealthCareInformationOut getPersonWithHealthCareInformation(
			@WebParam(	name = "Security",
						targetNamespace = NS_WS_SECURITY,
						mode = WebParam.Mode.INOUT,
						partName = "wsseHeader")
						Holder<Security> wsseHeader,
			@WebParam(	name = "Header",
						targetNamespace = NS_DGWS_1_0,
						mode = WebParam.Mode.INOUT,
						partName = "medcomHeader")
						Holder<Header> medcomHeader,
			@WebParam(	name = "getPersonWithHealthCareInformationIn",
						targetNamespace = NS_TNS,
						partName = "parameters")
						GetPersonWithHealthCareInformationIn parameters) throws DGWSFault {
		// 1. Check the white list to see if the client is authorized.

		String pnr = parameters.getPersonCivilRegistrationIdentifier();

		checkClientAuthorization(pnr, wsseHeader, medcomHeader);

		// 2. Validate the input parameters.

		checkInputParameters(pnr);

		// 2. Fetch the person from the database.
		//
		// NOTE: Unfortunately the specification is defined so that we have to
		// return a fault if no person is found. We cannot change this to return nil
		// which would be a nicer protocol.

		Person person = fetchPersonWithPnr(pnr);

		GetPersonWithHealthCareInformationOut output = new GetPersonWithHealthCareInformationOut();

		return output;
	}
	
	// HELPERS
    
    private SystemIDCard fetchIDCardFromRequestContext()
    {
        ServletRequest servletRequest = (ServletRequest)context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        SystemIDCard idcard = (SystemIDCard)servletRequest.getAttribute(DgwsIdcardFilter.IDCARD_REQUEST_ATTRIBUTE_KEY);
        
        // We are counting on the DGWS filter to inject the ID Card
        // into the request context. In fact we can never get to this
        // point if the request did not have a ID-card. Therefore if
        // the id card is null, the service is in an inconsistent state.
        
        Preconditions.checkState(idcard != null, "The SOSI ID Card was not injected to the request context.");
        
        return idcard;
    }
	
	private Person fetchPersonWithPnr(String pnr)
	{
		checkNotNull(pnr, "pnr");
		
		Person person;

		try
		{
			Fetcher fetcher = fetcherPool.get();
			person = fetcher.fetch(Person.class, pnr);
		}
		catch (Exception e)
		{
			throw returnServerErrorFault(e);
		}

		if (person == null)
		{
			throw returnSOAPSenderFault(DetGodeCPROpslagFaultMessages.NO_DATA_FOUND_FAULT_MSG);
	}

		return person;
	}

    private void throwDGWSFault(Holder<Security> securityHolder, Holder<Header> medcomHeaderHolder, String status, String errorMsg) throws DGWSFault
	{
        DGWSHeaderUtil.setHeadersToOutgoing(securityHolder, medcomHeaderHolder);
        medcomHeaderHolder.value.setFlowStatus(status);
        throw new DGWSFault(errorMsg, "DGWS error");
    }

	private SOAPFaultException returnSOAPSenderFault(String message)
	{
		checkNotNull(message, "message");
		
		SOAPFault fault;
		
		try
		{
			// We have to make sure to use the same protocol version
			// as defined in the WSDL.
			
			SOAPFactory factory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			
			fault = factory.createFault();
			fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
			
			// TODO: For some reason the xml:lang att. is always "en"
			// even when the locale is set in this next call.
			
			fault.setFaultString(message);
		}
		catch (Exception e)
		{
			throw returnServerErrorFault(e);
		}
		
		return new SOAPFaultException(fault);
	}
	

	private RuntimeException returnServerErrorFault(Exception e)
	{
		checkNotNull(e, "e");
		
		return new RuntimeException(DetGodeCPROpslagFaultMessages.INTERNAL_SERVER_ERROR, e);
	}


	private XMLGregorianCalendar newXMLGregorianCalendar(Date date)
	{
		try
		{
			DatatypeFactory factory = DatatypeFactory.newInstance();
			return factory.newXMLGregorianCalendar(new DateTime(date).toGregorianCalendar());
		}
		catch (DatatypeConfigurationException e)
		{
			throw returnServerErrorFault(e);
		}
	}


	private PersonInformationStructureType mapPersonInformation(Person person)
	{
		PersonInformationStructureType personInformation = new PersonInformationStructureType();
		personInformation.setCurrentPersonCivilRegistrationIdentifier(person.cpr);

		RegularCPRPersonType regularCprPerson = new RegularCPRPersonType();
		SimpleCPRPersonType simpleCprPerson = new SimpleCPRPersonType();

		// PERSON NAME STRUCTURE SECTION

		PersonNameStructureType personName = new PersonNameStructureType();
		simpleCprPerson.setPersonNameStructure(personName);

		personName.setPersonGivenName(person.fornavn);

		// Middle name is optional.

		if (!StringUtils.isBlank(person.mellemnavn))
		{
			personName.setPersonMiddleName(person.mellemnavn);
		}

		personName.setPersonSurnameName(person.efternavn);

		simpleCprPerson.setPersonCivilRegistrationIdentifier(person.cpr);

		regularCprPerson.setSimpleCPRPerson(simpleCprPerson);

		regularCprPerson.setPersonNameForAddressingName(person.fornavn); // FIXME: Is this correct. This might be missing for the importer.

		if ("M".equalsIgnoreCase(person.koen))
		{
			regularCprPerson.setPersonGenderCode(PersonGenderCodeType.MALE);
		}
		else if ("K".equalsIgnoreCase(person.koen))
		{
			regularCprPerson.setPersonGenderCode(PersonGenderCodeType.FEMALE);
		}
		else
		{
			regularCprPerson.setPersonGenderCode(PersonGenderCodeType.UNKNOWN);
		}

		regularCprPerson.setPersonInformationProtectionIndicator(true); // FIXME: Does this mean that there is a start date element later or that one is active?

		// BIRTH DATE

		PersonBirthDateStructureType personBirthDate = new PersonBirthDateStructureType();

		personBirthDate.setBirthDate(newXMLGregorianCalendar(person.foedselsdato));
		personBirthDate.setBirthDateUncertaintyIndicator(false); // FIXME: This is not stored by the importer. Requires updated sql schema and importer update.

		regularCprPerson.setPersonBirthDateStructure(personBirthDate);

		// CIVIL STATUS

		PersonCivilRegistrationStatusStructureType personCivil = new PersonCivilRegistrationStatusStructureType();

		personCivil.setPersonCivilRegistrationStatusCode(BigInteger.ONE); // FIXME: This information comes from another CPR Posttype and needs to get a new SQL table and extention to the importer.
		personCivil.setPersonCivilRegistrationStatusStartDate(newXMLGregorianCalendar(new Date())); // FIXME: This is fake data.

		regularCprPerson.setPersonCivilRegistrationStatusStructure(personCivil);

		//
		// PERSON ADDRESS
		//

		PersonAddressStructureType personAddress = new PersonAddressStructureType();

		if (person.navnebeskyttelsestartdato != null)
		{
			personAddress.setPersonInformationProtectionStartDate(newXMLGregorianCalendar(person.navnebeskyttelsestartdato));
		}

		personAddress.setCountyCode("Fake Value"); // FIXME: We don't import this value. Amt or Region.

		if (StringUtils.isNotBlank(person.coNavn))
		{
			personAddress.setCareOfName(person.coNavn);
		}

		AddressCompleteType addressComplete = new AddressCompleteType();

		AddressAccessType addressAccess = new AddressAccessType();

		addressAccess.setMunicipalityCode(person.kommuneKode);
		addressAccess.setStreetCode(person.vejKode);
		addressAccess.setStreetBuildingIdentifier(person.bygningsnummer);

		addressComplete.setAddressAccess(addressAccess);

		AddressPostalType addressPostal = new AddressPostalType();
		addressComplete.setAddressPostal(addressPostal);

		if (StringUtils.isNotBlank(""))
		{
			addressPostal.setMailDeliverySublocationIdentifier("Fake Value"); // FIXME: The importer does not import this field.
		}

		addressPostal.setStreetName(person.vejnavn);

		if (StringUtils.isNotBlank(""))
		{
			addressPostal.setStreetNameForAddressingName("Fake Value"); // FIXME: The importer does not import this field.
		}

		addressPostal.setStreetBuildingIdentifier(person.bygningsnummer);

		if (StringUtils.isNotBlank(""))
		{
			addressPostal.setFloorIdentifier("Fake Value"); // FIXME: The importer does not import this field.
		}

		if (StringUtils.isNotBlank(person.sideDoerNummer))
		{
			addressPostal.setSuiteIdentifier(person.sideDoerNummer);
		}

		if (StringUtils.isNotBlank(person.lokalitet)) // TODO: We are not sure this is the correct field.
		{
			addressPostal.setDistrictSubdivisionIdentifier(person.lokalitet);
		}

		if (StringUtils.isNotBlank(""))
		{
			addressPostal.setPostOfficeBoxIdentifier(-1); // FIXME: The importer does not import this field.
		}

		addressPostal.setPostCodeIdentifier(person.postnummer);
		addressPostal.setDistrictName(person.postdistrikt);

		if (StringUtils.isNotBlank("")) // FIXME: The importer does not import this field.
		{
			CountryIdentificationCodeType country = new CountryIdentificationCodeType();
			country.setScheme(CountryIdentificationSchemeType.ISO_3166_ALPHA_2); // Two alpha-numerical characters.
			country.setValue("DK");
			addressPostal.setCountryIdentificationCode(country); // FIXME: The importer does not import this field.
		}

		personAddress.setAddressComplete(addressComplete);
        personInformation.setPersonAddressStructure(personAddress);

		personInformation.setRegularCPRPerson(regularCprPerson);
		return personInformation;
	}


	private void checkInputParameters(String pnr)
	{
		if (StringUtils.isBlank(pnr))
		{
			throw returnSOAPSenderFault("PersonCivilRegistrationIdentifier was not set in request, but is required.");
		}
	}


	private void checkClientAuthorization(String requestedPNR, Holder<Security> wsseHeader, Holder<Header> medcomHeader) throws DGWSFault
    {
		String clientCVR = fetchIDCardFromRequestContext().getSystemInfo().getCareProvider().getID();

		if (!whitelist.contains(clientCVR))
		{
            logger.warn("Unauthorized access attempt. client_cvr={}, requested_pnr={}", clientCVR, requestedPNR);
            throwDGWSFault(wsseHeader, medcomHeader, DetGodeCPROpslagFaultMessages.CALLER_NOT_AUTHORIZED, FaultCodeValues.NOT_AUTHORIZED);
        }
		else
		{
            logger.info("Access granted. client_cvr={}, requested_pnr={}", clientCVR, requestedPNR);
        }
	}
}
