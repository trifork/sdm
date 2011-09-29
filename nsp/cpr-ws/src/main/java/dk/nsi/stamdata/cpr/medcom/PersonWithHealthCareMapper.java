package dk.nsi.stamdata.cpr.medcom;

import javax.xml.datatype.DatatypeConfigurationException;

import com.google.inject.Inject;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.Sikrede;

import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.ws.PersonHealthCareInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonWithHealthCareInformationStructureType;

public class PersonWithHealthCareMapper
{
	private final PersonMapper personMapper;

	@Inject
	PersonWithHealthCareMapper(PersonMapper personMapper)
	{
		this.personMapper = personMapper;
	}

	public PersonWithHealthCareInformationStructureType map(Person person, Sikrede sikrede, ServiceProtectionLevel protectionLevel) throws DatatypeConfigurationException
	{
		PersonHealthCareInformationStructureType output = createOutputWithRealDate(person);
		
		PersonWithHealthCareInformationStructureType personWithHealthCare = new PersonWithHealthCareInformationStructureType();
		personWithHealthCare.setPersonInformationStructure(personMapper.map(person, protectionLevel));
		personWithHealthCare.setPersonHealthCareInformationStructure(output);

		return personWithHealthCare;
	}

	private PersonHealthCareInformationStructureType createOutputWithRealDate(Person person) throws DatatypeConfigurationException
	{
		return null;
	}
}
