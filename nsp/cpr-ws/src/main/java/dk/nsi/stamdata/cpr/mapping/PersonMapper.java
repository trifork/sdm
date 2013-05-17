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
package dk.nsi.stamdata.cpr.mapping;

import com.trifork.stamdata.Preconditions;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.security.WhitelistService;
import dk.oio.rep.cpr_dk.xml.schemas.core._2005._11._24.PersonBirthDateStructureType;
import dk.oio.rep.cpr_dk.xml.schemas.core._2006._01._17.PersonCivilRegistrationStatusStructureType;
import dk.oio.rep.cpr_dk.xml.schemas.core._2006._01._17.RegularCPRPersonType;
import dk.oio.rep.cpr_dk.xml.schemas.core._2006._01._17.SimpleCPRPersonType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.PersonGenderCodeType;
import dk.oio.rep.itst_dk.xml.schemas._2006._01._17.PersonNameStructureType;
import dk.oio.rep.medcom_sundcom_dk.xml.schemas._2007._02._01.AssociatedGeneralPractitionerStructureType;
import dk.sosi.seal.model.SystemIDCard;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Date;

import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;

/**
 * Contains mapping functions that are shared between wsdl versions
 */
public class PersonMapper {

    public static enum ServiceProtectionLevel {
        AlwaysCensorProtectedData,
        CensorProtectedDataForNonAuthorities
    }

    public static enum CPRProtectionLevel {
        CensorCPR,
        DoNotCensorCPR
    }

    protected static final String UKENDT = "UKENDT";
    protected static final int AUTHORITY_CODE_LENGTH = 4;
    protected static final String SERVICE_NAME_DGCPR = WhitelistService.DEFAULT_SERVICE_NAME;
    protected WhitelistService whitelistService;

    protected static final String ADRESSEBESKYTTET = "ADRESSEBESKYTTET";
    protected static final String PROTECTED_CPR = "0000000000";
    protected final SystemIDCard idCard;
    protected final MunicipalityMapper munucipalityMapper;

    public PersonMapper(WhitelistService whitelistService, SystemIDCard idCard, MunicipalityMapper munucipalityMapper) {
        // Once we get this far the filter should have gotten rid of id cards
        // that are not CVR authenticated System ID Cards.
        this.whitelistService = whitelistService;
        this.idCard = idCard;
        this.munucipalityMapper = munucipalityMapper;
    }

    protected boolean isClientAnAuthority() {
        String careProviderType = idCard.getSystemInfo().getCareProvider().getType();
        Preconditions.checkState(CVR_NUMBER.equals(careProviderType), "ID Card Care provider is not a CVR. This is a programming error.");
        String clientCVR = idCard.getSystemInfo().getCareProvider().getID();
        return whitelistService.isCvrWhitelisted(clientCVR, SERVICE_NAME_DGCPR);
    }

    protected boolean isPersonProtected(Person person) {
        if (person.getNavnebeskyttelsestartdato() == null) return false;

        // We have to make the guard above to avoid null being passed into the
        // Instant
        // it is converted to the beginning of the era.

        Preconditions.checkState(person.getNavnebeskyttelseslettedato() != null, "The protection end date was not present. This is most unexpected and a programming error.");

        Instant protectionStart = new Instant(person.getNavnebeskyttelsestartdato());
        Instant protectionEnd = new Instant(person.getNavnebeskyttelseslettedato());

        return protectionStart.isEqualNow() || (protectionStart.isBeforeNow() && protectionEnd.isAfterNow());
    }

    protected PersonGenderCodeType mapGenderToGenderCode(String genderString) {
        if ("M".equalsIgnoreCase(genderString)) {
            return PersonGenderCodeType.MALE;
        } else if ("K".equalsIgnoreCase(genderString)) {
            return PersonGenderCodeType.FEMALE;
        } else {
            return PersonGenderCodeType.UNKNOWN;
        }
    }

    protected RegularCPRPersonType createRegularPerson(Person person) {
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
        return regularCprPerson;
    }

    protected RegularCPRPersonType createCensoredRegularPerson(String cpr, CPRProtectionLevel cprProtection) {
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
        simpleCprPerson.setPersonCivilRegistrationIdentifier(isCPRHidden ? PROTECTED_CPR : cpr);

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

        return regularCprPerson;
    }

    protected AssociatedGeneralPractitionerStructureType createDummyPractitioner(String placeholderText)
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


    //////////////////////////////////////////////////

    protected String actualOrUnknown(String actual)
    {
        return !StringUtils.isBlank(actual) ? actual : UKENDT;
    }

    protected String actualOrNull(String actual)
    {
        return !StringUtils.isBlank(actual) ? actual : null;
    }

    public static XMLGregorianCalendar newXMLGregorianCalendar(Date date)
    {
        DatatypeFactory factory;
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        return factory.newXMLGregorianCalendar(new DateTime(date).toGregorianCalendar());
    }

}
