package dk.nsi.stamdata.cpr;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.mapping.MunicipalityMapper;
import dk.nsi.stamdata.cpr.pvit.WhitelistProvider.Whitelist;
import dk.nsi.stamdata.cpr.ws.*;
import dk.sosi.seal.model.SystemIDCard;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;

@RequestScoped
public class PersonMapper
{
	public static enum ServiceProtectionLevel
	{
		AlwaysCensorProtectedData,
		CensorProtectedDataForNonAuthorities
	}
	
	private static final String ADRESSEBESKYTTET = "ADRESSEBESKYTTET";
	
	private final Set<String> whitelist;
	private final SystemIDCard idCard;

	private final MunicipalityMapper munucipalityMapper;

	@Inject
	PersonMapper(@Whitelist Set<String> whitelist, SystemIDCard idCard, MunicipalityMapper munucipalityMapper)
	{
		// Once we get this far the filter should have gotten rid of id cards that
		// are not CVR authenticated System ID Cards.
		
		this.whitelist = whitelist;
		this.idCard = idCard;
		this.munucipalityMapper = munucipalityMapper;
	}
	
	public PersonInformationStructureType map(Person person, ServiceProtectionLevel protectionLevel) throws DatatypeConfigurationException
	{
		boolean censorData = isPersonProtected(person) && (protectionLevel == ServiceProtectionLevel.AlwaysCensorProtectedData || !isClientAnAuthority());
		
		return (censorData) ? createOutputWithCensoredDate(person) : createOutputWithRealDate(person);
	}

	private PersonInformationStructureType createOutputWithCensoredDate(Person person) throws DatatypeConfigurationException
	{
		PersonInformationStructureType output = new ObjectFactory().createPersonInformationStructureType();
		mapCurrentPersonCivilRegistrationIdentifier(person, output);

		RegularCPRPersonType regularCprPerson = new RegularCPRPersonType();
		SimpleCPRPersonType simpleCprPerson = new SimpleCPRPersonType();

		// PERSON NAME STRUCTURE SECTION

		PersonNameStructureType personName = new PersonNameStructureType();
		simpleCprPerson.setPersonNameStructure(personName);

		personName.setPersonGivenName(ADRESSEBESKYTTET);
		personName.setPersonMiddleName(null);
		personName.setPersonSurnameName(ADRESSEBESKYTTET);

		simpleCprPerson.setPersonCivilRegistrationIdentifier(person.cpr);

		regularCprPerson.setSimpleCPRPerson(simpleCprPerson);

		regularCprPerson.setPersonNameForAddressingName(ADRESSEBESKYTTET);

		// It is safe to include the gender since it is encoded in the
		// CPR number.

		regularCprPerson.setPersonGenderCode(mapGenderToGenderCode(person.koen));

		regularCprPerson.setPersonInformationProtectionIndicator(true);

		// BIRTH DATE

		PersonBirthDateStructureType personBirthDate = new PersonBirthDateStructureType();

		personBirthDate.setBirthDate(newXMLGregorianCalendar(person.foedselsdato));
		personBirthDate.setBirthDateUncertaintyIndicator(false);

		regularCprPerson.setPersonBirthDateStructure(personBirthDate);

		// CIVIL STATUS

		PersonCivilRegistrationStatusStructureType personCivil = new PersonCivilRegistrationStatusStructureType();

		personCivil.setPersonCivilRegistrationStatusCode(BigInteger.ONE);
		personCivil.setPersonCivilRegistrationStatusStartDate(newXMLGregorianCalendar(new Date(0)));

		regularCprPerson.setPersonCivilRegistrationStatusStructure(personCivil);

		//
		// PERSON ADDRESS
		//

		PersonAddressStructureType personAddress = new PersonAddressStructureType();
		personAddress.setPersonInformationProtectionStartDate(null);

		personAddress.setCountyCode(null);
		personAddress.setCareOfName(null);

		AddressCompleteType addressComplete = new AddressCompleteType();

		AddressAccessType addressAccess = new AddressAccessType();

		addressAccess.setMunicipalityCode("9999");
		addressAccess.setStreetCode("9999");
		addressAccess.setStreetBuildingIdentifier("999A");

		addressComplete.setAddressAccess(addressAccess);

		AddressPostalType addressPostal = new AddressPostalType();
		addressComplete.setAddressPostal(addressPostal);

		addressPostal.setMailDeliverySublocationIdentifier(null);
		addressPostal.setStreetName(ADRESSEBESKYTTET);
		addressPostal.setStreetNameForAddressingName(null);

		addressPostal.setStreetBuildingIdentifier("999A");
		addressPostal.setFloorIdentifier(null);
		addressPostal.setSuiteIdentifier(null);
		addressPostal.setDistrictSubdivisionIdentifier(null);
		addressPostal.setPostOfficeBoxIdentifier(null);

		addressPostal.setPostCodeIdentifier("9999");
		addressPostal.setDistrictName(ADRESSEBESKYTTET);

		addressPostal.setCountryIdentificationCode(null);

		personAddress.setAddressComplete(addressComplete);
		output.setPersonAddressStructure(personAddress);

		output.setRegularCPRPerson(regularCprPerson);

		return output;
	}

	private PersonInformationStructureType createOutputWithRealDate(Person person) throws DatatypeConfigurationException
	{
		PersonInformationStructureType output = new ObjectFactory().createPersonInformationStructureType();

		mapCurrentPersonCivilRegistrationIdentifier(person, output);

		RegularCPRPersonType regularCprPerson = new RegularCPRPersonType();
		SimpleCPRPersonType simpleCprPerson = new SimpleCPRPersonType();

		// PERSON NAME STRUCTURE SECTION

		PersonNameStructureType personName = new PersonNameStructureType();
		simpleCprPerson.setPersonNameStructure(personName);

		personName.setPersonGivenName(person.fornavn);

		// Middle name is optional.

		if (!StringUtils.isBlank(person.mellemnavn))
		{
			personName.setPersonMiddleName(person.mellemnavn);
		}

		personName.setPersonSurnameName(person.efternavn);

		simpleCprPerson.setPersonCivilRegistrationIdentifier(person.cpr);

		regularCprPerson.setSimpleCPRPerson(simpleCprPerson);

		regularCprPerson.setPersonNameForAddressingName(person.getNavnTilAdressering());

		regularCprPerson.setPersonGenderCode(mapGenderToGenderCode(person.koen));

		regularCprPerson.setPersonInformationProtectionIndicator(false);

		// BIRTH DATE

		PersonBirthDateStructureType personBirthDate = new PersonBirthDateStructureType();

		personBirthDate.setBirthDate(newXMLGregorianCalendar(person.foedselsdato));
		personBirthDate.setBirthDateUncertaintyIndicator(person.getFoedselsdatoMarkering());

		regularCprPerson.setPersonBirthDateStructure(personBirthDate);

		// CIVIL STATUS

		PersonCivilRegistrationStatusStructureType personCivil = new PersonCivilRegistrationStatusStructureType();

		personCivil.setPersonCivilRegistrationStatusCode(new BigInteger(person.getStatus()));
		personCivil.setPersonCivilRegistrationStatusStartDate(newXMLGregorianCalendar(person.getStatusDato()));

		regularCprPerson.setPersonCivilRegistrationStatusStructure(personCivil);

		//
		// PERSON ADDRESS
		//

		PersonAddressStructureType personAddress = new PersonAddressStructureType();

		if (person.navnebeskyttelsestartdato != null)
		{
			personAddress.setPersonInformationProtectionStartDate(newXMLGregorianCalendar(person.navnebeskyttelsestartdato));
		}

		personAddress.setCountyCode(munucipalityMapper.toCountyCode(person.getKommuneKode()));

		if (StringUtils.isNotBlank(person.coNavn))
		{
			personAddress.setCareOfName(person.coNavn);
		}

		AddressCompleteType addressComplete = new AddressCompleteType();

		AddressAccessType addressAccess = new AddressAccessType();

		addressAccess.setMunicipalityCode(person.kommuneKode);
		addressAccess.setStreetCode(person.vejKode);
		addressAccess.setStreetBuildingIdentifier(getBuildingIdentifier(person));

		addressComplete.setAddressAccess(addressAccess);

		AddressPostalType addressPostal = new AddressPostalType();
		addressComplete.setAddressPostal(addressPostal);

		// The following field is not included in the source "CPR Registeret"
		// therefore we cannot fill the element.
		//
		// if (StringUtils.isNotBlank(""))
		// {
		//	 This is:
		//	 The given name of a farm, estate, building or dwelling, which is used as a additional postal address identifier.
		//
		//	addressPostal.setMailDeliverySublocationIdentifier("Fake Value"); // FIXME: The importer does not import this field.
		// }

		addressPostal.setStreetName(person.vejnavn);

		if (StringUtils.isNotBlank(person.getVejnavnTilAdressering()))
		{
			addressPostal.setStreetNameForAddressingName(person.getVejnavnTilAdressering());
		}

		addressPostal.setStreetBuildingIdentifier(getBuildingIdentifier(person));

		if (StringUtils.isNotBlank(person.getEtage()))
		{
			addressPostal.setFloorIdentifier(person.getEtage());
		}

		if (StringUtils.isNotBlank(person.sideDoerNummer))
		{
			addressPostal.setSuiteIdentifier(person.sideDoerNummer);
		}

		if (StringUtils.isNotBlank(person.lokalitet)) // TODO: We are not sure this is the correct field.
		{
			// Documentation says:
			//
			// Name of a village, city or subdivision of a city or district, which is determined as a part of the official address specification for a certain street
			// or specific parts of a street, defined by intervals of street building identifiers (da: house numbers).
			//
			// We believe that the CPR term for this is 'Lokalitet'.

			addressPostal.setDistrictSubdivisionIdentifier(person.lokalitet);
		}

		// Post Box is excluded since a persons address cannot be a Post Box.
		//
		// if (StringUtils.isNotBlank(""))
		// {
		//    addressPostal.setPostOfficeBoxIdentifier(-1); // FIXME: The importer does not import this field.
		// }

		addressPostal.setPostCodeIdentifier(person.postnummer);
		addressPostal.setDistrictName(person.postdistrikt);

		// FIXME: The importer does not import this field.
		// Maybe we cannot

		if (StringUtils.isNotBlank(""))
		{
			CountryIdentificationCodeType country = new CountryIdentificationCodeType();

			// Two alpha-numerical characters.
			country.setScheme(CountryIdentificationSchemeType.ISO_3166_ALPHA_2);
			country.setValue("DK");
			addressPostal.setCountryIdentificationCode(country);
		}

		personAddress.setAddressComplete(addressComplete);
		output.setPersonAddressStructure(personAddress);

		output.setRegularCPRPerson(regularCprPerson);

		return output;
	}

	private void mapCurrentPersonCivilRegistrationIdentifier(Person person, PersonInformationStructureType output) {
		if (StringUtils.isNotBlank(person.getGaeldendeCPR())) {
			output.setCurrentPersonCivilRegistrationIdentifier(person.getGaeldendeCPR());
		}
	}

	private String getBuildingIdentifier(Person person) {
		return person.husnummer + person.bygningsnummer;
	}

	private boolean isClientAnAuthority()
	{
		String careProviderType = idCard.getSystemInfo().getCareProvider().getType();
		
		Preconditions.checkState(CVR_NUMBER.equals(careProviderType), "ID Card Care provider is not a CVR. This is a programming error.");
		
		String clientCVR = idCard.getSystemInfo().getCareProvider().getID();
		
		return whitelist.contains(clientCVR);
	}
	
	private boolean isPersonProtected(Person person)
	{
		if (person.getNavnebeskyttelsestartdato() == null) return false;
		
		// We have to make the guard above to avoid null being passed into the Instant
		// it is converted to the beginning of the era.
		
		Preconditions.checkState(person.getNavnebeskyttelseslettedato() != null, "The protection end date was not present. This is most unexpected and a programming error.");
		
		Instant protectionStart = new Instant(person.getNavnebeskyttelsestartdato());
		Instant protectionEnd = new Instant(person.getNavnebeskyttelseslettedato());
		
		return protectionStart.isEqualNow() || (protectionStart.isBeforeNow() && protectionEnd.isAfterNow());
	}

	private PersonGenderCodeType mapGenderToGenderCode(String genderString)
	{
		if ("M".equalsIgnoreCase(genderString))
		{
			return PersonGenderCodeType.MALE;
		}
		else if ("K".equalsIgnoreCase(genderString))
		{
			return PersonGenderCodeType.FEMALE;
		}
		else
		{
			return PersonGenderCodeType.UNKNOWN;
		}
	}

	public static XMLGregorianCalendar newXMLGregorianCalendar(Date date) throws DatatypeConfigurationException
	{
		DatatypeFactory factory = DatatypeFactory.newInstance();
		return factory.newXMLGregorianCalendar(new DateTime(date).toGregorianCalendar());
	}
}
