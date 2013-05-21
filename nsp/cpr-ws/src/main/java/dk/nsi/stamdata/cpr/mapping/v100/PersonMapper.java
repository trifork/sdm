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
package dk.nsi.stamdata.cpr.mapping.v100;

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import dk.nsi.stamdata.cpr.mapping.*;
import dk.nsi.stamdata.security.WhitelistService;
import dk.oio.rep.cpr_dk.xml.schemas.core._2006._01._17.RegularCPRPersonType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationCodeType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationSchemeType;
import dk.oio.rep.medcom_sundcom_dk.xml.schemas._2007._02._01.*;
import dk.oio.rep.xkom_dk.xml.schemas._2005._03._15.AddressAccessType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressCompleteType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressPostalType;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.persistence.Record;

import dk.nsi.stamdata.cpr.models.Person;

import dk.sosi.seal.model.SystemIDCard;

/**
 * Person mapper for wsdl version 1.0.0
 */
@RequestScoped
public class PersonMapper extends dk.nsi.stamdata.cpr.mapping.PersonMapper
{

	@Inject
    public PersonMapper(WhitelistService whitelistService, SystemIDCard idCard, MunicipalityMapper munucipalityMapper) {
        super(whitelistService, idCard, munucipalityMapper);
	}

	public PersonInformationStructureType map(Person person, ServiceProtectionLevel protectionLevel, CPRProtectionLevel cprProtection) {
		boolean censorData = isPersonProtected(person) &&
                (protectionLevel == ServiceProtectionLevel.AlwaysCensorProtectedData || !isClientAnAuthority());
		return (censorData) ? createOutputWithCensoredDate(person, cprProtection) : createOutputWithRealDate(person);
	}

	private PersonInformationStructureType createOutputWithCensoredDate(Person person, CPRProtectionLevel cprProtection) {
		PersonInformationStructureType output = new ObjectFactory().createPersonInformationStructureType();
		mapCurrentPersonCivilRegistrationIdentifier(person, output);

        RegularCPRPersonType regularCprPerson = createCensoredRegularPerson(person.getCpr(), cprProtection);

		// PERSON ADDRESS
		output.setPersonAddressStructure(createFakePersonAddressStructure(ADRESSEBESKYTTET));
		output.getPersonAddressStructure().setPersonInformationProtectionStartDate(newXMLGregorianCalendar(person.getNavnebeskyttelsestartdato()));

		output.setRegularCPRPerson(regularCprPerson);

		return output;
	}


	private PersonInformationStructureType createOutputWithRealDate(Person person) {
		// There are many cases in which we cannot fulfill the output format
		// just by using data present in the db. Therefore we have to 'fill'
		// in the gaps with e.g. 'UNKNOWN'.

		PersonInformationStructureType output = new ObjectFactory().createPersonInformationStructureType();
		mapCurrentPersonCivilRegistrationIdentifier(person, output);

		RegularCPRPersonType regularCprPerson = createRegularPerson(person);

		// PERSON ADDRESS
		//
		// There are also records for which the record's values should not be
		// used. For instance if a person is dead or missing his address should
		// not be filled in. Yet the output schema requires them to be there.

		boolean shouldIncludeAddress = !CivilRegistrationStatusCodes.STATUSES_WITH_NO_ADDRESS.contains(person.getStatus());

		if (shouldIncludeAddress) {
			PersonAddressStructureType personAddress = new PersonAddressStructureType();

			if (person.navnebeskyttelsestartdato != null) {
				personAddress.setPersonInformationProtectionStartDate(newXMLGregorianCalendar(person.navnebeskyttelsestartdato));
			}

			// The output requires that the authority codes (municipality and
			// county) are exactly four char long.

			AddressCompleteType addressComplete = new AddressCompleteType();
			AddressAccessType addressAccess = new AddressAccessType();
			addressComplete.setAddressAccess(addressAccess);

			if (StringUtils.isNotBlank(person.kommuneKode)) {
				String municipalityCode = StringUtils.leftPad(person.kommuneKode, AUTHORITY_CODE_LENGTH, "0");

				addressAccess.setMunicipalityCode(municipalityCode);
				personAddress.setCountyCode(munucipalityMapper.toCountyCode(municipalityCode));
			} else {
				addressAccess.setMunicipalityCode("0000");
				personAddress.setCountyCode("0000");
			}

			personAddress.setCareOfName(actualOrNull(person.coNavn));

			addressAccess.setStreetCode(StringUtils.leftPad(person.vejKode, AUTHORITY_CODE_LENGTH, "0"));
			addressAccess.setStreetBuildingIdentifier(person.husnummer);

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

			addressPostal.setStreetBuildingIdentifier(actualOrNull(person.husnummer));

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

			addressPostal.setPostCodeIdentifier(person.postnummer);
			addressPostal.setDistrictName(person.postdistrikt);

			// FIXME: The importer does not import this field.
			// We can though figure it out using the Postal Codes.

			if (StringUtils.isNotBlank("")) {
				CountryIdentificationCodeType country = new CountryIdentificationCodeType();

				// Two alpha-numerical characters.
				country.setScheme(CountryIdentificationSchemeType.ISO_3166_ALPHA_2);
				country.setValue("DK");
				addressPostal.setCountryIdentificationCode(country);
			}

			personAddress.setAddressComplete(addressComplete);
			output.setPersonAddressStructure(personAddress);
		} else {
			output.setPersonAddressStructure(createFakePersonAddressStructure(UKENDT));
		}

		output.setRegularCPRPerson(regularCprPerson);

		return output;
	}


	public PersonWithHealthCareInformationStructureType map(Person person, @Nullable Record sikredeRecord, @Nullable Record yderRecord) throws DatatypeConfigurationException {
		Preconditions.checkNotNull(person, "person");
		
		PersonWithHealthCareInformationStructureType personWithHealthCare = new PersonWithHealthCareInformationStructureType();

		PersonInformationStructureType personInformation = map(person, ServiceProtectionLevel.AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR);
		personWithHealthCare.setPersonInformationStructure(personInformation);

		PersonHealthCareInformationStructureType personHealthCareInformation = new PersonHealthCareInformationStructureType();
		personWithHealthCare.setPersonHealthCareInformationStructure(personHealthCareInformation);

		// Fill the associated general practitioner.
		
		AssociatedGeneralPractitionerStructureType associatedGeneralPractitioner;
		
		if (isPersonProtected(person)) {
			associatedGeneralPractitioner = createDummyPractitioner(ADRESSEBESKYTTET);
		} else if (yderRecord == null) {
			associatedGeneralPractitioner = createDummyPractitioner(UKENDT);
		} else {
		    YderregisterRecordToAssociatedGeneralPractitionerMapper yderregisterRecordToAssociatedGeneralPractitionerMapper = new YderregisterRecordToAssociatedGeneralPractitionerMapper();
		    associatedGeneralPractitioner = yderregisterRecordToAssociatedGeneralPractitionerMapper.map(yderRecord);
		}
		
		personHealthCareInformation.setAssociatedGeneralPractitionerStructure(associatedGeneralPractitioner);
		PersonPublicHealthInsuranceType personPublicHealthInsurance;
		if (isPersonProtected(person)) {
			personPublicHealthInsurance = createDummyPublicHealthInsurance(ADRESSEBESKYTTET);
		} else if (sikredeRecord == null) {
			personPublicHealthInsurance = createDummyPublicHealthInsurance(UKENDT);
		} else {
			SikredeRecordToPersonPublicHealthInsuranceMapper sikredeRecordToPersonPublicHealhInsuranceMapper = new SikredeRecordToPersonPublicHealthInsuranceMapper();
            personPublicHealthInsurance = sikredeRecordToPersonPublicHealhInsuranceMapper.map(sikredeRecord);
		}
		
		personHealthCareInformation.setPersonPublicHealthInsurance(personPublicHealthInsurance);
		return personWithHealthCare;
	}


	public PersonPublicHealthInsuranceType createDummyPublicHealthInsurance(String placeholderText) throws DatatypeConfigurationException
	{
		Preconditions.checkNotNull(placeholderText, "placeholderText");

		PersonPublicHealthInsuranceType personPublicHealthInsurance = new PersonPublicHealthInsuranceType();
		personPublicHealthInsurance.setPublicHealthInsuranceGroupStartDate(newXMLGregorianCalendar(new Date(0)));
		personPublicHealthInsurance.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1);

		return personPublicHealthInsurance;
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


	private void mapCurrentPersonCivilRegistrationIdentifier(Person person, PersonInformationStructureType output) {
		if (StringUtils.isNotBlank(person.getGaeldendeCPR())) {
			output.setCurrentPersonCivilRegistrationIdentifier(person.getGaeldendeCPR());
		}
	}

}
