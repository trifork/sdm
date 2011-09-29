package dk.nsi.stamdata.cpr;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.integrationtest.dgws.TestSTSMock;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.sosi.seal.model.SystemIDCard;

public class PersonMapperFieldMappingTest
{
	private Person createPerson()
	{
		Person person = new Person();
		
		person.setGaeldendeCPR("2345678901");
		
		person.setFornavn("Peter");
		person.setMellemnavn("Sigurd");
		person.setEfternavn("Andersen");
		
		person.setCpr("1234567890");
		
		person.setKoen("M");
		
		person.setFoedselsdato(new Date());
		
		person.setCoNavn("Søren Petersen");
		
		person.setKommuneKode("123");
		person.setVejKode("234");
		person.setHusnummer("10");
		person.setBygningsnummer("A");
		person.setLokalitet("Birkely");
		person.setVejnavn("Ørstedgade");
		person.setEtage("12");
		person.setSideDoerNummer("tv");
		
		person.setPostnummer("6666");
		person.setPostdistrikt("Überwald");
		
		person.setNavnebeskyttelsestartdato(null);
		person.setNavnebeskyttelsestartdato(null);

		return person;
	}
	
	Person person;
	PersonInformationStructureType output;
	
	@Before
	public void setUp() throws DatatypeConfigurationException
	{
		person = createPerson();
		
		SystemIDCard idCard = TestSTSMock.createTestSTSSignedIDCard("12345678");
		output = new PersonMapper(Sets.<String>newHashSet(), idCard).map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);
	}
	
	@Test
	public void mapCurrentPersonCivilRegistrationIdentifier()
	{
		assertEquals(person.getGaeldendeCPR(), output.getCurrentPersonCivilRegistrationIdentifier());
	}
}
