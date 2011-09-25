package dk.nsi.stamdata.cpr;

import static org.junit.Assert.assertEquals;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.medcom.PersonMapper;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;

public class PersonMapperTest
{
	private PersonMapper mapper;
	private Person person;

	@Before
	public void setup()
	{
		mapper = new PersonMapper();

		person = new Person();
		person.setFornavn("Fornavn");
	}

	@Test
	public void mapsFirstname() throws DatatypeConfigurationException
	{
		PersonInformationStructureType jaxbPerson = mapper.map(person);
		assertEquals(person.getFornavn(), jaxbPerson.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName());
	}
}
