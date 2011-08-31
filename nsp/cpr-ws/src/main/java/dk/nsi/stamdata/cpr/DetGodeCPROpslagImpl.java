package dk.nsi.stamdata.cpr;

import dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonInformationOut;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationIn;
import dk.nsi.stamdata.cpr.ws.GetPersonWithHealthCareInformationOut;
import dk.nsi.stamdata.cpr.ws.PersonGenderCodeType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.RegularCPRPersonType;

public class DetGodeCPROpslagImpl implements DetGodeCPROpslag
{
	@Override
	public GetPersonInformationOut getPersonInformation(GetPersonInformationIn input)
	{
		// Look for a record in the database with the given CPR.
		
		String pnr = input.getPersonCivilRegistrationIdentifier();
		
		// TODO: Fetch from the database.
		
		GetPersonInformationOut output = new GetPersonInformationOut();
		
		// If no record is found return a SOAP Fault: "Ingen data fundet",
		// as defined in the specification.
		
		if (false)
		throw new RuntimeException("Ingen data fundet");
		
		// Serialize the record to the output format.
		
		PersonInformationStructureType person = new PersonStructureWrapper(person);
		
		return null;
	}

	@Override
	public GetPersonWithHealthCareInformationOut getPersonWithHealthCareInformation(GetPersonWithHealthCareInformationIn parameters)
	{
		// TODO Auto-generated method stub
		
		return null;
	}
	
	// Helpers
	
	public PersonGenderCodeType mapGenderCode(String value)
	{
		if ("M".equals(value))
		{
			return PersonGenderCodeType.MALE;
		}
		else if ("K".equals(value))
		{
			return PersonGenderCodeType.FEMALE;
		}
		else
		{
			return PersonGenderCodeType.UNKNOWN;
		}
	}
}
