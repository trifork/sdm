package dk.nsi.stamdata.cpr;

import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;

import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.pvit.WhitelistProvider.Whitelist;
import dk.nsi.stamdata.cpr.ws.AddressAccessType;
import dk.nsi.stamdata.cpr.ws.AddressCompleteType;
import dk.nsi.stamdata.cpr.ws.AddressPostalType;
import dk.nsi.stamdata.cpr.ws.CountryIdentificationCodeType;
import dk.nsi.stamdata.cpr.ws.CountryIdentificationSchemeType;
import dk.nsi.stamdata.cpr.ws.ObjectFactory;
import dk.nsi.stamdata.cpr.ws.PersonAddressStructureType;
import dk.nsi.stamdata.cpr.ws.PersonBirthDateStructureType;
import dk.nsi.stamdata.cpr.ws.PersonCivilRegistrationStatusStructureType;
import dk.nsi.stamdata.cpr.ws.PersonGenderCodeType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonNameStructureType;
import dk.nsi.stamdata.cpr.ws.RegularCPRPersonType;
import dk.nsi.stamdata.cpr.ws.SimpleCPRPersonType;
import dk.sosi.seal.model.SystemIDCard;

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

	@Inject
	PersonMapper(@Whitelist Set<String> whitelist, SystemIDCard idCard)
	{
		// Once we get this far the filter should have gotten rid of id cards that
		// are not CVR authenticated System ID Cards.
		
		this.whitelist = whitelist;
		this.idCard = idCard;
	}
	
	public PersonInformationStructureType map(Person person, ServiceProtectionLevel protectionLevel) throws DatatypeConfigurationException
	{
		boolean censorData = isPersonProtected(person) && (protectionLevel == ServiceProtectionLevel.AlwaysCensorProtectedData || !isClientAnAuthority());
		
		return (censorData) ? createOutputWithCensoredDate(person) : createOutputWithRealDate(person);
	}

	private PersonInformationStructureType createOutputWithCensoredDate(Person person) throws DatatypeConfigurationException
	{
		PersonInformationStructureType output = new ObjectFactory().createPersonInformationStructureType();
		output.setCurrentPersonCivilRegistrationIdentifier(person.cpr);

		RegularCPRPersonType regularCprPerson = new RegularCPRPersonType();
		SimpleCPRPersonType simpleCprPerson = new SimpleCPRPersonType();

		// PERSON NAME STRUCTURE SECTION

		PersonNameStructureType personName = new PersonNameStructureType();
		simpleCprPerson.setPersonNameStructure(personName);

		personName.setPersonGivenName(ADRESSEBESKYTTET);
		personName.setPersonMiddleName(ADRESSEBESKYTTET);
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
		personCivil.setPersonCivilRegistrationStatusStartDate(newXMLGregorianCalendar(new Date()));

		regularCprPerson.setPersonCivilRegistrationStatusStructure(personCivil);

		//
		// PERSON ADDRESS
		//

		PersonAddressStructureType personAddress = new PersonAddressStructureType();
		personAddress.setPersonInformationProtectionStartDate(newXMLGregorianCalendar(person.navnebeskyttelsestartdato));

		personAddress.setCountyCode("9999");
		personAddress.setCareOfName(ADRESSEBESKYTTET);

		AddressCompleteType addressComplete = new AddressCompleteType();

		AddressAccessType addressAccess = new AddressAccessType();

		addressAccess.setMunicipalityCode("9999");
		addressAccess.setStreetCode("999");
		addressAccess.setStreetBuildingIdentifier("999A");

		addressComplete.setAddressAccess(addressAccess);

		AddressPostalType addressPostal = new AddressPostalType();
		addressComplete.setAddressPostal(addressPostal);

		addressPostal.setMailDeliverySublocationIdentifier(ADRESSEBESKYTTET);
		addressPostal.setStreetName(ADRESSEBESKYTTET);
		addressPostal.setStreetNameForAddressingName(ADRESSEBESKYTTET);

		addressPostal.setStreetBuildingIdentifier("99");
		addressPostal.setFloorIdentifier("99");
		addressPostal.setSuiteIdentifier("99");
		addressPostal.setDistrictSubdivisionIdentifier("9999");
		addressPostal.setPostOfficeBoxIdentifier(0);

		addressPostal.setPostCodeIdentifier("9999");
		addressPostal.setDistrictName(ADRESSEBESKYTTET);

		CountryIdentificationCodeType country = new CountryIdentificationCodeType();
		country.setScheme(CountryIdentificationSchemeType.ISO_3166_ALPHA_2); // Two alpha-numerical characters.
		country.setValue("XX");
		addressPostal.setCountryIdentificationCode(country);

		personAddress.setAddressComplete(addressComplete);
		output.setPersonAddressStructure(personAddress);

		output.setRegularCPRPerson(regularCprPerson);

		return output;
	}

	private PersonInformationStructureType createOutputWithRealDate(Person person) throws DatatypeConfigurationException
	{
		PersonInformationStructureType output = new ObjectFactory().createPersonInformationStructureType();
		
		if (StringUtils.isNotBlank(person.getGaeldendeCPR())) {
			output.setCurrentPersonCivilRegistrationIdentifier(person.getGaeldendeCPR());
		}

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

		regularCprPerson.setPersonNameForAddressingName("Fake Value"); // FIXME: Is this correct. This might be missing for the importer.

		regularCprPerson.setPersonGenderCode(mapGenderToGenderCode(person.koen));

		regularCprPerson.setPersonInformationProtectionIndicator(false);

		// BIRTH DATE

		PersonBirthDateStructureType personBirthDate = new PersonBirthDateStructureType();

		personBirthDate.setBirthDate(newXMLGregorianCalendar(person.foedselsdato));
		personBirthDate.setBirthDateUncertaintyIndicator(false); // FIXME: This is not stored by the importer. Requires updated sql schema and importer update.

		regularCprPerson.setPersonBirthDateStructure(personBirthDate);

		// CIVIL STATUS

		PersonCivilRegistrationStatusStructureType personCivil = new PersonCivilRegistrationStatusStructureType();

		personCivil.setPersonCivilRegistrationStatusCode(BigInteger.ONE); // FIXME: This information comes from another CPR Posttype and needs to get a new SQL table and extention to the importer.
		personCivil.setPersonCivilRegistrationStatusStartDate(newXMLGregorianCalendar(new Date())); // FIXME: This is fake data.

		regularCprPerson.setPersonCivilRegistrationStatusStructure(personCivil);

		//
		// PERSON ADDRESS
		//

		PersonAddressStructureType personAddress = new PersonAddressStructureType();

		if (person.navnebeskyttelsestartdato != null)
		{
			personAddress.setPersonInformationProtectionStartDate(newXMLGregorianCalendar(person.navnebeskyttelsestartdato));
		}

		personAddress.setCountyCode("Fake Value"); // FIXME: We don't import this value. Amt or Region.

		if (StringUtils.isNotBlank(person.coNavn))
		{
			personAddress.setCareOfName(person.coNavn);
		}

		AddressCompleteType addressComplete = new AddressCompleteType();

		AddressAccessType addressAccess = new AddressAccessType();

		addressAccess.setMunicipalityCode(person.kommuneKode);
		addressAccess.setStreetCode(person.vejKode);
		addressAccess.setStreetBuildingIdentifier(person.bygningsnummer);

		addressComplete.setAddressAccess(addressAccess);

		AddressPostalType addressPostal = new AddressPostalType();
		addressComplete.setAddressPostal(addressPostal);

		if (StringUtils.isNotBlank(""))
		{
			addressPostal.setMailDeliverySublocationIdentifier("Fake Value"); // FIXME: The importer does not import this field.
		}

		addressPostal.setStreetName(person.vejnavn);

		if (StringUtils.isNotBlank(""))
		{
			addressPostal.setStreetNameForAddressingName("Fake Value"); // FIXME: The importer does not import this field.
		}

		addressPostal.setStreetBuildingIdentifier(person.bygningsnummer);

		if (StringUtils.isNotBlank(""))
		{
			addressPostal.setFloorIdentifier("Fake Value"); // FIXME: The importer does not import this field.
		}

		if (StringUtils.isNotBlank(person.sideDoerNummer))
		{
			addressPostal.setSuiteIdentifier(person.sideDoerNummer);
		}

		if (StringUtils.isNotBlank(person.lokalitet)) // TODO: We are not sure this is the correct field.
		{
			addressPostal.setDistrictSubdivisionIdentifier(person.lokalitet);
		}

		if (StringUtils.isNotBlank(""))
		{
			addressPostal.setPostOfficeBoxIdentifier(-1); // FIXME: The importer does not import this field.
		}

		addressPostal.setPostCodeIdentifier(person.postnummer);
		addressPostal.setDistrictName(person.postdistrikt);

		if (StringUtils.isNotBlank("")) // FIXME: The importer does not import this field.
		{
			CountryIdentificationCodeType country = new CountryIdentificationCodeType();
			country.setScheme(CountryIdentificationSchemeType.ISO_3166_ALPHA_2); // Two alpha-numerical characters.
			country.setValue("DK");
			addressPostal.setCountryIdentificationCode(country); // FIXME: The importer does not import this field.
		}

		personAddress.setAddressComplete(addressComplete);
		output.setPersonAddressStructure(personAddress);

		output.setRegularCPRPerson(regularCprPerson);

		return output;
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
