package dk.nsi.stamdata.cpr;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jws.WebService;

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
		
		String clientCVR = "12345678"; // TODO: Get the CVR from the ID Card.
		
		if (!whitelist.contains(clientCVR))
		{
			// TODO: Check the specification on what to do. I'm thinking a fault.
		}
		
		// 2. Fetch the person from the database.
		//
		// NOTE: Unfortunately the specification is defined so that we have to return a
		// fault if no person is found. We cannot change this to return nil.
		
		String pnr = input.getPersonCivilRegistrationIdentifier();
		Person person = fetchPersonWithPnr(pnr);
		if (person == null) throw new RuntimeException(NO_DATA_FOUND_FAULT_MSG);

		// Serialize the record to the output format.
		
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
			throw new RuntimeException("Internal Service Exception.", e);
		}
	}
}
