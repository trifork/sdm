package dk.nsi.stamdata.cpr.medcom;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;

import java.util.Date;
import java.util.Set;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.Holder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.Sikrede;

import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.SoapFaultUtil;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.jaxws.GuiceInstanceResolver.GuiceWebservice;
import dk.nsi.stamdata.cpr.pvit.WhitelistProvider.Whitelist;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationOut;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationOut;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.sosi.seal.model.SystemIDCard;

@GuiceWebservice
@WebService(serviceName = "DetGodeCprOpslag", endpointInterface = "dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag")
public class DetGodeCPROpslagImpl implements DetGodeCPROpslag
{
	private static final Logger logger = LoggerFactory.getLogger(DetGodeCPROpslagImpl.class);
	
	private static final String NS_TNS = "http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/";
	private static final String NS_DGWS_1_0 = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd"; // TODO: Shouldn't this be 1.0.1?
	private static final String NS_WS_SECURITY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	
	private final Fetcher fetcher;
	private final PersonMapper personMapper;
	private final PersonWithHealthCareMapper personWithHealthCareMapper;
	private final String clientCVR;

	
	@Inject
	DetGodeCPROpslagImpl(Fetcher fetcher, PersonMapper personMapper, PersonWithHealthCareMapper personWithHealthCareMapper, SystemIDCard card)
	{
		this.fetcher = fetcher;
		this.personMapper = personMapper;
		this.personWithHealthCareMapper = personWithHealthCareMapper;
		this.clientCVR = card.getSystemInfo().getCareProvider().getID();
	}

	@Override
	public GetPersonInformationOut getPersonInformation(@WebParam(name = "Security", targetNamespace = NS_WS_SECURITY, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader, @WebParam(name = "Header", targetNamespace = NS_DGWS_1_0, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader, @WebParam(name = "getPersonInformationIn", targetNamespace = NS_TNS, partName = "parameters") GetPersonInformationIn input) throws DGWSFault
	{
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
            personInformation = personMapper.map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);
        }
        catch (DatatypeConfigurationException e)
        {
            throw SoapFaultUtil.newServerErrorFault(e);
        }
        
        output.setPersonInformationStructure(personInformation);
        
		return output;
	}


	@Override
	public GetPersonWithHealthCareInformationOut getPersonWithHealthCareInformation(@WebParam(name = "Security", targetNamespace = NS_WS_SECURITY, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader, @WebParam(	name = "Header", targetNamespace = NS_DGWS_1_0, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader, @WebParam(name = "getPersonWithHealthCareInformationIn", targetNamespace = NS_TNS, partName = "parameters") GetPersonWithHealthCareInformationIn parameters) throws DGWSFault
	{
		// 1. Check the white list to see if the client is authorized.

		String pnr = parameters.getPersonCivilRegistrationIdentifier();

		logAccess(pnr);

		// 2. Validate the input parameters.

		checkInputParameters(pnr);

		// 2. Fetch the person from the database.
		//
		// NOTE: Unfortunately the specification is defined so that we have to
		// return a fault if no person is found. We cannot change this to return nil
		// which would be a nicer protocol.
		
		Person person = fetchPersonWithPnr(pnr);
		Sikrede sikrede = null; // TODO: Fetch the "sikrede" record for the pnr.
		
		GetPersonWithHealthCareInformationOut output = new GetPersonWithHealthCareInformationOut();

		try
		{
			output.setPersonWithHealthCareInformationStructure(personWithHealthCareMapper.map(person, sikrede, ServiceProtectionLevel.AlwaysCensorProtectedData));
		}
		catch (DatatypeConfigurationException e)
		{
			throw SoapFaultUtil.newServerErrorFault(e);
		}
		
		return output;
	}
	
	// HELPERS
	
	private Person fetchPersonWithPnr(String cpr)
	{
		checkNotNull(cpr, "cpr");
		
		Person person;

		try
		{
			person = fetcher.fetch(Person.class, cpr);
		}
		catch (Exception e)
		{
			throw SoapFaultUtil.newServerErrorFault(e);
		}

		// NOTE: Unfortunately the specification is defined so that we have to return a
		// fault if no person is found. We cannot change this to return nil which would
		// be a nicer protocol.

		if (person == null)
		{
			throw SoapFaultUtil.newSOAPSenderFault(FaultMessages.NO_DATA_FOUND_FAULT_MSG);
		}

		return person;
	}

    @SuppressWarnings("unused")
	private Sikrede fetchSikredeWithPnr(String cpr)
    {
        checkNotNull(cpr, "cpr");

        Sikrede sikrede = null;

        try
        {
            //sikrede = fetcher.fetch(Sikrede.class, pnr); //TODO 
        }
        catch (Exception e)
        {
            throw SoapFaultUtil.newServerErrorFault(e);
        }

        if (sikrede == null)
		{
            throw SoapFaultUtil.newSOAPSenderFault(FaultMessages.NO_DATA_FOUND_FAULT_MSG);
		}
        
        return sikrede;
    }

	private void checkInputParameters(@Nullable String pnr)
	{
		if (StringUtils.isBlank(pnr))
		{
			throw SoapFaultUtil.newSOAPSenderFault("PersonCivilRegistrationIdentifier was not set in request, but is required.");
		}
	}

	private void logAccess(String requestedCPR) throws DGWSFault
	{
		logger.info("type=auditlog, service=stamdata-cpr, client_cvr={}, requested_cpr={}", clientCVR, requestedCPR);
	}
}
