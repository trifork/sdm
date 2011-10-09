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

import static dk.nsi.stamdata.cpr.Factories.YESTERDAY;
import static dk.nsi.stamdata.cpr.PersonMapper.newXMLGregorianCalendar;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Sets;

import dk.nsi.stamdata.cpr.PersonMapper.CPRProtectionLevel;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.mapping.CivilRegistrationStatusCodes;
import dk.nsi.stamdata.cpr.mapping.MunicipalityMapper;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.ws.PersonGenderCodeType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.sosi.seal.model.SystemIDCard;


public class PersonMapperFieldMappingForUnprotectedPersonTest
{
    private SystemIDCard idCard;
    private Person person;
    private PersonInformationStructureType output;

    private static final String VIBORG = "0791";
    private static final String REGION_MIDTJYLLAND = "1082";


    @Before
    public void setUp() throws Exception
    {
        person = Factories.createPerson();

        doMap();
    }


    @Test
    public void mapsUnknownFieldToUkendtString() throws Exception
    {
        person.fornavn = null;

        doMap();

        assertThat(output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName(), is("UKENDT"));
        assertThat(output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonSurnameName(), is(person.efternavn));
    }


    @Test
    public void dontOutputAddressesForTheDeadOrPeopleThatAreInOneOfTheOtherNoAddressCatagories() throws Exception
    {
        for (String statusCode : CivilRegistrationStatusCodes.STATUSES_WITH_NO_ADDRESS)
        {
            person.setStatus(statusCode);

            doMap();

            assertThat(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetName(), is("UKENDT"));
        }
    }


    @Test
    public void mapsGaeldendeToCprCurrentPersonCivilRegistrationIdentifier() throws Exception
    {
        assertEquals(person.getGaeldendeCPR(), output.getCurrentPersonCivilRegistrationIdentifier());
    }


    @Test
    public void mapCivilRegistrationCode() throws Exception
    {
        assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusCode(), is(new BigInteger("01")));
    }


    @Test
    public void mapCivilRegistrationCodeStartDate() throws Exception
    {
        assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusStartDate(), is(newXMLGregorianCalendar(YESTERDAY)));
    }


    @Test
    public void mapNameForAddressingName() throws Exception
    {
        assertThat(output.getRegularCPRPerson().getPersonNameForAddressingName(), is("Peter,Andersen"));
    }


    @Test
    public void alwaysUses02ForPersonCivilRegistrationStatusCode()
    {
        assertThat(output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusCode(), is(new BigInteger("01")));
    }


    @Test
    public void mapStreetNameForAddressingName() throws Exception
    {
        assertThat(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetNameForAddressingName(), is("Østergd."));
    }


    @Test
    public void mapsFornavnToGivenName() throws Exception
    {
        assertEquals(person.getFornavn(), output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonGivenName());
    }


    @Test
    public void mapsMellemnavnToMiddleName() throws Exception
    {
        assertEquals(person.getMellemnavn(), output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonMiddleName());
    }


    @Test
    public void mapsEfternavnToSurname() throws Exception
    {
        assertEquals(person.getEfternavn(), output.getRegularCPRPerson().getSimpleCPRPerson().getPersonNameStructure().getPersonSurnameName());
    }


    @Test
    public void mapsCprToPersonCivilRegistrationIdentifier() throws Exception
    {
        assertEquals(person.getCpr(), output.getRegularCPRPerson().getSimpleCPRPerson().getPersonCivilRegistrationIdentifier());
    }


    @Test
    public void mapsKoenMToGenderCodeMale() throws Exception
    {
        person.setKoen("M");
        doMap();

        assertEquals(PersonGenderCodeType.MALE, output.getRegularCPRPerson().getPersonGenderCode());
    }


    @Test
    public void mapsKoenKToGenderCodeFemale() throws Exception
    {
        person.setKoen("K");
        doMap();

        assertEquals(PersonGenderCodeType.FEMALE, output.getRegularCPRPerson().getPersonGenderCode());
    }


    @Test
    public void mapsKoenEmptystringToGenderCodeUnknown() throws Exception
    {
        person.setKoen("");
        doMap();

        assertEquals(PersonGenderCodeType.UNKNOWN, output.getRegularCPRPerson().getPersonGenderCode());
    }


    @Test
    public void mapsKoenSinglespaceToGenderCodeUnknown() throws Exception
    {
        person.setKoen(" ");
        doMap();

        assertEquals(PersonGenderCodeType.UNKNOWN, output.getRegularCPRPerson().getPersonGenderCode());
    }


    @Test
    public void mapsKoenSomeSingleletterCodeToGenderCodeUnknown() throws Exception
    {
        person.setKoen("X");
        doMap();

        assertEquals(PersonGenderCodeType.UNKNOWN, output.getRegularCPRPerson().getPersonGenderCode());
    }


    @Test
    public void mapsFoedselsdatoToBirthdate() throws Exception
    {
        assertEquals(person.getFoedselsdato(), output.getRegularCPRPerson().getPersonBirthDateStructure().getBirthDate().toGregorianCalendar().getTime());
    }


    @Test
    public void mapsCoNavnToCareOfName() throws Exception
    {
        assertEquals(person.getCoNavn(), output.getPersonAddressStructure().getCareOfName());
    }


    @Test
    public void mapsKommuneKodeToMunicipalityCode() throws Exception
    {
        assertEquals(person.getKommuneKode(), output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getMunicipalityCode());
    }


    @Test
    public void mapsVejkodeToStreetCode() throws Exception
    {
        assertEquals(person.getVejKode(), output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetCode());
    }


    @Test
    public void mapsHusnummerAndBygningsnummerToStreetBuildingIdentifierInAddressAccess() throws Exception
    {
        assertEquals(person.getHusnummer() + person.getBygningsnummer(), output.getPersonAddressStructure().getAddressComplete().getAddressAccess().getStreetBuildingIdentifier());
    }


    @Test
    public void mapsHusnummerAndBygningsnummerToStreetBuildingIdentifierInAddressPostal() throws Exception
    {
        assertEquals(person.getHusnummer() + person.getBygningsnummer(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetBuildingIdentifier());
    }


    @Test
    @Ignore("Is anything mapped to Maildelivery Subdivision identifier? Lokalitet is not, it's mapped to DistrictSubdivisionIdentifier")
    public void mapsLokalitetToMailDeliverySublocationIdentifier() throws Exception
    {

        // From the xsd's:
        //
        // DistrictSubdivisionIdentifier:
        // Name of a village, city or subdivision of a city or district, which
        // is determined as a part of the
        // official address specification for a certain street or specific parts
        // of a street, defined by intervals
        // of street building identifiers (da: house numbers).
        //
        // MailDeliverySublocationIdentifier:
        // The given name of a farm, estate, building or dwelling, which is used
        // as a additional postal address
        // identifier.

        assertEquals(person.getLokalitet(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getMailDeliverySublocationIdentifier());

    }


    @Test
    public void mapsVejnavnToStreetName() throws Exception
    {
        assertEquals(person.getVejnavn(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetName());
    }


    @Test
    public void mapsVejnavnTilAddresseringToStreetnameForAddressingName() throws Exception
    {
        assertEquals(person.getVejnavnTilAdressering(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getStreetNameForAddressingName());
    }


    @Test
    public void mapsEtageToFloorIdentifier() throws Exception
    {
        assertEquals(person.getEtage(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getFloorIdentifier());
    }


    @Test
    public void mapsSideDoerNummerToSuiteIdentifier() throws Exception
    {
        assertEquals(person.getSideDoerNummer(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getSuiteIdentifier());
    }


    @Test
    public void mapsFoedselsdatoMarkeringToBirthdateUncertaintyIndicator() throws Exception
    {
        assertEquals(person.getFoedselsdatoMarkering(), output.getRegularCPRPerson().getPersonBirthDateStructure().isBirthDateUncertaintyIndicator());
    }


    @Test
    public void mapsStatusToPersonCivilRegistrationStatusCode() throws Exception
    {
        assertEquals(new BigInteger(person.getStatus()), output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusCode());
    }


    @Test
    public void mapsStatusDatoToPersonCivilRegistrationStatusStartDate() throws Exception
    {
        assertEquals(person.getStatusDato(), output.getRegularCPRPerson().getPersonCivilRegistrationStatusStructure().getPersonCivilRegistrationStatusStartDate().toGregorianCalendar().getTime());
    }


    @Test
    public void mapsPostnummerToPostCodeIdentifier() throws Exception
    {
        assertEquals(person.getPostnummer(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getPostCodeIdentifier());
    }


    @Test
    public void mapsPostdistriktToDistrictName() throws Exception
    {
        assertEquals(person.getPostdistrikt(), output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getDistrictName());
    }


    @Test
    public void omitsNavnebeskyttelsestartdatoToPersonInformationProtectionStartDate() throws Exception
    {
        assertNull(output.getPersonAddressStructure().getPersonInformationProtectionStartDate());
    }


    @Test
    public void usesFalseForPersonInformationProtectionIndicator() throws Exception
    {
        assertFalse(output.getRegularCPRPerson().isPersonInformationProtectionIndicator());
    }


    @Test
    public void mapsLokalitetToDistrictSubdivisionIdentifier() throws Exception
    {
        assertEquals(person.lokalitet, output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getDistrictSubdivisionIdentifier());
    }


    @Test
    public void nothingIsMappedToPostOfficeBoxIdentifier() throws Exception
    {
        // Just to document the fact
        assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getPostOfficeBoxIdentifier());
    }


    @Test
    public void nothingIsMappedToCountryIdentificationCode()
    {
        // Just to document the fact
        assertNull(output.getPersonAddressStructure().getAddressComplete().getAddressPostal().getCountryIdentificationCode());
    }


    @Test
    public void theMunicipalityIsMappedToTheCorrectCounty() throws Exception
    {
        person.kommuneKode = VIBORG;

        doMap();

        assertThat(output.getPersonAddressStructure().getCountyCode(), is(REGION_MIDTJYLLAND));
    }


    private void doMap() throws Exception
    {

        Set<String> whiteList = Sets.newHashSet();
        MunicipalityMapper municipalityMapper = new MunicipalityMapper();
        PersonMapper personMapper = new PersonMapper(whiteList, idCard, municipalityMapper);

        output = personMapper.map(person, ServiceProtectionLevel.AlwaysCensorProtectedData, CPRProtectionLevel.DoNotCensorCPR);
    }
}
