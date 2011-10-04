package dk.nsi.stamdata.cpr;

import com.google.common.collect.Sets;
import com.trifork.stamdata.models.cpr.Person;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.integrationtest.dgws.TestSTSMock;
import dk.nsi.stamdata.cpr.ws.PersonGenderCodeType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.sosi.seal.model.SystemIDCard;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PersonMapperFieldMappingForProtectedPersonTest {
	private static final String ADRESSEBESKYTTET = "ADRESSEBESKYTTET";
	private SystemIDCard idCard;
	private Person person;
	private PersonInformationStructureType output;

	@Before
	public void setUp() throws Exception {
		person = Factories.createPersonWithAddressProtection();

		Set<String> whiteList = Sets.newHashSet();
		output = new PersonMapper(whiteList, idCard).map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);

		idCard = TestSTSMock.createTestSTSSignedIDCard("12345678");
	}

	@Test
	public void mapsGaeldendeToCurrentPersonCivilRegistrationIdentifier() throws Exception {
		assertEquals(person.getGaeldendeCPR(), output.getCurrentPersonCivilRegistrationIdentifier());
	}

	@Test
	public void alwaysUses01ForPersonCivilRegistrationCode() {
		assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusCode(), is(new BigInteger("01")));
	}

	@Test
	public void setsCivilRegistrationStatusStartDateToStartOfEra() throws DatatypeConfigurationException {
		assertEquals(new Date(0), output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusStartDate().toGregorianCalendar().getTime());
	}

	@Test
	public void usesAddresseBeskyttetForPersonNameForAddressingName() throws DatatypeConfigurationException {
		assertEquals(ADRESSEBESKYTTET, output.getRegularCPRPerson().getPersonNameForAddressingName());
	}

	@Test
	public void omitsForStreetNameForAddressingName() throws DatatypeConfigurationException {
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetNameForAddressingName());
	}

	@Test
	public void usesAddresseBeskyttetForGivenName() throws Exception {
		assertEquals(ADRESSEBESKYTTET, output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName());
	}

	@Test
	public void omitsMiddleName() throws Exception {
		assertNull(output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonMiddleName());
	}

	@Test
	public void usesAddresseBeskyttetForSurname() throws Exception {
		assertEquals(ADRESSEBESKYTTET, output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonSurnameName());
	}

	@Test
	public void mapsCprToPersonCivilRegistrationIdentifier() throws Exception {
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
		assertEquals(person.getFoedselsdato(), output.getRegularCPRPerson().getPersonBirthDateStructure().getBirthDate().toGregorianCalendar().getTime());
	}

	@Test
	public void omitsCareOfName() throws Exception {
		assertNull(output.getPersonAddressStructure().getCareOfName());
	}

	@Test
	public void uses9999ForMunicipalityCode() throws Exception {
		assertEquals("9999", output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getMunicipalityCode());
	}

	@Test
	public void uses9999ForStreetCode() throws Exception {
		assertEquals("9999", output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetCode());
	}

	@Test
	public void uses999AForStreetBuildingIdentifierInAddressAccess() throws Exception {
		assertEquals("999A", output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetBuildingIdentifier());
	}

	@Test
	public void uses999AForStreetBuildingIdentifierInAddressPostal() throws Exception {
		assertEquals("999A", output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetBuildingIdentifier());
	}

	@Test
	public void omitsMailDeliverySublocationIdentifier() throws Exception {
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getMailDeliverySublocationIdentifier());
	}

	@Test
	public void usesAdressebeskyttetForStreetName() throws Exception {
		assertEquals(ADRESSEBESKYTTET, output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetName());
	}

	@Test
	public void omitsStreetnameForAddressingName() throws Exception {
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetNameForAddressingName());
	}

	@Test
	public void omitsFloorIdentifier() throws Exception {
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getFloorIdentifier());
	}

	@Test
	public void omitsSuiteIdentifier() throws Exception {
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getSuiteIdentifier());
	}

	@Test
	public void usesFalseForBirthdateUncertaintyIndicator() throws Exception {
		assertFalse(output.getRegularCPRPerson().getPersonBirthDateStructure().isBirthDateUncertaintyIndicator());
	}

	@Test
	public void uses9999ForPostCodeIdentifier() throws Exception {
		assertEquals("9999", output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getPostCodeIdentifier());
	}

	@Test
	public void usesAdressebeskyttetForDistrictName() throws Exception {
		assertEquals(ADRESSEBESKYTTET, output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getDistrictName());
	}

	@Test
	public void omitsPersonInformationProtectionStartDate() throws Exception {
		assertNull(output.getPersonAddressStructure().getPersonInformationProtectionStartDate());
	}

	@Test
	public void usesTrueForPersonInformationProtectionIndicator() throws Exception {
		assertTrue(output.getRegularCPRPerson().isPersonInformationProtectionIndicator());
	}

	@Test
	public void omitsDistrictSubdivisionIdentifier() throws Exception {
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getDistrictSubdivisionIdentifier());
	}

	@Test
	public void omitsPostOfficeBoxIdentifier() throws Exception {
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getPostOfficeBoxIdentifier());
	}

	@Test
	public void omitsCountryIdentificationCode() {
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getCountryIdentificationCode());
	}

	@Test
	public void omitsCountyCode() {
		assertNull(output.getPersonAddressStructure().getCountyCode());
	}

	private void doMap() throws Exception {
		output = new PersonMapper(Sets.<String>newHashSet(), idCard).map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);
	}
}
