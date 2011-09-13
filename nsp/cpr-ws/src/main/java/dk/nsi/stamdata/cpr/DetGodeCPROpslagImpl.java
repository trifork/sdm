package dk.nsi.stamdata.cpr;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.annotations.Whitelist;
import dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationOut;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationOut;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;

@WebService(serviceName = "DetGodeCprOpslag", endpointInterface = "dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag")
public class DetGodeCPROpslagImpl implements DetGodeCPROpslag
{	
	private static final String INTERNAL_SERVER_ERROR = "Internal Server Error.";
	private static final String NO_DATA_FOUND_FAULT_MSG = "Ingen data fundet";

	@Inject
	@Whitelist
	private Set<String> whitelist;
	
	@Inject
	private Provider<Fetcher> fetcherPool;
	
	@PostConstruct
	protected void init()
	{
		// This is a bit of a hack allowing Guice to inject
		// the dependencies without having to jump through
		// hoops to get it to do so automatically.
		
		ApplicationController.injector.injectMembers(this);
	}

	@Override
	public GetPersonInformationOut getPersonInformation(GetPersonInformationIn input)
	{
		// 1. Check the white list to see if the client is authorized.
		
		String clientCVR = "12345678";
		
		if (!whitelist.contains(clientCVR))
		{
			// TODO: Check the specification on what to do. I'm thinking a fault.
		}
		
		
		// 2. Fetch the person from the database.
		//
		// NOTE: Unfortunately the specification is defined so that we have to return a
		// fault if no person is found. We cannot change this to return nil which would
		// be a nicer protocol.
		
		
		String pnr = input.getPersonCivilRegistrationIdentifier();
		Person person = fetchPersonWithPnr(pnr);
		
		if (person == null)
		{
			returnSOAPFault(NO_DATA_FOUND_FAULT_MSG);
		}

		// We now have the requested person. Use it to fill in
		// the response.
		
		GetPersonInformationOut output = new GetPersonInformationOut();
		
		PersonInformationStructureType personInformation = new PersonInformationStructureType();
		personInformation.setCurrentPersonCivilRegistrationIdentifier(pnr);

		output.setPersonInformationStructure(personInformation);
		
		return output;
	}

	@Override
	public GetPersonWithHealthCareInformationOut getPersonWithHealthCareInformation(GetPersonWithHealthCareInformationIn parameters)
	{
		return null;
        // TODO: Add sikrede information to the response
	}
	
	// HELPERS
	
	private Person fetchPersonWithPnr(String pnr)
	{
		try
		{
			Fetcher fetcher = fetcherPool.get();
			return fetcher.fetch(Person.class, pnr);
		}
		catch (Exception e)
		{
			throw new RuntimeException(INTERNAL_SERVER_ERROR, e);
		}
	}
	
	private void returnSOAPFault(String message)
	{		
		SOAPFault fault = null;
		
		try
		{
			// We have to make sure to use the same protocol version
			// as defined in the WSDL.
			
			SOAPFactory factory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			
			fault = factory.createFault();
			fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
			
			// TODO: For some reason the xml:lang att. is always "en"
			// even when the locale is set in this next call.
			
			fault.setFaultString(message);
		}
		catch (Exception e)
		{
			returnServerErrorFault(e);
		}
		
		throw new SOAPFaultException(fault);
	}
	
	private void returnServerErrorFault(Exception e)
	{
		throw new RuntimeException(INTERNAL_SERVER_ERROR, e);
	}
}
