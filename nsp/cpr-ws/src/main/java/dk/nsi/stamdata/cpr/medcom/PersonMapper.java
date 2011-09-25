package dk.nsi.stamdata.cpr.medcom;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.trifork.stamdata.models.cpr.Person;

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

public class PersonMapper
{
    public PersonInformationStructureType map(Person person) throws DatatypeConfigurationException
    {
        PersonInformationStructureType personInformation = new ObjectFactory().createPersonInformationStructureType();
        personInformation.setCurrentPersonCivilRegistrationIdentifier(person.cpr);

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

        regularCprPerson.setPersonNameForAddressingName(person.fornavn); // FIXME: Is this correct. This might be missing for the importer.

        if ("M".equalsIgnoreCase(person.koen))
        {
            regularCprPerson.setPersonGenderCode(PersonGenderCodeType.MALE);
        }
        else if ("K".equalsIgnoreCase(person.koen))
        {
            regularCprPerson.setPersonGenderCode(PersonGenderCodeType.FEMALE);
        }
        else
        {
            regularCprPerson.setPersonGenderCode(PersonGenderCodeType.UNKNOWN);
        }

        regularCprPerson.setPersonInformationProtectionIndicator(true); // FIXME: Does this mean that there is a start date element later or that one is active?

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
        personInformation.setPersonAddressStructure(personAddress);

        personInformation.setRegularCPRPerson(regularCprPerson);
        return personInformation;
    }


    private XMLGregorianCalendar newXMLGregorianCalendar(Date date) throws DatatypeConfigurationException
    {
        DatatypeFactory factory = DatatypeFactory.newInstance();
        return factory.newXMLGregorianCalendar(new DateTime(date).toGregorianCalendar());
    }
}
