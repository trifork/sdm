package dk.nsi.stamdata.cpr.medcom;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import javax.xml.datatype.DatatypeConfigurationException;

import com.google.inject.Inject;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.Sikrede;

import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonWithHealthCareInformationStructureType;

public class PersonWithHealthCareMapper
{
	private PersonMapper personMapper;

	@Inject
	PersonWithHealthCareMapper(PersonMapper personMapper)
	{
		this.personMapper = checkNotNull(personMapper, "personMapper");
	}

	public PersonWithHealthCareInformationStructureType map(Person person, Sikrede sikrede) throws DatatypeConfigurationException
	{
		PersonWithHealthCareInformationStructureType personWithHealthCare = new PersonWithHealthCareInformationStructureType();

		PersonInformationStructureType personInformation = personMapper.map(person);

		personWithHealthCare.setPersonInformationStructure(personInformation);

		// TODO: Map the person information.

		return null;
	}
}
