/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
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
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.persistence.Record;

import dk.nsi.stamdata.cpr.mapping.CivilRegistrationStatusCodes;
import dk.nsi.stamdata.cpr.mapping.MunicipalityMapper;
import dk.nsi.stamdata.cpr.mapping.SikredeRecordToPersonPublicHealhInsuranceMapper;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.models.SikredeYderRelation;
import dk.nsi.stamdata.cpr.models.Yderregister;
import dk.nsi.stamdata.cpr.pvit.WhitelistProvider.Whitelist;
import dk.nsi.stamdata.jaxws.generated.AddressAccessType;
import dk.nsi.stamdata.jaxws.generated.AddressCompleteType;
import dk.nsi.stamdata.jaxws.generated.AddressPostalType;
import dk.nsi.stamdata.jaxws.generated.AssociatedGeneralPractitionerStructureType;
import dk.nsi.stamdata.jaxws.generated.CountryIdentificationCodeType;
import dk.nsi.stamdata.jaxws.generated.CountryIdentificationSchemeType;
import dk.nsi.stamdata.jaxws.generated.ObjectFactory;
import dk.nsi.stamdata.jaxws.generated.PersonAddressStructureType;
import dk.nsi.stamdata.jaxws.generated.PersonBirthDateStructureType;
import dk.nsi.stamdata.jaxws.generated.PersonCivilRegistrationStatusStructureType;
import dk.nsi.stamdata.jaxws.generated.PersonGenderCodeType;
import dk.nsi.stamdata.jaxws.generated.PersonHealthCareInformationStructureType;
import dk.nsi.stamdata.jaxws.generated.PersonInformationStructureType;
import dk.nsi.stamdata.jaxws.generated.PersonNameStructureType;
import dk.nsi.stamdata.jaxws.generated.PersonPublicHealthInsuranceType;
import dk.nsi.stamdata.jaxws.generated.PersonWithHealthCareInformationStructureType;
import dk.nsi.stamdata.jaxws.generated.PublicHealthInsuranceGroupIdentifierType;
import dk.nsi.stamdata.jaxws.generated.RegularCPRPersonType;
import dk.nsi.stamdata.jaxws.generated.SimpleCPRPersonType;
import dk.sosi.seal.model.SystemIDCard;


@RequestScoped
public class PersonMapper
{
	private static final String UKENDT = "UKENDT";

	private static final int AUTHORITY_CODE_LENGTH = 4;


	public static enum ServiceProtectionLevel
	{
		AlwaysCensorProtectedData,
		CensorProtectedDataForNonAuthorities
	}


	public static enum CPRProtectionLevel
	{
		CensorCPR,
		DoNotCensorCPR
	}


	private static final String ADRESSEBESKYTTET = "ADRESSEBESKYTTET";
	private static final String PROTECTED_CPR = "0000000000";

	private final Set<String> whitelist;
	private final SystemIDCard idCard;

	private final MunicipalityMapper munucipalityMapper;


	@Inject
	PersonMapper(@Whitelist Set<String> whitelist, SystemIDCard idCard, MunicipalityMapper munucipalityMapper)
	{
		// Once we get this far the filter should have gotten rid of id cards
		// that are not CVR authenticated System ID Cards.

		this.whitelist = whitelist;
		this.idCard = idCard;
		this.munucipalityMapper = munucipalityMapper;
	}


	public PersonInformationStructureType map(Person person, ServiceProtectionLevel protectionLevel, CPRProtectionLevel cprProtection)
	{
		boolean censorData = isPersonProtected(person) && (protectionLevel == ServiceProtectionLevel.AlwaysCensorProtectedData || !isClientAnAuthority());

		return (censorData) ? createOutputWithCensoredDate(person, cprProtection) : createOutputWithRealDate(person);
	}


	private PersonInformationStructureType createOutputWithCensoredDate(Person person, CPRProtectionLevel cprProtection)
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

		// For some searches we also need to censor the CPR number.

		boolean isCPRHidden = cprProtection == CPRProtectionLevel.CensorCPR;
		simpleCprPerson.setPersonCivilRegistrationIdentifier(isCPRHidden ? PROTECTED_CPR : person.getCpr());

		regularCprPerson.setSimpleCPRPerson(simpleCprPerson);

		regularCprPerson.setPersonNameForAddressingName(ADRESSEBESKYTTET);

		// Even though the CPR can be read from the CPR (when the CPR is
		// included),
		// we might as well always protect it.

		regularCprPerson.setPersonGenderCode(PersonGenderCodeType.UNKNOWN);

		regularCprPerson.setPersonInformationProtectionIndicator(true);

		// BIRTH DATE

		PersonBirthDateStructureType personBirthDate = new PersonBirthDateStructureType();

		personBirthDate.setBirthDate(newXMLGregorianCalendar(new Date(0)));
		personBirthDate.setBirthDateUncertaintyIndicator(true);

		regularCprPerson.setPersonBirthDateStructure(personBirthDate);

		// CIVIL STATUS

		PersonCivilRegistrationStatusStructureType personCivil = new PersonCivilRegistrationStatusStructureType();

		personCivil.setPersonCivilRegistrationStatusCode(BigInteger.ONE);
		personCivil.setPersonCivilRegistrationStatusStartDate(newXMLGregorianCalendar(new Date(0)));

		regularCprPerson.setPersonCivilRegistrationStatusStructure(personCivil);

		// PERSON ADDRESS

		output.setPersonAddressStructure(createFakePersonAddressStructure(ADRESSEBESKYTTET));
		output.getPersonAddressStructure().setPersonInformationProtectionStartDate(newXMLGregorianCalendar(person.getNavnebeskyttelsestartdato()));

		output.setRegularCPRPerson(regularCprPerson);

		return output;
	}


	private PersonInformationStructureType createOutputWithRealDate(Person person)
	{
		// There are many cases in which we cannot fulfill the output format
		// just by using data present in the db. Therefore we have to 'fill'
		// in the gaps with e.g. 'UNKNOWN'.

		PersonInformationStructureType output = new ObjectFactory().createPersonInformationStructureType();

		mapCurrentPersonCivilRegistrationIdentifier(person, output);

		RegularCPRPersonType regularCprPerson = new RegularCPRPersonType();
		SimpleCPRPersonType simpleCprPerson = new SimpleCPRPersonType();
		regularCprPerson.setSimpleCPRPerson(simpleCprPerson);

		// PERSON NAME STRUCTURE SECTION
		//
		// Many of these values might not be present in the database
		// this is because that 'Such is life' and sometimes we simply
		// don't know a person's first name, address or birthday.

		PersonNameStructureType personName = new PersonNameStructureType();
		simpleCprPerson.setPersonNameStructure(personName);

		personName.setPersonGivenName(actualOrUnknown(person.fornavn));

		// Middle name is optional.

		personName.setPersonMiddleName(actualOrNull(person.mellemnavn));

		personName.setPersonSurnameName(actualOrUnknown(person.efternavn));

		simpleCprPerson.setPersonCivilRegistrationIdentifier(person.cpr);

		regularCprPerson.setPersonNameForAddressingName(actualOrUnknown(person.getNavnTilAdressering()));

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

		// PERSON ADDRESS
		//
		// There are also records for which the record's values should not be
		// used. For instance if a person is dead or missing his address should
		// not be filled in. Yet the output schema requires them to be there.

		boolean shouldIncludeAddress = !CivilRegistrationStatusCodes.STATUSES_WITH_NO_ADDRESS.contains(person.getStatus());

		if (shouldIncludeAddress)
		{
			PersonAddressStructureType personAddress = new PersonAddressStructureType();

			if (person.navnebeskyttelsestartdato != null)
			{
				personAddress.setPersonInformationProtectionStartDate(newXMLGregorianCalendar(person.navnebeskyttelsestartdato));
			}

			// The output requires that the authority codes (municipality and
			// county) are exactly four char long.

			AddressCompleteType addressComplete = new AddressCompleteType();
			AddressAccessType addressAccess = new AddressAccessType();
			addressComplete.setAddressAccess(addressAccess);

			if (StringUtils.isNotBlank(person.kommuneKode))
			{
				String municipalityCode = StringUtils.leftPad(person.kommuneKode, AUTHORITY_CODE_LENGTH, "0");

				addressAccess.setMunicipalityCode(municipalityCode);
				personAddress.setCountyCode(munucipalityMapper.toCountyCode(municipalityCode));
			}
			else
			{
				addressAccess.setMunicipalityCode("0000");
				personAddress.setCountyCode("0000");
			}

			personAddress.setCareOfName(actualOrNull(person.coNavn));

			addressAccess.setStreetCode(StringUtils.leftPad(person.vejKode, AUTHORITY_CODE_LENGTH, "0"));
			addressAccess.setStreetBuildingIdentifier(getBuildingIdentifier(person));

			AddressPostalType addressPostal = new AddressPostalType();
			addressComplete.setAddressPostal(addressPostal);

			// The following field is not included in the source
			// "CPR Registeret"
			// therefore we cannot fill the element.
			//
			// if (StringUtils.isNotBlank(""))
			// {
			// This is:
			// The given name of a farm, estate, building or dwelling, which is
			// used as a additional postal address identifier.
			//
			// addressPostal.setMailDeliverySublocationIdentifier("Fake Value");
			// }

			addressPostal.setStreetName(actualOrUnknown(person.vejnavn));

			addressPostal.setStreetNameForAddressingName(actualOrNull(person.getVejnavnTilAdressering()));

			addressPostal.setStreetBuildingIdentifier(actualOrNull(getBuildingIdentifier(person)));

			addressPostal.setFloorIdentifier(actualOrNull(person.getEtage()));

			addressPostal.setSuiteIdentifier(actualOrNull(person.sideDoerNummer));

			// Documentation says:
			//
			// Name of a village, city or subdivision of a city or district,
			// which is determined as a part of the official address
			// specification for a certain street
			// or specific parts of a street, defined by intervals of street
			// building identifiers (da: house numbers).
			//
			// We believe that the CPR term for this is 'Lokalitet'.

			addressPostal.setDistrictSubdivisionIdentifier(actualOrNull(person.lokalitet));

			// Post Box is excluded since a persons address cannot be a Post
			// Box.
			//
			// if (StringUtils.isNotBlank(""))
			// {
			// addressPostal.setPostOfficeBoxIdentifier(-1);
			// }

			addressPostal.setPostCodeIdentifier(person.postnummer);
			addressPostal.setDistrictName(person.postdistrikt);

			// FIXME: The importer does not import this field.
			// We can though figure it out using the Postal Codes.

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
		}
		else
		{
			output.setPersonAddressStructure(createFakePersonAddressStructure(UKENDT));
		}

		output.setRegularCPRPerson(regularCprPerson);

		return output;
	}


	public PersonWithHealthCareInformationStructureType map(Person person, @Nullable SikredeYderRelation sikredeYderRelation, @Nullable Yderregister yderregister, Record sikredeRecord) throws DatatypeConfigurationException
	{
		Preconditions.checkNotNull(person, "person");
		
		PersonWithHealthCareInformationStructureType personWithHealthCare = new PersonWithHealthCareInformationStructureType();

		PersonInformationStructureType personInformation = map(person, ServiceProtectionLevel.AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR);
		personWithHealthCare.setPersonInformationStructure(personInformation);

		PersonHealthCareInformationStructureType personHealthCareInformation = new PersonHealthCareInformationStructureType();
		personWithHealthCare.setPersonHealthCareInformationStructure(personHealthCareInformation);

		// Fill the associated general practitioner.
		
		AssociatedGeneralPractitionerStructureType associatedGeneralPractitioner;
		
		if (isPersonProtected(person))
		{
			associatedGeneralPractitioner = createDummyPractitioner(ADRESSEBESKYTTET);
		}
		else if (yderregister == null)
		{
			associatedGeneralPractitioner = createDummyPractitioner(UKENDT);
		}
		else
		{
			associatedGeneralPractitioner = createPractitioner(yderregister);
		}
		
		personHealthCareInformation.setAssociatedGeneralPractitionerStructure(associatedGeneralPractitioner);

		PersonPublicHealthInsuranceType personPublicHealthInsurance;
		
		if (isPersonProtected(person))
		{
			personPublicHealthInsurance = createDummyPublicHealthInsurance(ADRESSEBESKYTTET);
		}
		else if (sikredeRecord == null)
		{
			personPublicHealthInsurance = createDummyPublicHealthInsurance(UKENDT);
		}
		else
		{
			SikredeRecordToPersonPublicHealhInsuranceMapper sikredeRecordToPersonPublicHealhInsuranceMapper = new SikredeRecordToPersonPublicHealhInsuranceMapper();
            personPublicHealthInsurance = sikredeRecordToPersonPublicHealhInsuranceMapper.map(sikredeRecord);
		}
		
		personHealthCareInformation.setPersonPublicHealthInsurance(personPublicHealthInsurance);

		return personWithHealthCare;
	}


	private AssociatedGeneralPractitionerStructureType createPractitioner(Yderregister yderregister)
	{
		AssociatedGeneralPractitionerStructureType associatedGeneralPractitioner = new AssociatedGeneralPractitionerStructureType();
		associatedGeneralPractitioner.setAssociatedGeneralPractitionerIdentifier(BigInteger.valueOf(yderregister.getNummer()));
		associatedGeneralPractitioner.setAssociatedGeneralPractitionerOrganisationName(yderregister.getNavn());
		associatedGeneralPractitioner.setDistrictName(yderregister.getBynavn());
		associatedGeneralPractitioner.setEmailAddressIdentifier(yderregister.getEmail());
		associatedGeneralPractitioner.setPostCodeIdentifier(yderregister.getPostnummer());

		String address = yderregister.getVejnavn() + ", " + yderregister.getPostnummer() + " " + yderregister.getBynavn();

		associatedGeneralPractitioner.setStandardAddressIdentifier(address);
		associatedGeneralPractitioner.setTelephoneSubscriberIdentifier(yderregister.getTelefon());

		return associatedGeneralPractitioner;
	}


	private AssociatedGeneralPractitionerStructureType createDummyPractitioner(String placeholderText)
	{
		Preconditions.checkNotNull(placeholderText, "placeholderText");

		AssociatedGeneralPractitionerStructureType associatedGeneralPractitioner = new AssociatedGeneralPractitionerStructureType();
		associatedGeneralPractitioner.setAssociatedGeneralPractitionerIdentifier(BigInteger.ZERO);
		associatedGeneralPractitioner.setAssociatedGeneralPractitionerOrganisationName(placeholderText);
		associatedGeneralPractitioner.setDistrictName(placeholderText);
		associatedGeneralPractitioner.setEmailAddressIdentifier(placeholderText + "@example.com");
		associatedGeneralPractitioner.setPostCodeIdentifier("0000");

		associatedGeneralPractitioner.setStandardAddressIdentifier(placeholderText);
		associatedGeneralPractitioner.setTelephoneSubscriberIdentifier("00000000");

		return associatedGeneralPractitioner;
	}


	public PersonPublicHealthInsuranceType createPublicHealthInsurance(SikredeYderRelation sikredeYderRelation) throws DatatypeConfigurationException
	{
		PersonPublicHealthInsuranceType personPublicHealthInsurance = new PersonPublicHealthInsuranceType();

		personPublicHealthInsurance.setPublicHealthInsuranceGroupStartDate(newXMLGregorianCalendar(sikredeYderRelation.getGruppeKodeIkraftDato()));
		PublicHealthInsuranceGroupIdentifierType publicHealthInsuranceGroupIdentifier;

		char code = sikredeYderRelation.getSikringsgruppeKode();

		Preconditions.checkState(code == '1' || code == '2', "SikredeYderRelation for cpr" + sikredeYderRelation.getCpr() + " and yder " + sikredeYderRelation.getYdernummer() + " has unsupported group code " + sikredeYderRelation.getSikringsgruppeKode() + ". Cannot proceed with request");

		if (sikredeYderRelation.getSikringsgruppeKode() == '1')
		{
			publicHealthInsuranceGroupIdentifier = PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1;
		}
		else
		{
			publicHealthInsuranceGroupIdentifier = PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2;
		}

		personPublicHealthInsurance.setPublicHealthInsuranceGroupIdentifier(publicHealthInsuranceGroupIdentifier);

		return personPublicHealthInsurance;
	}


	public PersonPublicHealthInsuranceType createDummyPublicHealthInsurance(String placeholderText) throws DatatypeConfigurationException
	{
		Preconditions.checkNotNull(placeholderText, "placeholderText");

		PersonPublicHealthInsuranceType personPublicHealthInsurance = new PersonPublicHealthInsuranceType();
		personPublicHealthInsurance.setPublicHealthInsuranceGroupStartDate(newXMLGregorianCalendar(new Date(0)));
		personPublicHealthInsurance.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1);

		return personPublicHealthInsurance;
	}


	private String actualOrUnknown(String actual)
	{
		return !StringUtils.isBlank(actual) ? actual : UKENDT;
	}
	

	private String actualOrNull(String actual)
	{
		return !StringUtils.isBlank(actual) ? actual : null;
	}


	private PersonAddressStructureType createFakePersonAddressStructure(String placeholderText)
	{
		PersonAddressStructureType personAddress = new PersonAddressStructureType();
		personAddress.setPersonInformationProtectionStartDate(null);

		personAddress.setCountyCode("0000");

		AddressCompleteType addressComplete = new AddressCompleteType();

		AddressAccessType addressAccess = new AddressAccessType();

		addressAccess.setMunicipalityCode("0000");
		addressAccess.setStreetCode("0000");
		addressAccess.setStreetBuildingIdentifier("1");

		addressComplete.setAddressAccess(addressAccess);

		AddressPostalType addressPostal = new AddressPostalType();
		addressComplete.setAddressPostal(addressPostal);

		addressPostal.setStreetName(placeholderText);

		addressPostal.setStreetBuildingIdentifier("1");

		addressPostal.setPostCodeIdentifier("0000");
		addressPostal.setDistrictName(placeholderText);

		personAddress.setAddressComplete(addressComplete);

		return personAddress;
	}


	private void mapCurrentPersonCivilRegistrationIdentifier(Person person, PersonInformationStructureType output)
	{
		if (StringUtils.isNotBlank(person.getGaeldendeCPR()))
		{
			output.setCurrentPersonCivilRegistrationIdentifier(person.getGaeldendeCPR());
		}
	}


	private String getBuildingIdentifier(Person person)
	{
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

		// We have to make the guard above to avoid null being passed into the
		// Instant
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


	public static XMLGregorianCalendar newXMLGregorianCalendar(Date date)
	{
		DatatypeFactory factory = null;
		try {
			factory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return factory.newXMLGregorianCalendar(new DateTime(date).toGregorianCalendar());
	}
}
