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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import dk.nsi.stamdata.cpr.mapping.v100.PersonMapper;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.PersonGenderCodeType;
import dk.oio.rep.medcom_sundcom_dk.xml.schemas._2007._02._01.PersonInformationStructureType;
import org.junit.Before;
import org.junit.Test;

import dk.nsi.stamdata.cpr.mapping.PersonMapper.CPRProtectionLevel;
import dk.nsi.stamdata.cpr.mapping.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.mapping.MunicipalityMapper;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.testing.MockSecureTokenService;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.SystemIDCard;


public class PersonMapperFieldMappingForProtectedPersonTest {
    private static final String ADRESSEBESKYTTET = "ADRESSEBESKYTTET";
    private Person person;
    private PersonInformationStructureType output;


    @Before
    public void setUp() throws Exception {
        person = Factories.createPersonWithAddressProtection();

        doMap();
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
    public void mapsKoenMToGenderCodeUnknown() throws Exception {
        person.setKoen("M");
        doMap();

        assertEquals(PersonGenderCodeType.UNKNOWN, output.getRegularCPRPerson().getPersonGenderCode());
    }


    @Test
    public void mapsKoenKToGenderCodeFemale() throws Exception {
        person.setKoen("K");
        doMap();

        assertEquals(PersonGenderCodeType.UNKNOWN, output.getRegularCPRPerson().getPersonGenderCode());
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
        assertThat(output.getRegularCPRPerson().getPersonBirthDateStructure().getBirthDate().toGregorianCalendar().getTime(), is(not(person.getFoedselsdato())));
    }


    @Test
    public void omitsCareOfName() throws Exception {
        assertNull(output.getPersonAddressStructure().getCareOfName());
    }


    @Test
    public void uses0000ForMunicipalityCode() throws Exception {
        assertEquals("0000", output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getMunicipalityCode());
    }


    @Test
    public void uses0000ForStreetCode() throws Exception {
        assertEquals("0000", output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetCode());
    }


    @Test
    public void uses1ForStreetBuildingIdentifierInAddressAccess() throws Exception {
        assertEquals("1", output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetBuildingIdentifier());
    }


    @Test
    public void uses1ForStreetBuildingIdentifierInAddressPostal() throws Exception {
        assertEquals("1", output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetBuildingIdentifier());
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
    public void usesTrueForBirthdateUncertaintyIndicator() throws Exception {
        assertTrue(output.getRegularCPRPerson().getPersonBirthDateStructure().isBirthDateUncertaintyIndicator());
    }


    @Test
    public void uses0000ForPostCodeIdentifier() throws Exception {
        assertEquals("0000", output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getPostCodeIdentifier());
    }


    @Test
    public void usesAdressebeskyttetForDistrictName() throws Exception {
        assertEquals(ADRESSEBESKYTTET, output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getDistrictName());
    }


    @Test
    public void usesPersonInformationProtectionStartDate() throws Exception {
        assertThat(output.getPersonAddressStructure().getPersonInformationProtectionStartDate().toGregorianCalendar().getTime(), is(person.getNavnebeskyttelsestartdato()));
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
        assertThat(output.getPersonAddressStructure().getCountyCode(), is("0000"));
    }


    private void doMap() throws Exception {
        SystemIDCard idCard = MockSecureTokenService.createSignedSystemIDCard("12345678", AuthenticationLevel.VOCES_TRUSTED_SYSTEM);
        MunicipalityMapper municipalityMapper = new MunicipalityMapper();

        PersonMapper personMapper = new PersonMapper(new StubWhitelistService(Collections.<String>emptyList()), idCard, municipalityMapper);

        output = personMapper.map(person, ServiceProtectionLevel.AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR);
    }
}
