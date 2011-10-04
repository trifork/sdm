package dk.nsi.stamdata.cpr;

import static dk.nsi.stamdata.cpr.Factories.YESTERDAY;
import static dk.nsi.stamdata.cpr.PersonMapper.newXMLGregorianCalendar;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.integrationtest.dgws.TestSTSMock;
import dk.nsi.stamdata.cpr.mapping.MunicipalityMapper;
import dk.nsi.stamdata.cpr.ws.PersonGenderCodeType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.sosi.seal.model.SystemIDCard;

public class PersonMapperFieldMappingTest
{
	private SystemIDCard idCard;
	private Person person;
	private PersonInformationStructureType output;
	
	@Before
	public void setUp() throws Exception
	{
		person = Factories.createPerson();		
		idCard = TestSTSMock.createTestSTSSignedIDCard("12345678");
	}

	@Test
	public void mapsGaeldendeToCprCurrentPersonCivilRegistrationIdentifier() throws Exception {
		doMap();
		assertEquals(person.getGaeldendeCPR(), output.getCurrentPersonCivilRegistrationIdentifier());
	}
	
	@Test
	public void mapCivilRegistrationCode() throws Exception
	{
		doMap();
		assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusCode(), is(new BigInteger("02")));
	}
	
	@Test
	public void mapCivilRegistrationCodeStartDate() throws Exception
	{
		doMap();
		assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusStartDate(), is(newXMLGregorianCalendar(YESTERDAY)));
	}
	
	@Test
	public void mapNameForAddressingName() throws Exception
	{
		doMap();
		assertThat(output.getRegularCPRPerson().getPersonNameForAddressingName(), is("Peter,Andersen"));
	}
	
	@Test
	public void mapStreetNameForAddressingName() throws Exception
	{
		doMap();
		assertThat(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetNameForAddressingName(), is("Østergd."));
	}

	@Test
	public void mapsFornavnToGivenName() throws Exception {
		doMap();
		assertEquals(person.getFornavn(), output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName());
	}

	@Test
	public void mapsMellemnavnToMiddleName() throws Exception {
		doMap();
		assertEquals(person.getMellemnavn(), output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonMiddleName());
	}

	@Test
	public void mapsEfternavnToSurname() throws Exception {
		doMap();
		assertEquals(person.getEfternavn(), output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonSurnameName());
	}

	@Test
	public void mapsCprToPersonCivilRegistrationIdentifier() throws Exception {
		doMap();
		assertEquals(person.getCpr(), output.getRegularCPRPerson().getSimpleCPRPerson().getPersonCivilRegistrationIdentifier());
	}

	@Test
	public void mapsKoenMToGenderCodeMale() throws Exception {
		person.setKoen("M");
		doMap();

		assertEquals(PersonGenderCodeType.MALE, output.getRegularCPRPerson().getPersonGenderCode());
	}

	@Test
	public void mapsKoenKToGenderCodeFemale() throws Exception {
		person.setKoen("K");
		doMap();

		assertEquals(PersonGenderCodeType.FEMALE, output.getRegularCPRPerson().getPersonGenderCode());
	}


	@Test
	public void mapsKoenEmptystringToGenderCodeUnknown() throws Exception {
		person.setKoen("");
		doMap();

		assertEquals(PersonGenderCodeType.UNKNOWN, output.getRegularCPRPerson().getPersonGenderCode());
	}

	@Test
	public void mapsKoenSinglespaceToGenderCodeUnknown() throws Exception {
		person.setKoen(" ");
		doMap();
		
		assertEquals(PersonGenderCodeType.UNKNOWN, output.getRegularCPRPerson().getPersonGenderCode());
	}

	@Test
	public void mapsKoenSomeSingleletterCodeToGenderCodeUnknown() throws Exception {
		person.setKoen("X");
		doMap();

		assertEquals(PersonGenderCodeType.UNKNOWN, output.getRegularCPRPerson().getPersonGenderCode());
	}
	
	@Test
	public void mapsFoedselsdatoToBirthdate() throws Exception {
		doMap();

		assertEquals(person.getFoedselsdato(), output.getRegularCPRPerson().getPersonBirthDateStructure().getBirthDate().toGregorianCalendar().getTime());
	}

	@Test
	public void mapsCoNavnToCareOfName() throws Exception {
		doMap();
		
		assertEquals(person.getCoNavn(), output.getPersonAddressStructure().getCareOfName());
	}

	@Test
	public void mapsKommuneKodeToMunicipalityCode() throws Exception {
		doMap();

		assertEquals(person.getKommuneKode(), output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getMunicipalityCode());
	}

	@Test
	public void mapsVejkodeToStreetCode() throws Exception {
		doMap();

		assertEquals(person.getVejKode(), output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetCode());
	}

	@Test
	public void mapsHusnummerAndBygningsnummerToStreetBuildingIdentifierInAddressAccess() throws Exception {
		doMap();

		assertEquals(person.getHusnummer() + person.getBygningsnummer(), output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetBuildingIdentifier());
	}

	@Test
	public void mapsHusnummerAndBygningsnummerToStreetBuildingIdentifierInAddressPostal() throws Exception {
		doMap();

		assertEquals(person.getHusnummer() + person.getBygningsnummer(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetBuildingIdentifier());
	}

	private void doMap() throws Exception {
		
		Set<String> whiteList = Sets.newHashSet();
		MunicipalityMapper municipalityMapper = new MunicipalityMapper();
		PersonMapper personMapper = new PersonMapper(whiteList, idCard, municipalityMapper);
		
		output = personMapper.map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);
	}
}
