package dk.nsi.stamdata.cpr;

import com.google.common.collect.Sets;
import com.trifork.stamdata.models.cpr.Person;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.integrationtest.dgws.TestSTSMock;
import dk.nsi.stamdata.cpr.ws.PersonGenderCodeType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.sosi.seal.model.SystemIDCard;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class PersonMapperFieldMappingTest
{
	private SystemIDCard idCard;


	Person person;
	PersonInformationStructureType output;
	
	@Before
	public void setUp() throws Exception
	{
		person = createPerson();
		
		idCard = TestSTSMock.createTestSTSSignedIDCard("12345678");
	}

	@Test
	public void mapsGaeldendeToCprCurrentPersonCivilRegistrationIdentifier() throws Exception {
		doMap();
		assertEquals(person.getGaeldendeCPR(), output.getCurrentPersonCivilRegistrationIdentifier());
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
	@Ignore("birth date uncertainty is neither modelled or imported")
	public void mapsSomethingToBirthdateUncertainty() {
		throw new UnsupportedOperationException("birth date uncertainty is neither modelled or imported");
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

	private Person createPerson() {
		Person newPerson = new Person();

		newPerson.setGaeldendeCPR("2345678901");

		newPerson.setFornavn("Peter");
		newPerson.setMellemnavn("Sigurd");
		newPerson.setEfternavn("Andersen");

		newPerson.setCpr("1234567890");

		newPerson.setKoen("M");

		newPerson.setFoedselsdato(new Date());

		newPerson.setCoNavn("Søren Petersen");

		newPerson.setKommuneKode("123");
		newPerson.setVejKode("234");
		newPerson.setHusnummer("10");
		newPerson.setBygningsnummer("A");
		newPerson.setLokalitet("Birkely");
		newPerson.setVejnavn("Ørstedgade");
		newPerson.setEtage("12");
		newPerson.setSideDoerNummer("tv");

		newPerson.setPostnummer("6666");
		newPerson.setPostdistrikt("Überwald");

		newPerson.setNavnebeskyttelsestartdato(null);
		newPerson.setNavnebeskyttelseslettedato(null);

		return newPerson;
	}

	private void doMap() throws Exception {
		output = new PersonMapper(Sets.<String>newHashSet(), idCard).map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);
	}
}
