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
import static dk.nsi.stamdata.jaxws.generated.PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1;
import static dk.nsi.stamdata.jaxws.generated.PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.persistence.Record;

import dk.nsi.stamdata.cpr.mapping.MunicipalityMapper;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.jaxws.generated.AssociatedGeneralPractitionerStructureType;
import dk.nsi.stamdata.jaxws.generated.PersonPublicHealthInsuranceType;
import dk.nsi.stamdata.jaxws.generated.PersonWithHealthCareInformationStructureType;
import dk.nsi.stamdata.testing.MockSecureTokenService;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.SystemIDCard;


public class PersonHealthCareInfoMapperTest
{
    private static final String NOT_WHITELISTED_CVR = "99999999";
    private static final String ADRESSEBESKYTTET = "ADRESSEBESKYTTET";
    private static final String UKENDT = "UKENDT";

    private Person person;
    private Record sikredeRecord;
    private Record yderRecord;

    private PersonWithHealthCareInformationStructureType output;


    @Before
    public void setUp()
    {
        person = Factories.createPerson();
        yderRecord = Factories.createYderRecord("1234");
        sikredeRecord = Factories.createSikredeRecordFor(person, yderRecord, "2", new DateTime(Factories.YESTERDAY));
    }

    private void doMapping() throws DatatypeConfigurationException
    {
        MunicipalityMapper municipalityMapper = new MunicipalityMapper();
        SystemIDCard idCard = MockSecureTokenService.createSignedSystemIDCard(NOT_WHITELISTED_CVR, AuthenticationLevel.VOCES_TRUSTED_SYSTEM);

        output = new PersonMapper(new StubWhitelistService(Collections.<String>emptyList()), idCard, municipalityMapper).map(person, sikredeRecord, yderRecord);
    }


    @Test
    public void itInsertsRealDataIfNoneAreMissing() throws DatatypeConfigurationException
    {
        doMapping();

        assertThatRelationIsRealData();
        assertThatGeneralPractitionerIsRealData();
    }


    @Test
    public void itInsertsDummyDataIfTheYderDataIsMissing() throws DatatypeConfigurationException
    {
        yderRecord = null;

        doMapping();

        assertThatRelationIsRealData();
        assertThatGeneralPractitionerIsDummy(UKENDT);
    }


    @Test
    public void itInsertsDummyDataIfThePersonIsProtectedAndDataIsMissing() throws DatatypeConfigurationException
    {
        person = Factories.createPersonWithAddressProtection();
        yderRecord = null;
        sikredeRecord = null;

        doMapping();

        assertThatGeneralPractitionerIsDummy(ADRESSEBESKYTTET);
        assertThatRelationIsDummy();
    }


    @Test
    public void itInsertsDummyDataIfPersonIsProtected() throws DatatypeConfigurationException
    {
        person = Factories.createPersonWithAddressProtection();

        doMapping();

        assertThatRelationIsDummy();
        assertThatGeneralPractitionerIsDummy(ADRESSEBESKYTTET);
    }


    @Test
    public void itInsertsDummyDataIfBothAreMissing() throws DatatypeConfigurationException
    {
        sikredeRecord = null;
        yderRecord = null;

        doMapping();

        assertThatRelationIsDummy();
        assertThatGeneralPractitionerIsDummy(UKENDT);
    }


    private void assertThatGeneralPractitionerIsDummy(String placeholderText)
    {
        AssociatedGeneralPractitionerStructureType g = output.getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure();

        assertThat(g.getAssociatedGeneralPractitionerOrganisationName(), is(placeholderText));
        assertThat(g.getDistrictName(), is(placeholderText));
        assertThat(g.getStandardAddressIdentifier(), is(placeholderText));
        assertThat(g.getAssociatedGeneralPractitionerIdentifier(), is(BigInteger.ZERO));
        assertThat(g.getTelephoneSubscriberIdentifier(), is("00000000"));
        assertThat(g.getPostCodeIdentifier(), is("0000"));
        assertThat(g.getEmailAddressIdentifier(), is(placeholderText + "@example.com"));
    }


    private void assertThatGeneralPractitionerIsRealData()
    {
        AssociatedGeneralPractitionerStructureType g = output.getPersonHealthCareInformationStructure().getAssociatedGeneralPractitionerStructure();

        assertThat(g.getAssociatedGeneralPractitionerOrganisationName(), is(yderRecord.get("PrakBetegn")));
        assertThat(g.getDistrictName(), is(yderRecord.get("PostdistYder")));
        assertThat(g.getStandardAddressIdentifier(), is(yderRecord.get("AdrYder")));
        assertThat(g.getAssociatedGeneralPractitionerIdentifier(), is(new BigInteger((String) yderRecord.get("YdernrYder"))));
        assertThat(g.getTelephoneSubscriberIdentifier(), is(yderRecord.get("HvdTlf")));
        assertThat(g.getPostCodeIdentifier(), is(yderRecord.get("PostnrYder")));
        assertThat(g.getEmailAddressIdentifier(), is(yderRecord.get("EmailYder")));
    }


    private void assertThatRelationIsDummy() throws DatatypeConfigurationException
    {
        PersonPublicHealthInsuranceType g = output.getPersonHealthCareInformationStructure().getPersonPublicHealthInsurance();

        assertThat(g.getPublicHealthInsuranceGroupIdentifier(), is(SYGESIKRINGSGRUPPE_1));
        assertThat(g.getPublicHealthInsuranceGroupStartDate(), is(newXMLGregorianCalendar(new Date(0))));
    }


    private void assertThatRelationIsRealData() throws DatatypeConfigurationException
    {
        PersonPublicHealthInsuranceType g = output.getPersonHealthCareInformationStructure().getPersonPublicHealthInsurance();

        assertThat(g.getPublicHealthInsuranceGroupIdentifier(), is(SYGESIKRINGSGRUPPE_2));
        assertThat(g.getPublicHealthInsuranceGroupStartDate().toGregorianCalendar().getTime(), is(YESTERDAY));
    }
}
