package dk.nsi.stamdata.cpr;

import com.google.common.collect.Sets;
import com.trifork.stamdata.models.cpr.Person;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.integrationtest.dgws.TestSTSMock;
import dk.nsi.stamdata.cpr.ws.PersonGenderCodeType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.sosi.seal.model.SystemIDCard;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigInteger;
import java.util.Set;

import static dk.nsi.stamdata.cpr.Factories.YESTERDAY;
import static dk.nsi.stamdata.cpr.PersonMapper.newXMLGregorianCalendar;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PersonMapperFieldMappingTest {
	private SystemIDCard idCard;
	private Person person;
	private PersonInformationStructureType output;

	@Before
	public void setUp() throws Exception {
		person = Factories.createPersonWithoutAddressProtection();

		Set<String> whiteList = Sets.newHashSet();
		output = new PersonMapper(whiteList, idCard).map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);

		idCard = TestSTSMock.createTestSTSSignedIDCard("12345678");
	}

	@Test
	public void mapsGaeldendeToCprCurrentPersonCivilRegistrationIdentifier() throws Exception {
		doMap();
		assertEquals(person.getGaeldendeCPR(), output.getCurrentPersonCivilRegistrationIdentifier());
	}

	@Test
	public void mapCivilRegistrationCode() {
		assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusCode(), is(new BigInteger("02")));
	}

	@Test
	public void mapCivilRegistrationCodeStartDate() throws DatatypeConfigurationException {
		assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusStartDate(), is(newXMLGregorianCalendar(YESTERDAY)));
	}

	@Test
	public void mapNameForAddressingName() throws DatatypeConfigurationException {
		assertThat(output.getRegularCPRPerson().getPersonNameForAddressingName(), is("Peter,Andersen"));
	}

	@Test
	public void mapStreetNameForAddressingName() throws DatatypeConfigurationException {
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

	@Test
	@Ignore("Is anything mapped to Maildelivery Subdivision identifier? Lokalitet is not, it's mapped to DistrictSubdivisionIdentifier")
	public void mapsLokalitetToMailDeliverySublocationIdentifier() throws Exception {
		doMap();

		/*
				 From the xsd's:
				 DistrictSubdivisionIdentifier:
				 Name of a village, city or subdivision of a city or district, which is determined as a part of the
				 official address specification for a certain street or specific parts of a street, defined by intervals
				 of street building identifiers (da: house numbers).
				 </documentation>

				 MailDeliverySublocationIdentifier:
				The given name of a farm, estate, building or dwelling, which is used as a additional postal address
				identifier.
				 */

		assertEquals(person.getLokalitet(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getMailDeliverySublocationIdentifier());

	}

	@Test
	public void mapsVejnavnTo() throws Exception {
		doMap();

		assertEquals(person.getVejnavn(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetName());
	}

	@Test
	public void mapsVejnavnTilAddresseringToStreetnameForAddressingName() throws Exception {
		doMap();

		assertEquals(person.getVejnavnTilAdressering(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetNameForAddressingName());
	}

	@Test
	public void mapsEtageToFloorIdentifier() throws Exception {
		doMap();

		assertEquals(person.getEtage(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getFloorIdentifier());
	}

	@Test
	public void mapsSideDoerNummerToSuiteIdentifier() throws Exception {
		doMap();

		assertEquals(person.getSideDoerNummer(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getSuiteIdentifier());
	}

	@Test
	public void mapsFoedselsdatoMarkeringToBirthdateUncertaintyIndicator() throws Exception {
		doMap();

		assertEquals(person.getFoedselsdatoMarkering(), output.getRegularCPRPerson().getPersonBirthDateStructure().isBirthDateUncertaintyIndicator());
	}

	@Test
	public void mapsStatusToPersonCivilRegistrationStatusCode() throws Exception {
		doMap();

		assertEquals(new BigInteger(person.getStatus()), output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusCode());
	}

	@Test
	public void mapsStatusDatoToPersonCivilRegistrationStatusStartDate() throws Exception {
		doMap();

		assertEquals(person.getStatusDato(), output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusStartDate().toGregorianCalendar().getTime());
	}

	@Test
	public void mapsPostnummerToPostCodeIdentifier() throws Exception {
		doMap();

		assertEquals(person.getPostnummer(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getPostCodeIdentifier());
	}

	@Test
	public void mapsPostdistriktToDistrictName() throws Exception {
		doMap();

		assertEquals(person.getPostdistrikt(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getDistrictName());
	}

	@Test
	@Ignore("shouldn't this be so?")
	public void mapsNavnebeskyttelsestartdatoToPersonInformationProtectionStartDate() throws Exception {
		doMap();

		assertEquals(person.getNavnebeskyttelsestartdato(), output.getPersonAddressStructure().getPersonInformationProtectionStartDate().toGregorianCalendar().getTime());
	}

	@Test
	public void setsPersonInformationProtectionIndicatorToFalseWhenNavnebeskyttelsesstartdatoIsNull() throws Exception {
		doMap();

		assertFalse(output.getRegularCPRPerson().isPersonInformationProtectionIndicator());
	}

	@Test
	public void setsPersonInformationProtectionIndicatorToFalseWhenNavnebeskyttelsesstartdatoIsInTheFuture() throws Exception {
		person.setNavnebeskyttelsestartdato(DateTime.now().plusDays(2).toDate());
		doMap();

		assertFalse(output.getRegularCPRPerson().isPersonInformationProtectionIndicator());
	}

	@Test
	public void mapsLokalitetToDistrictSubdivisionIdentifier() {
		assertEquals(person.lokalitet, output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getDistrictSubdivisionIdentifier());
	}

	@Test
	public void nothingIsMappedToPostOfficeBoxIdentifier() {
		// Just to document the fact
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getPostOfficeBoxIdentifier());
	}

	@Test
	public void nothingIsMappedToCountryIdentificationCode() {
		// Just to document the fact
		assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getCountryIdentificationCode());
	}

	@Test
	@Ignore("Lige nu er værdien af CountyCode 'Fake value', det kan ikke være godt!")
	public void nothingIsMappedToCountyCode() {
		// Just to document the fact
		System.out.println(output.getPersonAddressStructure().getCountyCode());
		assertNull(output.getPersonAddressStructure().getCountyCode());
	}

	private void doMap() throws Exception {
		output = new PersonMapper(Sets.<String>newHashSet(), idCard).map(person, ServiceProtectionLevel.AlwaysCensorProtectedData);
	}
}
