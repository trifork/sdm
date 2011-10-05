package dk.nsi.stamdata.cpr.medcom;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.Holder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.Sikrede;
import com.trifork.stamdata.models.sikrede.SikredeYderRelation;
import com.trifork.stamdata.models.sikrede.Yderregister;

import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.PersonMapper.CPRProtectionLevel;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.SoapUtils;
import dk.nsi.stamdata.cpr.jaxws.GuiceInstanceResolver.GuiceWebservice;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationOut;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationOut;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonWithHealthCareInformationStructureType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.sosi.seal.model.SystemIDCard;


@SchemaValidation
@GuiceWebservice
@WebService(serviceName = "DetGodeCprOpslag", endpointInterface = "dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag")
public class DetGodeCPROpslagImpl implements DetGodeCPROpslag
{
	private static final Logger logger = LoggerFactory.getLogger(DetGodeCPROpslagImpl.class);

	private static final String NS_DET_GODE_CPR_OPSLAG = "http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/";
	private static final String NS_DGWS_1_0 = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd";
	private static final String NS_WS_SECURITY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

	private final Fetcher fetcher;
	private final PersonMapper personMapper;
	private final String clientCVR;


	@Inject
	DetGodeCPROpslagImpl(Fetcher fetcher, PersonMapper personMapper, SystemIDCard card)
	{
		this.fetcher = fetcher;
		this.personMapper = personMapper;
		this.clientCVR = card.getSystemInfo().getCareProvider().getID();
	}


	// FIXME: Headers should be set to outgoing. See BRS for correct way of
	// setting these.
	@Override
	public GetPersonInformationOut getPersonInformation(@WebParam(name = "Security", targetNamespace = NS_WS_SECURITY, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader, @WebParam(name = "Header", targetNamespace = NS_DGWS_1_0, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader, @WebParam(name = "getPersonInformationIn", targetNamespace = NS_DET_GODE_CPR_OPSLAG, partName = "parameters") GetPersonInformationIn input) throws DGWSFault
	{
		SoapUtils.setHeadersToOutgoing(wsseHeader, medcomHeader);

		// 1. Check the white list to see if the client is authorized.

		String pnr = input.getPersonCivilRegistrationIdentifier();

		logAccess(pnr);

		// 2. Validate the input parameters.

		checkInputParameters(pnr);

		// 3. Fetch the person from the database.

		Person person = fetchPersonWithPnr(pnr);

		// We now have the requested person. Use it to fill in
		// the response.

		GetPersonInformationOut output = new GetPersonInformationOut();
		PersonInformationStructureType personInformation;

		try
		{
			personInformation = personMapper.map(person, ServiceProtectionLevel.AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR);
		}
		catch (DatatypeConfigurationException e)
		{
			throw SoapUtils.newServerErrorFault(e);
		}

		output.setPersonInformationStructure(personInformation);

		return output;
	}


	@Override
	public GetPersonWithHealthCareInformationOut getPersonWithHealthCareInformation(@WebParam(name = "Security", targetNamespace = NS_WS_SECURITY, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader, @WebParam(name = "Header", targetNamespace = NS_DGWS_1_0, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader, @WebParam(name = "getPersonWithHealthCareInformationIn", targetNamespace = NS_DET_GODE_CPR_OPSLAG, partName = "parameters") GetPersonWithHealthCareInformationIn parameters) throws DGWSFault
	{
		SoapUtils.setHeadersToOutgoing(wsseHeader, medcomHeader);

		// 1. Check the white list to see if the client is authorized.

		String pnr = parameters.getPersonCivilRegistrationIdentifier();

		logAccess(pnr);

		// 2. Validate the input parameters.

		checkInputParameters(pnr);

		// 2. Fetch the person from the database.
		//
		// NOTE: Unfortunately the specification is defined so that we have to
		// return a fault if no person is found. We cannot change this to return
		// nil
		// which would be a nicer protocol.

		Person person = fetchPersonWithPnr(pnr);

		SikredeYderRelation sikredeYderRelation = fetchSikredeYderRelationWithPnr(pnr + "-C");
		Yderregister yderregister = fetchYderregisterForPnr(sikredeYderRelation.getYdernummer());

		GetPersonWithHealthCareInformationOut output = new GetPersonWithHealthCareInformationOut();
		PersonWithHealthCareInformationStructureType personWithHealthCareInformation = null;

		try
		{
			personWithHealthCareInformation = personMapper.map(person, sikredeYderRelation, yderregister);
		}
		catch (DatatypeConfigurationException e)
		{
			throw SoapUtils.newServerErrorFault(e);
		}

		output.setPersonWithHealthCareInformationStructure(personWithHealthCareInformation);

		return output;
	}


	// HELPERS

	private Person fetchPersonWithPnr(String pnr)
	{
		checkNotNull(pnr, "pnr");

		Person person;

		try
		{
			person = fetcher.fetch(Person.class, pnr);
		}
		catch (Exception e)
		{
			throw SoapUtils.newServerErrorFault(e);
		}

		// NOTE: Unfortunately the specification is defined so that we have to
		// return a
		// fault if no person is found. We cannot change this to return nil
		// which would
		// be a nicer protocol.

		if (person == null)
		{
			throw SoapUtils.newSOAPSenderFault(FaultMessages.NO_DATA_FOUND_FAULT_MSG);
		}

		return person;
	}


	// HELPERS

	private SikredeYderRelation fetchSikredeYderRelationWithPnr(String pnr)
	{
		checkNotNull(pnr, "pnr");
		SikredeYderRelation sikredeYderRelation = null;

		try
		{
			sikredeYderRelation = fetcher.fetch(SikredeYderRelation.class, pnr);
		}
		catch (Exception e)
		{
			throw SoapUtils.newServerErrorFault(e);
		}

		return sikredeYderRelation;

	}


	private Yderregister fetchYderregisterForPnr(int ydernummer)
	{
		Yderregister yderregister = null;
		try
		{
			yderregister = fetcher.fetch(Yderregister.class, ydernummer);
		}
		catch (Exception e)
		{
			throw SoapUtils.newServerErrorFault(e);
		}
		return yderregister;
	}


	@SuppressWarnings("unused")
	private Sikrede fetchSikredeWithPnr(String cpr)
	{
		checkNotNull(cpr, "cpr");

		Sikrede sikrede = null;

		try
		{
			// sikrede = fetcher.fetch(Sikrede.class, pnr); //TODO
		}
		catch (Exception e)
		{
			throw SoapUtils.newServerErrorFault(e);
		}

		if (sikrede == null)
		{
			throw SoapUtils.newSOAPSenderFault(FaultMessages.NO_DATA_FOUND_FAULT_MSG);
		}

		return sikrede;
	}


	private void checkInputParameters(@Nullable String pnr)
	{
		if (StringUtils.isBlank(pnr))
		{
			// This service should match functionality from a service that was
			// not DGWS protected.
			// Callers expect to be met with a SOAP sender fault.
			// Callers to the PVIT service should expect DGWS faults instead.
			throw SoapUtils.newSOAPSenderFault("PersonCivilRegistrationIdentifier was not set in request, but is required.");
		}
	}


	private void logAccess(String requestedCPR) throws DGWSFault
	{
		logger.info("type=auditlog, service=stamdata-cpr, client_cvr={}, requested_cpr={}", clientCVR, requestedCPR);
	}

}
