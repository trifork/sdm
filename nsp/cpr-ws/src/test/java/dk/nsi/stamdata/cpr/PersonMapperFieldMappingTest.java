package dk.nsi.stamdata.cpr;

import static dk.nsi.stamdata.cpr.Factories.YESTERDAY;
import static dk.nsi.stamdata.cpr.PersonMapper.newXMLGregorianCalendar;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.Set;

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
	Person person;
	PersonInformationStructureType output;
	
	@Before
	public void setUp() throws DatatypeConfigurationException
	{
		person = Factories.createPerson();
		
		SystemIDCard idCard = TestSTSMock.createTestSTSSignedIDCard("12345678");
		Set<String> whiteList = Sets.newHashSet();
		output = new PersonMapper(whiteList, idCard).map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);
	}
	
	@Test
	public void mapCurrentPersonCivilRegistrationIdentifier()
	{
		assertEquals(person.getGaeldendeCPR(), output.getCurrentPersonCivilRegistrationIdentifier());
	}
	
	@Test
	public void mapCivilRegistrationCode()
	{
		assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusCode(), is(new BigInteger("02")));
	}
	
	@Test
	public void mapCivilRegistrationCodeStartDate() throws DatatypeConfigurationException
	{
		assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusStartDate(), is(newXMLGregorianCalendar(YESTERDAY)));
	}
	
	@Test
	public void mapNameForAddressingName() throws DatatypeConfigurationException
	{
		assertThat(output.getRegularCPRPerson().getPersonNameForAddressingName(), is("Peter,Andersen"));
	}
	
	@Test
	public void mapStreetNameForAddressingName() throws DatatypeConfigurationException
	{
		assertThat(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetNameForAddressingName(), is("Østergd."));
	}
}
